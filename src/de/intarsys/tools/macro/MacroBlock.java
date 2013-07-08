package de.intarsys.tools.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.collection.ReverseListIterator;
import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;

/**
 * The {@link MacroBlock} allows for aggregation of other {@link IFunctor}
 * instances to a new compound implementation.
 * 
 * Each {@link IFunctor} expression is called in the order of definition. The
 * result of the {@link MacroBlock} is the result of the last expression. The
 * result is undefined when an error was thrown and not handled.
 * 
 * If an error occurs, normal execution is terminated and all error expressions
 * are executed if available. The error execution may handle the error or
 * rethrow another. If an error occurs while executing the error expressions,
 * the execution is terminated and the error is thrown. If no error expressions
 * are defined, the error is thrown.
 * 
 * After normal execution or error execution is finished, all finally
 * expressions are performed. If an error occurs while executing the finally
 * expressions, the finally execution is terminated and the error is thrown. To
 * ensure that all finally blocks are properly executed, each block
 * implementation should take care of defensive exception handling.
 * 
 */
public class MacroBlock extends MacroFunctor {

	final private List<MacroExpr> initExpressions = new ArrayList<>();

	final private List<MacroExpr> blockExpressions = new ArrayList<>();

	final private List<MacroExpr> errorExpressions = new ArrayList<>();

	final private List<MacroExpr> finallyExpressions = new ArrayList<>();

	final private List<MacroExpr> disposeExpressions = new ArrayList<>();

	public MacroBlock() {
		super();
	}

	public void addBlockExpression(int index, MacroExpr step) {
		addExpression(blockExpressions, index, step);
	}

	public void addBlockExpression(MacroExpr step) {
		addExpression(blockExpressions, step);
	}

	public void addDisposeExpression(int index, MacroExpr step) {
		addExpression(disposeExpressions, index, step);
	}

	public void addDisposeExpression(MacroExpr step) {
		addExpression(disposeExpressions, step);
	}

	public void addErrorExpression(int index, MacroExpr step) {
		addExpression(errorExpressions, index, step);
	}

	public void addErrorExpression(MacroExpr step) {
		addExpression(errorExpressions, step);
	}

	protected void addExpression(List<MacroExpr> expressions, int index,
			MacroExpr step) {
		expressions.add(index, step);
	}

	protected void addExpression(List<MacroExpr> expressions, MacroExpr step) {
		if (step == null) {
			return;
		}
		step.setScriptFunctor(this);
		expressions.add(step);
	}

	public void addFinallyExpression(int index, MacroExpr step) {
		addExpression(finallyExpressions, index, step);
	}

	public void addFinallyExpression(MacroExpr step) {
		addExpression(finallyExpressions, step);
	}

	public void addInitExpression(int index, MacroExpr step) {
		addExpression(initExpressions, index, step);
	}

	public void addInitExpression(MacroExpr step) {
		addExpression(initExpressions, step);
	}

	@Override
	protected void basicDispose() {
		super.basicDispose();
		ComponentException se = null;
		try {
			performExpressions(disposeExpressions,
					new FunctorCall(null, Args.create()));
		} catch (Exception e) {
			if (se == null) {
				se = new ComponentException(e);
			}
		}
		se = disposeExpressions(disposeExpressions, se);
		se = disposeExpressions(finallyExpressions, se);
		se = disposeExpressions(errorExpressions, se);
		se = disposeExpressions(blockExpressions, se);
		se = disposeExpressions(initExpressions, se);
		if (se != null) {
			throw se;
		}
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		configureExpressions(element.element("init"), initExpressions);
		configureExpressions(element.element("block"), blockExpressions);
		configureExpressions(element.element("error"), errorExpressions);
		configureExpressions(element.element("finally"), finallyExpressions);
		configureExpressions(element.element("dispose"), disposeExpressions);
	}

	protected void configureExpressions(IElement stepsElement,
			List<MacroExpr> steps) throws ConfigurationException {
		if (stepsElement == null) {
			return;
		}
		Iterator<IElement> itStep = stepsElement.elementIterator("step"); //$NON-NLS-1$
		while (itStep.hasNext()) {
			IElement stepElement = itStep.next();
			MacroExpr step = createScriptExpr(stepElement);
			steps.add(step);
		}
	}

