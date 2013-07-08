/*
 * Copyright (c) 2007, intarsys consulting GmbH
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

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
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
 * <dt>+</dt>
 * <dd>re - perform evaluation on the result one time</dd>
 * <dt>*</dt>
 * <dd>perform evaluation on the result until it is no longer changed (or null)</dd>
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

	private static IFunctorRegistry formattingFunctors;

	protected static Object formatFunctor(Object value, String format)
			throws FunctorInvocationException {
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
			argStrings = format.substring(openBrace + 1, closeBrace).split(
					ARG_SEPARATOR);
		} else {
			argStrings = new String[0];
		}
		IFunctor formatter = registry.lookupFunctor(formatterId);
		if (formatter == null) {
			throw new FunctorInvocationException("formatter '" + formatterId //$NON-NLS-1$
					+ "' not found"); //$NON-NLS-1$
		}
		Args args = Args.createIndexed(value, argStrings);
		IFunctorCall call = new FunctorCall(value, args);
		return formatter.perform(call);
	}

	public static IFunctorRegistry getFormattingFunctors() {
		return formattingFunctors;
	}

	public static void setFormattingFunctors(IFunctorRegistry formattingFunctors) {
		ProcessingDecorator.formattingFunctors = formattingFunctors;
	}

	private IStringEvaluator evaluator;

	private char separator = PROCESSING_SEPARATOR;

	private String separatorString = "" + separator; //$NON-NLS-1$

	private IStringEvaluator recursionEvaluator;

	private static final String ARG_RECURSION = "de.intarsys.tools.expression.ProcessingDecorator.recursion";

	/**
	 * 
	 */
	public ProcessingDecorator(IStringEvaluator evaluator) {
		this.evaluator = evaluator;
		this.recursionEvaluator = evaluator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.expression.IStringEvaluator#evaluate(java.lang.String,
	 * de.intarsys.tools.functor.IArgs)
	 */
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		String[] instructions = expression.split(getSeparatorString(), -1);
		String valueExpression = instructions[0].trim();
		Object value;
		if (valueExpression.startsWith("\"")) { //$NON-NLS-1$
			try {
				value = StringTools.unquote(valueExpression);
			} catch (IOException e) {
				throw new EvaluationException(e);
			}
		} else {
			value = evaluator.evaluate(valueExpression, args);
		}
		for (int i = 1; i < instructions.length; i++) {
			value = evaluateInstruction(value, args, instructions[i]);
		}
		return value;
	}

	protected Object evaluateConditional(Object value, IArgs args,
			String instruction) throws EvaluationException {
		String expression = instruction.trim().substring(1);
		boolean ok = ArgTools.getBool(args, expression, false);
		return ok ? value : null;
	}

	protected Object evaluateDeepRecursion(Object value, IArgs args,
			String instruction) throws EvaluationException {
		int depth = (Integer) args.get(ARG_RECURSION, 10);
		if (depth == -1) {
			return value;
		}
		if (value instanceof String) {
			args.put(ARG_RECURSION, --depth);
			Object result;
			try {
				result = recursionEvaluator.evaluate((String) value, args);
				if (value.equals(result)) {
					// nothing has changed, this will starve...
					return value;
				}
				return evaluateDeepRecursion(result, args, instruction);
			} catch (Exception e) {
				return value;
			}
		} else {
			return value;
		}
	}

	protected Object evaluateFunctor(Object value, IArgs args,
			String instruction) throws EvaluationException {
		try {
			return formatFunctor(value, instruction.substring(1));
		} catch (FunctorInvocationException e) {
			throw new EvaluationException(e.getCause() == null ? e
					: e.getCause());
		}
	}

	protected Object evaluateInstruction(Object value, IArgs args,
			String instruction) throws EvaluationException {
		if (instruction.length() == 0) {
			// apply default format for value
			return StringTools.format(value, instruction);
		}
		char c = instruction.charAt(0);
		if (c == CODE_SHALLOWRECURSION) {
			// this is legacy...
			return evaluateDeepRecursion(value, args, instruction);
		} else if (c == CODE_DEEPRECURSION) {
			return evaluateDeepRecursion(value, args, instruction);
		} else if (c == CODE_FUNCTOR) {
			return evaluateFunctor(value, args, instruction);
		} else if (c == CODE_CONDITIONAL) {
			return evaluateConditional(value, args, instruction);
		} else if (c == CODE_REFLECTION) {
			return evaluateReflection(value, args, instruction);
		} else {
			return StringTools.format(value, instruction);
		}
	}

	protected Object evaluateReflection(Object value, IArgs args,
			String instruction) throws EvaluationException {
		ReflectiveResolver reflector = new ReflectiveResolver(value);
		return reflector.evaluate(instruction, args);
	}

	protected Object evaluateShallowRecursion(Object value, IArgs args,
			String instruction) throws EvaluationException {
		int depth = (Integer) args.get(ARG_RECURSION, 1);
		if (depth == -1) {
			throw new EvaluationException("expression nested to deeply"); //$NON-NLS-1$
		}
		args.put(ARG_RECURSION, depth--);
		return recursionEvaluator.evaluate(String.valueOf(value), args);
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

	public void setRecursionEvaluator(IStringEvaluator recursionEvaluator) {
		this.recursionEvaluator = recursionEvaluator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}
}
