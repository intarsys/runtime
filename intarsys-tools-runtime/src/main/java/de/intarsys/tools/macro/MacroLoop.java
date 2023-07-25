package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;

/**
 * Implementation of repeated execution of an {@link IFunctor}.
 * 
 * First, the "init" expression is executed.
 * 
 * The, as long as evaluation of the "while" expression returns "true", "do" is
 * executed.
 * 
 * The result of this {@link IFunctor} is null.
 */
public class MacroLoop extends MacroFunctor {

	private IFunctor initExpression;

	private IFunctor whileExpression;

	private IFunctor doExpression;

	public MacroLoop() {
		super();
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		IElement stepElement;
		//
		stepElement = element.element("init");
		setInitExpression(createFunctor(stepElement));
		stepElement = element.element("while");
		setWhileExpression(createFunctor(stepElement));
		stepElement = element.element("do");
		setDoExpression(createFunctor(stepElement));
	}

	@Override
	protected String getDefaultLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("for (");
		sb.append(getChildLabel(initExpression));
		sb.append("; ");
		sb.append(getChildLabel(whileExpression));
		sb.append(") { \n");
		sb.append(getChildLabel(doExpression));
		sb.append("\n }");
		return sb.toString();
	}

	public IFunctor getDoExpression() {
		return doExpression;
	}

	public IFunctor getInitExpression() {
		return initExpression;
	}

	public IFunctor getWhileExpression() {
		return whileExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		if (getInitExpression() != null) {
			getInitExpression().perform(call);
		}
		boolean conditionResult = true;
		if (getWhileExpression() != null) {
			Object tempObj = getWhileExpression().perform(call);
			try {
				conditionResult = ConverterRegistry.get().convert(tempObj, Boolean.class);
			} catch (ConversionException e) {
				throw new FunctorExecutionException(e);
			}
		}
		while (conditionResult) {
			try {
				if (getDoExpression() != null) {
					getDoExpression().perform(call);
				}
			} catch (Break e) {
				return null;
			} catch (Continue e) {
				//
			} catch (MacroControlFlow e) {
				return handleControlFlow(e);
			}
			if (getWhileExpression() != null) {
				Object tempObj = getWhileExpression().perform(call);
				try {
					conditionResult = ConverterRegistry.get().convert(tempObj, Boolean.class);
				} catch (ConversionException e) {
					throw new FunctorExecutionException(e);
				}
			}
		}
		return null;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
		if (initExpression != null) {
			IElement temp = element.newElement("init");
			serializeChildFunctor(initExpression, temp);
		}
		if (whileExpression != null) {
			IElement temp = element.newElement("while");
			serializeChildFunctor(whileExpression, temp);
		}
		if (doExpression != null) {
			IElement temp = element.newElement("do");
			serializeChildFunctor(doExpression, temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		setChildContext(getInitExpression(), context);
		setChildContext(getWhileExpression(), context);
		setChildContext(getDoExpression(), context);
	}

	public void setDoExpression(IFunctor step) {
		this.doExpression = step;
		associateChild(step);
	}

	public void setInitExpression(IFunctor step) {
		this.initExpression = step;
		associateChild(step);
	}

	public void setWhileExpression(IFunctor step) {
		this.whileExpression = step;
		associateChild(step);
	}

}
