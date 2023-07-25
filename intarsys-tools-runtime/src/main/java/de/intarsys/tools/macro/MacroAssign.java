package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * A single "execution" step in a {@link MacroFunctor}.
 * 
 * This wraps an {@link IFunctor} and its definition context.
 * 
 */
public class MacroAssign<T> extends MacroFunctor<T> {

	private IFunctor<T> valueExpression;

	private String assignTo;

	public MacroAssign() {
		super();
	}

	public MacroAssign(IFunctor<T> functor, String assignTo) {
		super();
		this.valueExpression = functor;
		this.assignTo = assignTo;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		setValueExpression(createImplementor(element));
		setAssignTo(element.attributeValue("assignTo", getAssignTo()));
	}

	protected IFunctor createImplementor(IElement element) throws ConfigurationException {
		IElement implementorElement = element.element("value"); //$NON-NLS-1$
		if (implementorElement != null) {
			try {
				return ElementTools.createFunctor(null, implementorElement, null, getContext());
			} catch (ObjectCreationException e) {
				throw new ConfigurationException(e);
			}
		}
		return null;
	}

	public void dispose() throws FunctorException {
		disposeChild(valueExpression);
	}

	public String getAssignTo() {
		return assignTo;
	}

	@Override
	protected String getDefaultLabel() {
		StringBuilder sb = new StringBuilder();
		if (!StringTools.isEmpty(assignTo)) {
			sb.append(assignTo);
			sb.append(" = ");
		}
		sb.append(getChildLabel(valueExpression));
		return sb.toString();
	}

	public IFunctor getValueExpression() {
		return valueExpression;
	}

	@Override
	public T perform(IFunctorCall call) throws FunctorException {
		T result = null;
		if (valueExpression != null) {
			try {
				result = valueExpression.perform(call);
			} catch (MacroControlFlow e) {
				return (T) handleControlFlow(e);
			}
		}
		if (!StringTools.isEmpty(getAssignTo())) {
			call.getArgs().put(getAssignTo(), result);
		}
		return result;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		super.serialize(element);
		element.setAttributeValue("assignTo", getAssignTo());
		IElement implementorElement = element.newElement("value"); //$NON-NLS-1$
		serializeChildFunctor(getValueExpression(), implementorElement);
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	public void setValueExpression(IFunctor functor) {
		this.valueExpression = functor;
		associateChild(functor);
	}

}
