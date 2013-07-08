package de.intarsys.tools.macro;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.IClassLoaderSupport;

/**
 * An {@link IFunctor} based implementation for scripting components.
 * 
 */
abstract public class MacroFunctor<T> implements IFunctor<T>,
		IPresentationSupport, IElementConfigurable, IElementSerializable,
		IClassLoaderSupport, IContextSupport, IDisposable {

	private Object context;

	private boolean disposed = false;

	private boolean root = true;

	public MacroFunctor() {
		super();
	}

	protected void basicDispose() {
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
	}

	protected MacroExpr createScriptExpr(IElement element)
			throws ConfigurationException {
		if (element == null) {
			return null;
		}
		MacroExpr step = new MacroExpr();
		step.configure(element);
		step.setScriptFunctor(this);
		return step;
	}

	@Override
	final public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		basicDispose();
	}

	@Override
	public ClassLoader getClassLoader() {
		return null;
	}

	public Object getContext() {
		return context;
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
	public String getLabel() {
		return "Anweisung";
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	protected boolean isRoot() {
		return root;
	}

	@Override
	public void serialize(IElement element)
			throws ElementSerializationException {
		element.setAttributeValue("class", getClass().getName()); //$NON-NLS-1$
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
	}

	protected void setRoot(boolean root) {
		this.root = root;
	}
}
