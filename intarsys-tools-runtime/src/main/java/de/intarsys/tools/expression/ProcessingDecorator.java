/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.expression;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorInternalException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.IFunctorRegistry;
import de.intarsys.tools.string.StringTools;

/**
 * A decorating {@link IStringEvaluator} to add result processing support to an
 * other embedded {@link IStringEvaluator}. The command to be performed is
 * separated from the expression to be processed by a ":" character.
 * <p>
 * You can suffix the original expression by as many commands as you like, for
 * example
 * 
 * <pre>
 * my ${var:*:dts}
 * </pre>
 * 
 * will recursivly expand "var" and apply a conversion to a short time format
 * afterwards.
 * 
 * <p>
 * Currently we support processing the evaluated result with the following
 * command characters:
 * <dl>
 * <dt>?[expression]</dt>
 * <dd>return the evaluated result if expression is true, null otherwise.
 * Currently only expressions representing access to boolean arguments are
 * supported</dd>
 * <dt>.</dt>
 * <dd>reflective access to the value returned by the decorated evaluation</dd>
 * <dt>#</dt>
 * <dd>perform the named functor (action) on the result</dd>
 * <dt>*</dt>
 * <dd>perform evaluation on the result until it is no longer changed (or
 * null)</dd>
 * <dt>[char][formatting options]</dt>
 * <dd>perform the string formatting selected by the "char"</dd>
 * <dt>[empty]</dt>
 * <dd>perform the default string formatting</dd>
 * </dl>
 */
public class ProcessingDecorator implements IStringEvaluator {

	public static final char PROCESSING_SEPARATOR = ':';

	public static final String ARG_SEPARATOR = ","; //$NON-NLS-1$

	public static final char CLOSE_BRACE = ')';

	public static final char OPEN_BRACE = '(';

	public static final char CODE_REFLECTION = '.';

	public static final char CODE_FUNCTOR = '#';

	public static final char CODE_DEEPRECURSION = '*';

	public static final char CODE_SHALLOWRECURSION = '+';

	public static final char CODE_CONDITIONAL = '?';

	public static final char CODE_DEFAULTVALUE = '!';

	private static IFunctorRegistry FormattingFunctors;

	private static final String ARG_RECURSION = "de.intarsys.tools.expression.ProcessingDecorator.recursion";

	protected static Object formatFunctor(Object value, String format) throws FunctorException {
		IFunctorRegistry registry = getFormattingFunctors();
		if (registry == null) {
			return value;
		}
		int openBrace = format.indexOf(OPEN_BRACE);
		if (openBrace == -1) {
			openBrace = format.length();
		}
		int closeBrace = format.indexOf(CLOSE_BRACE);
		String formatterId = format.substring(0, openBrace);
		String[] argStrings;
		if (openBrace < closeBrace) {
			argStrings = format.substring(openBrace + 1, closeBrace).split(ARG_SEPARATOR);
		} else {
			argStrings = new String[0];
		}
		IFunctor formatter = registry.lookupFunctor(formatterId);
		if (formatter == null) {
			throw new FunctorInternalException("formatter '" + formatterId //$NON-NLS-1$
					+ "' not found"); //$NON-NLS-1$
		}
		Args args = Args.createIndexed(value, argStrings);
		IFunctorCall call = new FunctorCall(value, args);
		return formatter.perform(call);
	}

	public static IFunctorRegistry getFormattingFunctors() {
		return FormattingFunctors;
	}

	public static void setFormattingFunctors(IFunctorRegistry formattingFunctors) {
		ProcessingDecorator.FormattingFunctors = formattingFunctors;
	}

	private IStringEvaluator evaluator;

	private char separator = PROCESSING_SEPARATOR;

	private String separatorString = "" + separator; //$NON-NLS-1$

	private IStringEvaluator recursionEvaluator;

	public ProcessingDecorator(IStringEvaluator evaluator) {
		this.evaluator = evaluator;
		this.recursionEvaluator = evaluator;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		try {
			List<Expression> exprs = parse(expression);
			if (exprs.isEmpty()) {
				throw new EvaluationException("empty expression");
			}
			Object value = null;
			EvaluationException ex = null;
			Expression expr = exprs.get(0);
			try {
				if (expr.isString()) {
					value = ((StringLiteral) expr).getValue();
				} else {
					String valueExpression = expr.getCode().trim();
					value = evaluator.evaluate(valueExpression, args);
				}
			} catch (NamespaceNotFound e) {
				// do not compute default value
				// most probably we have multiple evaluation rounds with different resolver context
				throw e;
			} catch (EvaluationException e) {
				ex = e;
			}
			for (int i = 1; i < exprs.size(); i++) {
				try {
					value = evaluateInstruction(value, ex, args, exprs.get(i));
					ex = null;
				} catch (EvaluationException e) {
					ex = e;
				}
			}
			propagateException(ex);
			return value;
		} catch (IOException e) {
			throw new EvaluationException(e);
		}
	}

