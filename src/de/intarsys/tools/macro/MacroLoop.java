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
 * Implementation of repeated execution of an {@link IFunctor}.
 * 
 * First, the "init" expression is executed.
 * 
 * The, as long as evaluation of the "while" expression returns "true", first
 * "do" and then "after" is executed.
 * 
 * The result of this {@link IFunctor} is null.
 */
public class MacroLoop extends MacroFunctor {

	private MacroExpr initExpression;

	private MacroExpr whileExpression;

	private MacroExpr doExpression;

	public MacroLoop() {
		super();
	}

	@Override
	protected void basicDispose() {
		super.basicDispose();
		ComponentException se = null;
		try {
			if (getInitExpression() != null) {
				getInitExpression().dispose();
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
			if (getWhileExpression() != null) {
				getWhileExpression().dispose();
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
			if (getDoExpression() != null) {
				getDoExpression().dispose();
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
		stepElement = element.element("init");
		initExpression = createScriptExpr(stepElement);
		stepElement = element.element("while");
		whileExpression = createScriptExpr(stepElement);
		stepElement = element.element("do");
		doExpression = createScriptExpr(stepElement);
	}

	public MacroExpr getDoExpression() {
		return doExpression;
	}

	public MacroExpr getInitExpression() {
		return initExpression;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append("For (");
		if (initExpression != null) {
			sb.append(initExpression.getLabel());
		}
		sb.append("; ");
		if (whileExpression != null) {
			sb.append(whileExpression.getLabel());
		}
		sb.append(") ");
		if (doExpression != null) {
			sb.append(doExpression.getLabel());
		}
		return sb.toString();
	}

	public MacroExpr getWhileExpression() {
		return whileExpression;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		if (getInitExpression() != null) {
			getInitExpression().perform(call);
		}
		boolean conditionResult = true;
		if (getWhileExpression() != null) {
			Object tempObj = getWhileExpression().perform(call);
			try {
				conditionResult = ConverterRegistry.get().convert(tempObj,
						Boolean.class);
			} catch (ConversionException e) {
				throw new FunctorInvocationException(e);
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
			}
			if (getWhileExpression() != null) {
				Object tempObj = getWhileExpression().perform(call);
				try {
					conditionResult = ConverterRegistry.get().convert(tempObj,
							Boolean.class);
				} catch (ConversionException e) {
					throw new FunctorInvocationException(e);
				}
			}
		}
		return null;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		super.serialize(element);
		if (initExpression != null) {
			IElement temp = element.newElement("init");
			initExpression.serialize(temp);
		}
		if (whileExpression != null) {
			IElement temp = element.newElement("while");
			whileExpression.serialize(temp);
		}
		if (doExpression != null) {
			IElement temp = element.newElement("do");
			doExpression.serialize(temp);
		}
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		super.setContext(context);
		if (getInitExpression() != null) {
			getInitExpression().setContext(context);
		}
		if (getWhileExpression() != null) {
			getWhileExpression().setContext(context);
		}
		if (getDoExpression() != null) {
			getDoExpression().setContext(context);
		}
	}

	public void setDoExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.doExpression = step;
	}

	public void setInitExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.initExpression = step;
	}

	public void setWhileExpression(MacroExpr step) {
		if (step != null) {
			step.setScriptFunctor(this);
		}
		this.whileExpression = step;
	}

}
