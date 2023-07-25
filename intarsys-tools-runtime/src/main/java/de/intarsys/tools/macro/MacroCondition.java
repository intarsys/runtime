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
 * Implementation of conditional execution of an {@link IFunctor}.
 * 
 * When the "if" expression does not exist or returns true, the "then"
 * expression is executed, the "else" expression otherwise.
 * 
 * The result is the result of the last {@link IFunctor} executed (this is
 * either the "true" or the "false" expression).
 */
public class MacroCondition extends MacroFunctor {

	private IFunctor ifExpression;

	private IFunctor thenExpression;

	private IFunctor elseExpression;

	public MacroCondition() {
		super();
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		IElement stepElement;
		//
		stepElement = element.element("if");
		setIfExpression(createFunctor(stepElement));
		stepElement = element.element("then");
		setThenExpression(createFunctor(stepElement));
		stepElement = element.element("else");
		setElseExpression(createFunctor(stepElement));
	}

	@Override
	protected String getDefaultLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("if (");
		if (ifExpression != null) {
			sb.append(getChildLabel(ifExpression));
		}
		sb.append(") ");
		sb.append("then\n");
		if (thenExpression != null) {
			sb.append(" { \n");
			sb.append(getChildLabel(thenExpression));
			sb.append("\n }");
		} else {
			sb.append(" { }");
		}
		if (elseExpression != null) {
			sb.append("\n else { \n");
			sb.append(getChildLabel(elseExpression));
			sb.append("\n }");
		}
		return sb.toString();
	}

	public IFunctor getElseExpression() {
		return elseExpression;
	}

	public IFunctor getIfExpression() {
		return ifExpression;
	}

	public IFunctor getThenExpression() {
		return thenExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		boolean conditionResult = true;
		if (getIfExpression() != null) {
			Object tempObj = getIfExpression().perform(call);
			try {
				conditionResult = ConverterRegistry.get().convert(tempObj, Boolean.class);
			} catch (ConversionException e) {
				throw new FunctorExecutionException(e);
			}
		}
		try {
			if (conditionResult) {
				if (getThenExpression() != null) {
					return getThenExpression().perform(call);
				}
			} else {
				if (getElseExpression() != null) {
					return getElseExpression().perform(call);
				}
			}
		} catch (MacroControlFlow e) {
			return handleControlFlow(e);
		}
		return null;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
		if (ifExpression != null) {
			IElement temp = element.newElement("if");
			serializeChildFunctor(ifExpression, temp);
		}
		if (thenExpression != null) {
			IElement temp = element.newElement("then");
			serializeChildFunctor(thenExpression, temp);
		}
		if (elseExpression != null) {
			IElement temp = element.newElement("else");
			serializeChildFunctor(elseExpression, temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		setChildContext(getIfExpression(), context);
		setChildContext(getThenExpression(), context);
		setChildContext(getElseExpression(), context);
	}

	public void setElseExpression(IFunctor step) {
		this.elseExpression = step;
		associateChild(step);
	}

	public void setIfExpression(IFunctor step) {
		this.ifExpression = step;
		associateChild(step);
	}

	public void setThenExpression(IFunctor step) {
		this.thenExpression = step;
		associateChild(step);
	}
}
