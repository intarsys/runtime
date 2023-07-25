package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.functor.DeclarationFunctor;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorExecutionException;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IFunctor} based implementation for scripting components.
 * 
 */
public abstract class MacroFunctor<T> implements IFunctor<T>, IPresentationSupport, IElementConfigurable,
		IElementSerializable, IClassLoaderSupport, IContextSupport {

	private Object context;

	private MacroFunctor container;

	private String label;

	protected MacroFunctor() {
		super();
	}

	protected void associateChild(IFunctor child) {
		// workaround for declaration-enabled MacroFunctor
		if (child instanceof DeclarationFunctor) {
			child = ((DeclarationFunctor) child).getFunctor();
		}
		if (child instanceof MacroFunctor) {
			((MacroFunctor) child).setContainer(this);
		}
	}

	public String basicGetLabel() {
		return label;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setLabel(element.attributeValue("label", basicGetLabel()));
	}

	protected IFunctor createFunctor(IElement element) throws ConfigurationException {
		if (element == null) {
			return null;
		}
		try {
			return ElementTools.createFunctor(this, element, null, this);
		} catch (ObjectCreationException e) {
			throw new ConfigurationException(e);
		}
	}

	protected void disposeChild(IFunctor child) {
		if (child instanceof IDisposable) {
			((IDisposable) child).dispose();
		}
	}

	protected String getChildLabel(IFunctor child) {
		if (child instanceof IPresentationSupport) {
			return ((IPresentationSupport) child).getLabel();
		}
		if (child == null) {
			return "null";
		}
		return StringTools.safeString(child);
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	public MacroFunctor getContainer() {
		return container;
	}

	public Object getContext() {
		return context;
	}

	protected String getDefaultLabel() {
		return "Anweisung";
	}

	@Override
	public String getDescription() {
		return getTip();
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public final String getLabel() {
		String myLabel = basicGetLabel();
		if (StringTools.isEmpty(myLabel)) {
			myLabel = getDefaultLabel();
		}
		return myLabel;
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	protected Object handleControlFlow(MacroControlFlow e) throws FunctorException {
		if (isRoot()) {
			if (e instanceof Return) {
				return ((Return) e).getResult();
			}
			throw new FunctorExecutionException(new IllegalStateException("unexpected control flow " + e.getMessage()));
		}
		throw e;
	}

	protected boolean isRoot() {
		return container == null;
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		element.setAttributeValue("class", getClass().getName()); //$NON-NLS-1$
		if (basicGetLabel() != null) {
			element.setAttributeValue("label", basicGetLabel()); //$NON-NLS-1$
		}
	}

	protected void serializeChildFunctor(IFunctor functor, IElement implementorElement)
			throws ElementSerializationException {
		if (functor instanceof IElementSerializable) {
			((IElementSerializable) functor).serialize(implementorElement);
		} else {
			if (functor != null) {
				implementorElement.setAttributeValue("class", functor.getClass().getName());
			}
		}
	}

	protected void setChildContext(IFunctor step, Object context) throws ConfigurationException {
		if (step instanceof IContextSupport) {
			((IContextSupport) step).setContext(context);
		}
	}

	public void setContainer(MacroFunctor container) {
		this.container = container;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
