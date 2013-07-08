package de.intarsys.tools.macro;

import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorInvocationException;
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

	private MacroExpr valueExpression;

	@Override
	protected void basicDispose() {
		super.basicDispose();
		ComponentException se = null;
		try {
			if (getValueExpression() != null) {
				getValueExpression().dispose();
			}
		} catch (ComponentException e) {
			if (se == null) {
				se = e;
			}
		} catch (Exception e) {
			if (se == null) {
				se = new ComponentException(e);
			}
		}
		if (se != null) {
			throw se;
		}
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		IElement stepElement;
		//
		stepElement = element.element("value");
		valueExpression = createScriptExpr(stepElement);
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("Return ");
		if (valueExpression != null) {
			sb.append(valueExpression.getLabel());
		}
		return sb.toString();
	}

	public MacroExpr getValueExpression() {
		return valueExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		Object result;
		if (getValueExpression() != null) {
			result = getValueExpression().perform(call);
		} else {
			result = null;
		}
		throw new Return(result);
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		super.serialize(element);
		if (valueExpression != null) {
			IElement temp = element.newElement("value");
			valueExpression.serialize(temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		if (getValueExpression() != null) {
			getValueExpression().setContext(context);
		}
	}

	public void setValueExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.valueExpression = step;
	}

}
