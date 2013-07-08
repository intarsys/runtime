package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * A single "execution" step in a {@link MacroFunctor}.
 * 
 * This wraps an {@link IFunctor} and its definition context.
 * 
 */
public class MacroExpr implements IElementConfigurable, IElementSerializable,
		IPresentationSupport, IContextSupport {

	private MacroFunctor scriptFunctor;

	private IFunctor functor;

	private String assignTo;

	private Object context;

	protected MacroExpr() {
		super();
	}

	public MacroExpr(IFunctor functor) {
		super();
		this.functor = functor;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setFunctor(createImplementor(element));
		setAssignTo(element.attributeValue("assignTo", getAssignTo()));
	}

	protected IFunctor createImplementor(IElement element)
			throws ConfigurationException {
		IElement implementorElement = element.element("implementor"); //$NON-NLS-1$
		if (implementorElement != null) {
			try {
				return ElementTools.createObject(implementorElement,
						IFunctor.class, getContext());
			} catch (ObjectCreationException e) {
				throw new ConfigurationException(e);
			}
		}
		return null;
	}

	public void dispose() throws FunctorInvocationException {
		if (functor == null) {
			return;
		}
		if (functor instanceof IDisposable) {
			((IDisposable) functor).dispose();
		}
	}

	public String getAssignTo() {
		return assignTo;
	}

	public Object getContext() {
		return context;
	}

	@Override
	public String getDescription() {
		return getTip();
	}

	public IFunctor getFunctor() {
		return functor;
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public String getLabel() {
		if (functor instanceof IPresentationSupport) {
			return ((IPresentationSupport) functor).getLabel();
		}
		return toString();
	}

	protected MacroFunctor getScriptFunctor() {
		return scriptFunctor;
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	public Object perform(IFunctorCall call) throws FunctorInvocationException {
		Object result = null;
		if (functor != null) {
			try {
				result = functor.perform(call);
			} catch (Return e) {
				if (getScriptFunctor().isRoot()) {
					return e.getResult();
				}
				throw e;
			}
		}
		if (!StringTools.isEmpty(getAssignTo())) {
			call.getArgs().put(getAssignTo(), result);
		}
		return result;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		IElement implementorElement = element.newElement("implementor"); //$NON-NLS-1$
		if (getFunctor() instanceof IElementSerializable) {
			((IElementSerializable) getFunctor()).serialize(implementorElement);
		} else {
			if (getFunctor() != null) {
				implementorElement.setAttributeValue("class", getFunctor() //$NON-NLS-1$
						.getClass().getName());
			}
		}
		element.setAttributeValue("assignTo", getAssignTo());
	}

	public void setAssignTo(String assignTo) {
		this.assignTo = assignTo;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
		if (functor instanceof IContextSupport) {
			((IContextSupport) functor).setContext(context);
		}
	}

	public void setFunctor(IFunctor functor) {
		this.functor = functor;
		if (functor instanceof MacroFunctor) {
			((MacroFunctor) functor).setRoot(false);
		}
	}

	protected void setScriptFunctor(MacroFunctor scriptFunctor) {
		this.scriptFunctor = scriptFunctor;
	}

}