	protected void disposeExpressions(List<MacroExpr> steps)
			throws FunctorInvocationException {
		FunctorInvocationException se = null;
		Iterator<MacroExpr> it = new ReverseListIterator<>(steps);
		while (it.hasNext()) {
			MacroExpr step = it.next();
			try {
				step.dispose();
			} catch (FunctorInvocationException e) {
				if (se == null) {
					se = e;
				}
			} catch (Exception e) {
				if (se == null) {
					se = new FunctorInvocationException(e);
				}
			}
		}
		if (se != null) {
			throw se;
		}
	}

	protected ComponentException disposeExpressions(
			List<MacroExpr> expressions, ComponentException se) {
		try {
			disposeExpressions(expressions);
		} catch (ComponentException e) {
			if (se == null) {
				se = e;
			}
		} catch (Exception e) {
			if (se == null) {
				se = new ComponentException(e);
			}
		}
		return se;
	}

	public List<MacroExpr> getBlockExpressions() {
		return Collections.unmodifiableList(blockExpressions);
	}

	public List<MacroExpr> getDisposeExpressions() {
		return Collections.unmodifiableList(disposeExpressions);
	}

	public List<MacroExpr> getErrorExpressions() {
		return Collections.unmodifiableList(errorExpressions);
	}

	public List<MacroExpr> getFinallyExpressions() {
		return Collections.unmodifiableList(finallyExpressions);
	}

	public List<MacroExpr> getInitExpressions() {
		return Collections.unmodifiableList(initExpressions);
	}

	@Override
	public String getLabel() {
		return "{ Block }";
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		try {
			return performExpressions(blockExpressions, call);
		} catch (MacroControlFlow e) {
			throw e;
		} catch (Exception e) {
			if (errorExpressions.isEmpty()) {
				if (e instanceof FunctorInvocationException) {
					throw e;
				} else {
					throw new FunctorInvocationException(e);
				}
			} else {
				return performExpressions(errorExpressions, call);
			}
		} finally {
			performExpressions(finallyExpressions, call);
		}
	}

	protected Object performExpressions(List<MacroExpr> steps, IFunctorCall call)
			throws FunctorInvocationException {
		Object result = null;
		for (MacroExpr step : steps) {
			result = step.perform(call);
		}
		return result;
	}

	public void removeBlockExpression(MacroExpr step) {
		blockExpressions.remove(step);
	}

	public void removeDisposeExpression(MacroExpr step) {
		disposeExpressions.remove(step);
	}

	public void removeErrorExpression(MacroExpr step) {
		errorExpressions.remove(step);
	}

	public void removeFinallyExpression(MacroExpr step) {
		finallyExpressions.remove(step);
	}

	public void removeInitExpression(MacroExpr step) {
		initExpressions.remove(step);
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		super.serialize(element);
		if (!initExpressions.isEmpty()) {
			IElement stepsElement = element.newElement("init");
			serializeExpressions(stepsElement, initExpressions);
		}
		if (!blockExpressions.isEmpty()) {
			IElement stepsElement = element.newElement("block");
			serializeExpressions(stepsElement, blockExpressions);
		}
		if (!errorExpressions.isEmpty()) {
			IElement stepsElement = element.newElement("error");
			serializeExpressions(stepsElement, errorExpressions);
		}
		if (!finallyExpressions.isEmpty()) {
			IElement stepsElement = element.newElement("finally");
			serializeExpressions(stepsElement, finallyExpressions);
		}
		if (!disposeExpressions.isEmpty()) {
			IElement stepsElement = element.newElement("dispose");
			serializeExpressions(stepsElement, disposeExpressions);
		}
	}

	protected void serializeExpressions(IElement element, List<MacroExpr> steps)
			throws ElementSerializationException {
		for (MacroExpr step : steps) {
			IElement stepElement = element.newElement("step");
			step.serialize(stepElement);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		setContextExpressions(initExpressions, context);
		setContextExpressions(blockExpressions, context);
		setContextExpressions(errorExpressions, context);
		setContextExpressions(finallyExpressions, context);
		setContextExpressions(disposeExpressions, context);
		try {
			performExpressions(initExpressions,
					new FunctorCall(null, Args.create()));
		} catch (Exception e) {
			throw new ConfigurationException(e);
		}
	}

	protected void setContextExpressions(List<MacroExpr> steps, Object config)
			throws ConfigurationException {
		for (MacroExpr step : steps) {
			step.setContext(config);
		}
	}

}
