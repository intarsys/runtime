/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.factory;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IActivateDeactivate;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.event.DestroyedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.event.StoppedEvent;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * An abstract superclass for implementing an {@link IFactory} that supports the
 * "singleton" pattern. Concrete subclasses must implement the strategy defining
 * the "singleness" of the created instance with regard to its context.
 */
public abstract class AbstractSingletonFactory<T> extends CommonDelegatingFactory<T> {

	class SingletonDisposalMonitor implements INotificationListener {
		private INotificationSupport singleton;
		private IAttributeSupport context;

		public SingletonDisposalMonitor(IAttributeSupport context, INotificationSupport singleton) {
			this.context = context;
			this.singleton = singleton;
			singleton.addNotificationListener(StoppedEvent.ID, this);
			singleton.addNotificationListener(DestroyedEvent.ID, this);
		}

		@Override
		public void handleEvent(Event event) {
			if (singleton != null) {
				singleton.removeNotificationListener(StoppedEvent.ID, this);
				singleton.removeNotificationListener(DestroyedEvent.ID, this);
				onDisarm(context, singleton);
			}
		}
	}

	public static final String PREF_ACTIVATE_AT_START = "activateAtStart"; //$NON-NLS-1$

	public static final String PREF_CREATE_AT_START = "createAtStart"; //$NON-NLS-1$

	private final Attribute attrSingleton = new Attribute("singleton");

	/**
	 * A flag if the singleton is activated on startup
	 */
	private boolean activateAtStart;

	/**
	 * A flag if the singleton is created at startup
	 */
	private boolean createAtStart;

	private IFactory<T> delegate;

	protected AbstractSingletonFactory() {
		super();
	}

	protected T basicCreateSingleton(IArgs args) throws ObjectCreationException {
		return getDelegate().createInstance(args);
	}

	@Override
	protected IFactory<T> basicGetDelegate() {
		return delegate;
	}

	protected void clearSingleton(IAttributeSupport singletonContext) {
		if (singletonContext == null) {
			return;
		}
		singletonContext.removeAttribute(attrSingleton);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		String delegateId = element.attributeValue("delegate", null);
		if (delegateId != null) {
			delegate = (IFactory<T>) Outlet.get().lookupFactory(delegateId);
		}
		IElement elDelegate = element.element("delegate");
		if (elDelegate != null) {
			try {
				delegate = ElementTools.createObject(elDelegate, IFactory.class, getContext(), Args.create());
			} catch (ObjectCreationException e) {
				throw new ConfigurationException(e);
			}
		}
		super.configure(element);
		setCreateAtStart(ElementTools.getBool(element, "createatstart", false));
	}

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		IAttributeSupport singletonContext = getSingletonContext(args);
		return ensureSingleton(singletonContext);
	}

	protected T createSingleton(IAttributeSupport singletonContext) throws ObjectCreationException {
		if (singletonContext == null) {
			return null;
		}
		T singleton = basicCreateSingleton(Args.create());
		if (singleton instanceof INotificationSupport) {
			new SingletonDisposalMonitor(singletonContext, (INotificationSupport) singleton);
		}
		return singleton;
	}

	public T ensureSingleton(IAttributeSupport singletonContext) throws ObjectCreationException {
		T singleton = getSingleton(singletonContext);
		if (singleton == null) {
			singleton = createSingleton(singletonContext);
			setSingleton(singletonContext, singleton);
		}
		return singleton;
	}

	@Override
	public IFactory<T> getDelegate() {
		return delegate;
	}

	public T getSingleton(IAttributeSupport singletonContext) {
		if (singletonContext == null) {
			return null;
		}
		return (T) singletonContext.getAttribute(attrSingleton);
	}

	protected abstract IAttributeSupport getSingletonContext(IArgs args);

	public boolean isActivateAtStart() {
		return activateAtStart;
	}

	public boolean isCreateAtStart() {
		return createAtStart;
	}

	/**
	 * Disarm any listeners etc.
	 * 
	 * @param context
	 *            The singleton context
	 * @param singleton
	 *            The singleton itself
	 * 
	 */
	protected void onDisarm(IAttributeSupport context, INotificationSupport singleton) {
		clearSingleton(context);
	}

	protected void onSingletonContextStarted(IAttributeSupport singletonContext) {
		Object singleton = null;
		if (isCreateAtStart()) {
			try {
				singleton = ensureSingleton(singletonContext);
			} catch (ObjectCreationException e) {
				// handle exception
			}
		}
		if ((singleton instanceof IStartStop)) {
			((IStartStop) singleton).start();
		}
		if ((singleton instanceof IActivateDeactivate) && isActivateAtStart()) {
			((IActivateDeactivate) singleton).activate();
		}
	}

	protected boolean onSingletonContextStopRequested(IAttributeSupport singletonContext) {
		Object singleton = getSingleton(singletonContext);
		setCreateAtStart(singleton != null);
		setActivateAtStart((singleton instanceof IActivateDeactivate) && ((IActivateDeactivate) singleton).isActive());
		return true;
	}

	@Override
	protected void preferencesInit(IPreferences preferences) {
		super.preferencesInit(preferences);
		preferences.put(PREF_CREATE_AT_START, isCreateAtStart());
		preferences.put(PREF_ACTIVATE_AT_START, false);
	}

	@Override
	protected void preferencesRestore(IPreferences preferences) {
		super.preferencesRestore(preferences);
		setCreateAtStart(preferences.getBoolean(PREF_CREATE_AT_START, isCreateAtStart()));
		setActivateAtStart(preferences.getBoolean(PREF_ACTIVATE_AT_START, isActivateAtStart()));
	}

	@Override
	protected void preferencesStore(IPreferences preferences) {
		super.preferencesStore(preferences);
		preferences.put(PREF_CREATE_AT_START, isCreateAtStart());
		preferences.put(PREF_ACTIVATE_AT_START, isActivateAtStart());
	}

	public void setActivateAtStart(boolean activateAtStart) {
		this.activateAtStart = activateAtStart;
	}

	public void setCreateAtStart(boolean active) {
		this.createAtStart = active;
	}

	public void setDelegate(IFactory<T> delegate) {
		this.delegate = delegate;
	}

	protected void setSingleton(IAttributeSupport singletonContext, T singleton) {
		singletonContext.setAttribute(attrSingleton, singleton);
	}

}
