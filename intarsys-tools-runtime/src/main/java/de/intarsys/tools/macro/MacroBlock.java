package de.intarsys.tools.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.collection.ReverseListIterator;
import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.ComponentInternalException;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
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

	private final List<IFunctor> blockExpressions = new ArrayList<>();

	private final List<IFunctor> errorExpressions = new ArrayList<>();

	private final List<IFunctor> finallyExpressions = new ArrayList<>();

	public MacroBlock() {
		super();
	}

	public void addBlockExpression(IFunctor step) {
		addExpression(blockExpressions, step);
	}

	public void addBlockExpression(int index, IFunctor step) {
		addExpression(blockExpressions, index, step);
	}

	public void addErrorExpression(IFunctor step) {
		addExpression(errorExpressions, step);
	}

	public void addErrorExpression(int index, IFunctor step) {
		addExpression(errorExpressions, index, step);
	}

	protected void addExpression(List<IFunctor> expressions, IFunctor step) {
		if (step == null) {
			return;
		}
		associateChild(step);
		expressions.add(step);
	}

	protected void addExpression(List<IFunctor> expressions, int index, IFunctor step) {
		expressions.add(index, step);
	}

	public void addFinallyExpression(IFunctor step) {
		addExpression(finallyExpressions, step);
	}

	public void addFinallyExpression(int index, IFunctor step) {
		addExpression(finallyExpressions, index, step);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		configureExpressions(element.element("block"), blockExpressions);
		configureExpressions(element.element("error"), errorExpressions);
		configureExpressions(element.element("finally"), finallyExpressions);
	}

	protected void configureExpressions(IElement stepsElement, List<IFunctor> steps) throws ConfigurationException {
		if (stepsElement == null) {
			return;
		}
		Iterator<IElement> itStep = stepsElement.elementIterator("step"); //$NON-NLS-1$
		while (itStep.hasNext()) {
			IElement stepElement = itStep.next();
			IFunctor step = createFunctor(stepElement);
			addExpression(steps, step);
		}
	}

	protected void disposeExpressions(List<IFunctor> steps) throws FunctorException {
		FunctorException se = null;
		Iterator<IFunctor> it = new ReverseListIterator<>(steps);
		while (it.hasNext()) {
			IFunctor step = it.next();
			try {
				disposeChild(step);
			} catch (Exception e) {
				if (se == null) {
					se = new FunctorExecutionException(e);
				}
			}
		}
		if (se != null) {
			throw se;
		}
	}

	protected ComponentException disposeExpressions(List<IFunctor> expressions, ComponentException se) {
		try {
			disposeExpressions(expressions);
		} catch (ComponentException e) {
			if (se == null) {
				se = e;
			}
		} catch (Exception e) {
			if (se == null) {
				se = new ComponentInternalException(e);
			}
		}
		return se;
	}

	public List<IFunctor> getBlockExpressions() {
		return Collections.unmodifiableList(blockExpressions);
	}

	@Override
	protected String getDefaultLabel() {
		return "{ Block }";
	}

	public List<IFunctor> getErrorExpressions() {
		return Collections.unmodifiableList(errorExpressions);
	}

	public List<IFunctor> getFinallyExpressions() {
		return Collections.unmodifiableList(finallyExpressions);
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		try {
			return performExpressions(blockExpressions, call);
		} catch (MacroControlFlow e) {
			return handleControlFlow(e);
		} catch (Exception e) {
			if (errorExpressions.isEmpty()) {
				if (e instanceof FunctorException) {
					throw e;
				} else {
					throw new FunctorExecutionException(e);
				}
			} else {
				try {
					return performExpressions(errorExpressions, call);
				} catch (MacroControlFlow inner) {
					return handleControlFlow(inner);
				}
			}
		} finally {
			try {
				performExpressions(finallyExpressions, call);
			} catch (MacroControlFlow e) {
				// neither exception nor result is handled!
			}
		}
	}

	protected Object performExpressions(List<IFunctor> steps, IFunctorCall call) throws FunctorException {
		Object result = null;
		for (IFunctor step : steps) {
			result = step.perform(call);
		}
		return result;
	}

	public void removeBlockExpression(IFunctor step) {
		blockExpressions.remove(step);
	}

	public void removeErrorExpression(IFunctor step) {
		errorExpressions.remove(step);
	}

	public void removeFinallyExpression(IFunctor step) {
		finallyExpressions.remove(step);
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
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
	}

	protected void serializeExpressions(IElement element, List<IFunctor> steps) throws ElementSerializationException {
		for (IFunctor step : steps) {
			IElement stepElement = element.newElement("step");
			serializeChildFunctor(step, stepElement);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		setContextExpressions(blockExpressions, context);
		setContextExpressions(errorExpressions, context);
		setContextExpressions(finallyExpressions, context);
	}

	protected void setContextExpressions(List<IFunctor> steps, Object context) throws ConfigurationException {
		for (IFunctor step : steps) {
			setChildContext(step, context);
		}
	}

}
