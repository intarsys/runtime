package de.intarsys.tools.factory;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.event.CreatedEvent;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.proxy.IProxy;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * An {@link IFactory} to handle aliases. The aliased {@link IFactory} is
 * wrapped, requests are forwarded.
 * 
 */
public class AliasFactory<T> extends CommonFactory<T> implements
		IProxy, IBookkeepingFactory<T> {

	private IFactory<T> delegate;
	private INotificationListener<CreatedEvent> listenDelegateCreate = new INotificationListener<CreatedEvent>() {
		@Override
		public void handleEvent(CreatedEvent delegateEvent) {
			CreatedEvent event = new CreatedEvent(AliasFactory.this);
			event.setInstance(delegateEvent.getInstance());
			AliasFactory.this.triggerEvent(event);
			delegateEvent.setVeto(event.isVetoed());
			if (event.isConsumed()) {
				event.consume();
			}
		}
	};
	private int armed = 0;
	private Object armedLock = new Object();

	public AliasFactory() {
		//
	}

	public AliasFactory(IFactory delegate, String id) {
		super();
		setId(id);
		this.delegate = delegate;
	}

	protected void arm() {
		synchronized (armedLock) {
			if (armed == 0) {
				if (basicGetDelegate() instanceof INotificationSupport) {
					((INotificationSupport) basicGetDelegate())
							.addNotificationListener(CreatedEvent.ID, listenDelegateCreate);
				}
			}
			armed++;
		}
	}

	protected IFactory basicGetDelegate() {
		return delegate;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		String delegateId = element.attributeValue("delegate", null);
		if (delegateId != null) {
			delegate = (IFactory<T>) Outlet.get().lookupFactory(delegateId);
		}
		IElement elDelegate = getConfiguration().element("delegate");
		if (elDelegate != null) {
			try {
				delegate = ElementTools.createObject(elDelegate,
						IFactory.class, getContext(), Args.create());
			} catch (ObjectCreationException e) {
				throw new ConfigurationException(e);
			}
		}
	}

	@Override
	public T createInstance(IArgs args) throws ObjectCreationException {
		arm();
		try {
			return getDelegate().createInstance(args);
		} finally {
			disarm();
		}
	}

	protected synchronized void disarm() {
		synchronized (armedLock) {
			if (armed == 0) {
				return;
			}
			if (armed == 1) {
				if (basicGetDelegate() instanceof INotificationSupport) {
					((INotificationSupport) basicGetDelegate())
							.removeNotificationListener(CreatedEvent.ID, listenDelegateCreate);
				}
			}
			armed--;
		}
	}

	@Override
	public T getActive() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory<T>) getDelegate()).getActive();
		}
		return null;
	}

	@Override
	public final Object getAttribute(Object key) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate()).getAttribute(key);
		}
		return super.getAttribute(key);
	}

	public IFactory<T> getDelegate() {
		return basicGetDelegate();
	}

	@Override
	public String getDescription() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getDescription();
		}
		return super.getDescription();
	}

	@Override
	public String getIconName() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getIconName();
		}
		return super.getIconName();
	}

	@Override
	public List getInstances() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory) getDelegate()).getInstances();
		}
		return new ArrayList<>();
	}

	@Override
	public String getLabel() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getLabel();
		}
		return super.getLabel();
	}

	@Override
	public Object getRealized() {
		return getDelegate();
	}

	@Override
	public Class<T> getResultType() {
		return (Class<T>) getDelegate().getResultType();
	}

	@Override
	public String getTip() {
		if (getDelegate() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getDelegate()).getTip();
		}
		return super.getTip();
	}

	@Override
	public final Object removeAttribute(Object key) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate())
					.removeAttribute(key);
		}
		return super.removeAttribute(key);
	}

	@Override
	public final Object setAttribute(Object key, Object value) {
		if (basicGetDelegate() instanceof IAttributeSupport) {
			return ((IAttributeSupport) basicGetDelegate()).setAttribute(key,
					value);
		}
		return super.setAttribute(key, value);
	}

	@Override
	public int size() {
		if (getDelegate() instanceof IBookkeepingFactory) {
			return ((IBookkeepingFactory) getDelegate()).size();
		}
		return 0;
	}

}
