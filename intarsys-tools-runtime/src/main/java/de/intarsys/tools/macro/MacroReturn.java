package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;

/**
 * Return from the execution of a {@link MacroBlock}, regardless of execution
 * depth.
 * 
 * The result of this service is the result of the "value" expression executed
 * or null.
 */
public class MacroReturn extends MacroFunctor {

	private IFunctor valueExpression;

	public MacroReturn() {

	}

	public MacroReturn(IFunctor expr) {
		setValueExpression(expr);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		IElement stepElement;
		//
		stepElement = element.element("value");
		setValueExpression(createFunctor(stepElement));
	}

	@Override
	protected String getDefaultLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("return ");
		sb.append(getChildLabel(valueExpression));
		return sb.toString();
	}

	public IFunctor getValueExpression() {
		return valueExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		try {
			Object result;
			if (getValueExpression() != null) {
				result = getValueExpression().perform(call);
			} else {
				result = null;
			}
			throw new Return(result);
		} catch (MacroControlFlow e) {
			return handleControlFlow(e);
		}
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
		if (valueExpression != null) {
			IElement temp = element.newElement("value");
			serializeChildFunctor(valueExpression, temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		setChildContext(getValueExpression(), context);
	}

	public void setValueExpression(IFunctor step) {
		this.valueExpression = step;
		associateChild(step);
	}

}
