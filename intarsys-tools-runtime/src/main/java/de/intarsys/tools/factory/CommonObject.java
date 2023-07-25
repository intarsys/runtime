package de.intarsys.tools.factory;

import java.io.IOException;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgsAccess;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.locator.ILocatorFactorySupport;
import de.intarsys.tools.locator.LocatorFactory;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.reflect.ObjectTools;

/**
 * A common base class to simplify factory based creation of "complex" objects.
 * 
 */
public class CommonObject implements IFactorySupport, IAttributeSupport, IElementConfigurable, IArgsAccess,
		INotificationSupport, IContextSupport {

	private IArgs args;

	private Object context;

	private final AttributeMap attributes = new AttributeMap();

	private IElement configuration;

	private final String autoid;

	private IFactory factory;

	private int serial;

	/** A helper object for the event mechanics */
	private final EventDispatcher dispatcher = new EventDispatcher(this);

	public CommonObject() {
		autoid = ObjectTools.createLabel(this);
	}

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.addNotificationListener(type, listener);
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		this.configuration = pElement;
	}

	/**
	 * This utility method resolves a name to an {@link ILocator}.
	 * 
	 * If the associated factory can resolve names locally (implements
	 * {@link ILocatorFactorySupport}, we first ask the factory for local
	 * resolving.
	 * 
	 * If local resolving is not available or fails, global resolving is
	 * applied.
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public ILocator createLocatorGlobal(String name) throws IOException {
		ILocatorFactory locatorFactory = LocatorTools.createLookupFactory(getContext(), getFactory(),
				LocatorFactory.get());
		return locatorFactory.createLocator(name);
	}

	@Override
	public IArgs getArgs() {
		return args;
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	protected AttributeMap getAttributes() {
		return attributes;
	}

	public String getAutoid() {
		return autoid;
	}

	protected IElement getConfiguration() {
		return configuration;
	}

	public Object getContext() {
		return context;
	}

	protected EventDispatcher getDispatcher() {
		return dispatcher;
	}

	@Override
	public IFactory getFactory() {
		return factory;
	}

	public String getLogLabel() {
		return autoid;
	}

	public int getSerial() {
		return serial;
	}

	public Object myself() {
		return this;
	}

	@Override
	public Object removeAttribute(Object key) {
		Object result = attributes.remove(key);
		Event event = new AttributeChangedEvent(this, key, result, null);
		triggerEvent(event);
		return result;
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.removeNotificationListener(type, listener);
	}

	@Override
	public void setArgs(IArgs args) {
		if (this.args != null) {
			throw new InvalidRequestException();
		}
		this.args = args;
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		Object result = attributes.put(key, value);
		if (result != value) {
			Event event = new AttributeChangedEvent(this, key, result, value);
			triggerEvent(event);
		}
		return result;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
	}

	public void setFactory(IFactory factory) {
		if (this.factory != null) {
			throw new InvalidRequestException();
		}
		this.factory = factory;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	@Override
	public String toString() {
		return getLogLabel();
	}

	protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
		Event event = new AttributeChangedEvent(this, attribute, oldValue, newValue);
		triggerEvent(event);
	}

	protected void triggerEvent(Event event) {
		dispatcher.triggerEvent(event);
	}

}
