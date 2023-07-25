/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.factory;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.infoset.ElementProxy;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.proxy.IProxy;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * An {@link IFactory} acting as a lazy proxy to the real {@link IFactory} for
 * the sake of performance. The realization of the real factory is deferred so
 * startup time is minimized. Unused factories may never be instantiated at all.
 * 
 */
public class DeferredFactory extends CommonDelegatingFactory implements IProxy {

	private static final ILogger Log = PACKAGE.Log;

	private String resultClassName;

	private Class resultClass;

	private IProxy proxy;

	private IFactory realized;

	public DeferredFactory(Object context, IElement element) throws ObjectCreationException {
		super();
		try {
			setContext(context);
			configure(element);
		} catch (ConfigurationException e) {
			throw new ObjectCreationException(e);
		}
	}

	@Override
	protected IFactory basicGetDelegate() {
		return realized;
	}

	protected Object basicGetRealized() {
		return realized;
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		super.configure(element);
		String tempId = element.attributeValue("id", null); //$NON-NLS-1$
		if (tempId == null) {
			tempId = element.attributeValue("class", null); //$NON-NLS-1$
		}
		setId(tempId);
		resultClassName = element.attributeValue("resultClass", null);
		proxy = new ElementProxy(IFactory.class, element, getProxyClassAttribute(), getContext());
	}

	@Override
	public Object getActive() {
		if (basicGetRealized() == null) {
			return null;
		}
		return super.getActive();
	}

	@Override
	public synchronized IFactory getDelegate() {
		if (realized == null) {
			try {
				realized = (IFactory) proxy.getRealized();
			} catch (final Throwable t) {
				Log.warn("{} error creating delegate factory", getLogLabel(), t);
				realized = new SimpleSingletonFactory(getResultType(), null);
			}
			if (realized instanceof INotificationSupport) {
				getDispatcher().attach((INotificationSupport) realized);
			}
			if (isStarted() && realized instanceof IStartStop) {
				((IStartStop) realized).start();
			}
		}
		return realized;
	}

	@Override
	public List getInstances() {
		if (basicGetRealized() == null) {
			return new ArrayList<>();
		}
		return super.getInstances();
	}

	protected Object getLogLabel() {
		return toString() + " defined in " + getContext();
	}

	protected String getProxyClassAttribute() {
		return "class";
	}

	@Override
	public Object getRealized() {
		return getDelegate();
	}

	protected String getResultClassName() {
		return resultClassName;
	}

	@Override
	public synchronized Class getResultType() {
		if (basicGetRealized() == null) {
			// the service variable may be initialized lazy by a worker thread
			if (resultClass == null) {
				if (getResultClassName() == null) {
					resultClass = Object.class;
				} else {
					try {
						resultClass = ClassTools.createClass(getResultClassName(), Object.class, getClassLoader());
					} catch (Throwable t) {
						// prepare for errors when loading class
						Log.log(Level.WARN, t.getMessage(), t);
						resultClass = Object.class;
					}
				}
			}
			return resultClass;
		}
		return super.getResultType();
	}

	@Override
	public int size() {
		if (basicGetRealized() == null) {
			return 0;
		}
		return super.size();
	}
}