	protected Object evaluateConditional(Object value, IArgs args, String instruction) throws EvaluationException {
		String expression = instruction.substring(1).trim();
		boolean negate = false;
		if (expression.startsWith("!")) {
			negate = true;
			expression = expression.substring(1).trim();
		}
		Object instructionValue = evaluator.evaluate(expression, args);
		try {
			boolean ok = Boolean.TRUE.equals(ConverterRegistry.get().convert(instructionValue, Boolean.class));
			return ok ^ negate ? value : null;
		} catch (ConversionException e) {
			return null;
		}
	}

	protected Object evaluateDeepRecursion(Object value, IArgs args, String instruction) throws EvaluationException {
		int depth = (Integer) args.get(ARG_RECURSION, 10);
		if (depth == -1) {
			return value;
		}
		if (value instanceof String) {
			args.put(ARG_RECURSION, --depth);
			Object result;
			result = recursionEvaluator.evaluate((String) value, args);
			if (value.equals(result)) {
				// nothing has changed, this will starve...
				return value;
			}
			return evaluateDeepRecursion(result, args, instruction);
		} else {
			return value;
		}
	}

	protected Object evaluateDefaultValue(Object value, EvaluationException ex, IArgs args, String instruction)
			throws EvaluationException {
		if (ex != null || value == null || ((value instanceof String) && StringTools.isEmpty((String) value))) {
			return evaluate(instruction.substring(1), args);
		}
		return value;
	}

	/**
	 * Apply functor "instruction" to the value.
	 * 
	 * @param value
	 *            The argument to the functor
	 * @param args
	 *            The original args
	 * @param instruction
	 *            The functor serialization
	 * @return
	 * @throws EvaluationException
	 */
	protected Object evaluateFunctor(Object value, IArgs args, String instruction) throws EvaluationException {
		try {
			return formatFunctor(value, instruction.substring(1));
		} catch (FunctorException e) {
			throw new EvaluationException(e.getCause() == null ? e : e.getCause());
		}
	}

	protected Object evaluateInstruction(Object value, EvaluationException ex, IArgs args, Expression expr)
			throws EvaluationException {
		if (expr == null) {
			propagateException(ex);
			// apply default format for value
			return StringTools.format(value, "");
		}
		if (expr.isString()) {
			return evaluateInstruction(value, ex, args, ((StringLiteral) expr).getValue());
		}
		if (expr.isToken()) {
			return evaluateInstruction(value, ex, args, expr.getCode());
		}
		if (expr.isFunction()) {
			return evaluateInstruction(value, ex, args, expr.getCode());
		}
		if (expr.isParantheses()) {
			return evaluateInstruction(value, ex, args, ((Parantheses) expr).getNested());
		}
		propagateException(ex);
		// apply default format for value
		return StringTools.format(value, "");
	}

	protected Object evaluateInstruction(Object value, EvaluationException ex, IArgs args, String instruction)
			throws EvaluationException {
		char c = instruction.charAt(0);
		if (c == CODE_DEFAULTVALUE) {
			return evaluateDefaultValue(value, ex, args, instruction);
		} else if (c == CODE_DEEPRECURSION || c == CODE_SHALLOWRECURSION) {
			propagateException(ex);
			return evaluateDeepRecursion(value, args, instruction);
		} else if (c == CODE_FUNCTOR) {
			propagateException(ex);
			return evaluateFunctor(value, args, instruction);
		} else if (c == CODE_CONDITIONAL) {
			propagateException(ex);
			return evaluateConditional(value, args, instruction);
		} else if (c == CODE_REFLECTION) {
			propagateException(ex);
			return evaluateReflection(value, args, instruction);
		} else {
			propagateException(ex);
			return StringTools.format(value, instruction);
		}
	}

	protected Object evaluateReflection(Object value, IArgs args, String instruction) throws EvaluationException {
		ReflectiveResolver reflector = new ReflectiveResolver(value);
		return reflector.evaluate(instruction.substring(1), args);
	}

	public IStringEvaluator getEvaluator() {
		return evaluator;
	}

	public IStringEvaluator getRecursionEvaluator() {
		return recursionEvaluator;
	}

	public char getSeparator() {
		return separator;
	}

	public String getSeparatorString() {
		return separatorString;
	}

	protected void parse(List<Expression> exprs, StringReader reader) throws IOException {
		ExpressionParser parser = new ExpressionParser(':');
		Expression expr = parser.parse(reader);
		while (expr != null) {
			exprs.add(expr);
			expr = parser.parse(reader);
		}
	}

	/**
	 * <pre>
	 * expression ::= [ '"' chars '"' | var ] [ ':' instruction ]*
	 * chars ::= <any character>
	 * var ::= [ alpha | '.' ]*
	 * instruction ::= '(' instruction ')' | code [ option ]
	 * </pre>
	 * 
	 * @param expression
	 * @return
	 * @throws IOException
	 */
	protected List<Expression> parse(String expression) throws IOException {
		List<Expression> exprs = new ArrayList<>(2);
		StringReader r = new StringReader(expression);
		parse(exprs, r);
		return exprs;
	}

	protected void propagateException(EvaluationException ex) throws EvaluationException {
		if (ex != null) {
			throw ex;
		}
	}

	public void setRecursionEvaluator(IStringEvaluator recursionEvaluator) {
		this.recursionEvaluator = recursionEvaluator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}
}
