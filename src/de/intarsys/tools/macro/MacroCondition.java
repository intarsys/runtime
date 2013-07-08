package de.intarsys.tools.macro;

import de.intarsys.tools.component.ComponentException;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.functor.FunctorInvocationException;
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

	private MacroExpr ifExpression;

	private MacroExpr thenExpression;

	private MacroExpr elseExpression;

	public MacroCondition() {
		super();
	}

	@Override
	protected void basicDispose() {
		super.basicDispose();
		ComponentException se = null;
		try {
			if (getIfExpression() != null) {
				getIfExpression().dispose();
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
		try {
			if (getThenExpression() != null) {
				getThenExpression().dispose();
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
		try {
			if (getElseExpression() != null) {
				getElseExpression().dispose();
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
		stepElement = element.element("if");
		ifExpression = createScriptExpr(stepElement);
		stepElement = element.element("then");
		thenExpression = createScriptExpr(stepElement);
		stepElement = element.element("else");
		elseExpression = createScriptExpr(stepElement);
	}

	public MacroExpr getElseExpression() {
		return elseExpression;
	}

	public MacroExpr getIfExpression() {
		return ifExpression;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("If (");
		if (ifExpression != null) {
			sb.append(ifExpression.getLabel());
		}
		sb.append(") ");
		sb.append("Then \n");
		if (thenExpression != null) {
			sb.append(thenExpression.getLabel());
		} else {
			sb.append("{ }");
		}
		if (elseExpression != null) {
			sb.append("\nElse \n");
			sb.append(elseExpression.getLabel());
		}
		return sb.toString();
	}

	public MacroExpr getThenExpression() {
		return thenExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		boolean conditionResult = true;
		if (getIfExpression() != null) {
			Object tempObj = getIfExpression().perform(call);
			try {
				conditionResult = ConverterRegistry.get().convert(tempObj,
						Boolean.class);
			} catch (ConversionException e) {
				throw new FunctorInvocationException(e);
			}
		}
		if (conditionResult) {
			if (getThenExpression() != null) {
				return getThenExpression().perform(call);
			}
		} else {
			if (getElseExpression() != null) {
				return getElseExpression().perform(call);
			}
		}
		return null;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		super.serialize(element);
		if (ifExpression != null) {
			IElement temp = element.newElement("if");
			ifExpression.serialize(temp);
		}
		if (thenExpression != null) {
			IElement temp = element.newElement("then");
			ifExpression.serialize(temp);
		}
		if (elseExpression != null) {
			IElement temp = element.newElement("else");
			ifExpression.serialize(temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		if (getIfExpression() != null) {
			getIfExpression().setContext(context);
		}
		if (getThenExpression() != null) {
			getThenExpression().setContext(context);
		}
		if (getElseExpression() != null) {
			getElseExpression().setContext(context);
		}
	}

	public void setElseExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.elseExpression = step;
	}

	public void setIfExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.ifExpression = step;
	}

	public void setThenExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.thenExpression = step;
	}
}
