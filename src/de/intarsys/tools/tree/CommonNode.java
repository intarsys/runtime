package de.intarsys.tools.tree;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.DestroyedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.presentation.IPresentationSupport;

/**
 * A common implementation for a node in a tree. This node wraps a POJO for use
 * in presentation or other contexts.
 * <p>
 * The node factory acts as the "role" in which the POJO wants to be wrapped or
 * seen. This allows a POJO to be seen differently in different usage contexts
 * by simply using another factory (role).
 * <p>
 * There are two main usage scenarios:
 * <p>
 * Implement a concrete {@link CommonNode} and {@link CommonNodeFactory}
 * subclass to determine node behavior. This results in quick and easy to
 * understand solution. The drawback is the static behavior - if you want to
 * change some implementation detail down the hierarchy level you have to create
 * subclasses according to this change up to the root level to ensure the
 * changed leaf node factory is used.
 * <p>
 * The second scenario involves a callback to a strategy object
 * {@link INodeHandler} where the node behavior is encapsulated. This allows for
 * a more generic node implementation with the drawback of a "instanceof" style
 * of coding in the callback handler.
 * 
 * @param <T>
 *            The type of the wrapped POJO
 */
abstract public class CommonNode<T> implements IPresentationSupport,
		INotificationSupport, IAttributeSupport {

	private static final CommonNode<?>[] NODES_EMPTY = new CommonNode[0];

	/**
	 * Return the node associated with object. If no such node exists it will be
	 * created as a child of parent.
	 * <p>
	 * The strategy of associating an object o a node is up to the factory.
	 * 
	 * @param parent
	 * @param role
	 * @param object
	 * @return Return the UNIQUE node associated with object.
	 */
	synchronized public static CommonNode<?> getNode(CommonNode<?> parent,
			CommonNodeFactory<?> role, Object object) {
		CommonNode<?> result = role.lookupNode(parent, object);
		if (result == null) {
			CommonNodeFactory<?> factory = role.lookupFactory(object);
			result = factory.createNode(parent, object);
			role.registerNode(parent, result);
		}
		return result;
	}

	private AttributeMap attributes;

	private CommonNode<?>[] cachedChildren;

	final private EventDispatcher eventDispatcher;

	final private INotificationListener listenObjectChange = new INotificationListener() {
		public void handleEvent(Event event) {
			onEvent(event);
		}
	};

	final private INotificationListener listenObjectDestroy = new INotificationListener() {
		public void handleEvent(Event event) {
			onDestroy(event);
		}
	};

	final private T object;

	final private CommonNode<?> parent;

	private INodeHandler nodeHandler;

	protected CommonNode(CommonNode<?> parent, T object) {
		super();
		this.parent = parent;
		if (parent != null) {
			this.nodeHandler = parent.nodeHandler;
		}
		this.object = object;
		this.eventDispatcher = new EventDispatcher(this);
		arm();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.event.INotificationSupport#addNotificationListener(
	 * de.intarsys.tools.event.EventType,
	 * de.intarsys.tools.event.INotificationListener)
	 */
	public void addNotificationListener(EventType type,
			INotificationListener listener) {
		eventDispatcher.addNotificationListener(type, listener);
	}

	protected void arm() {
		if (object instanceof INotificationSupport) {
			((INotificationSupport) object).addNotificationListener(
					AttributeChangedEvent.ID, listenObjectChange);
			((INotificationSupport) object).addNotificationListener(
					DestroyedEvent.ID, listenObjectDestroy);
		}
	}

	protected CommonNode<?>[] basicCreateChildren() {
		return NODES_EMPTY;
	}

	protected String basicGetDescription() {
		return getTip();
	}

	protected String basicGetIconName() {
		return null;
	}

	protected String basicGetLabel() {
		return object.toString();
	}

	protected String basicGetObjectDescription() {
		if (getObject() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getObject()).getDescription();
		} else {
			return basicGetDescription();
		}
	}

	protected String basicGetObjectIconName() {
		if (getObject() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getObject()).getIconName();
		} else {
			return basicGetIconName();
		}
	}

	protected String basicGetObjectLabel() {
		if (getObject() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getObject()).getLabel();
		} else {
			return basicGetLabel();
		}
	}

	protected String basicGetObjectTip() {
		if (getObject() instanceof IPresentationSupport) {
			return ((IPresentationSupport) getObject()).getTip();
		} else {
			return basicGetTip();
		}
	}

	protected String basicGetTip() {
		return getLabel();
	}

	protected boolean basicHasChildren() {
		return cachedChildren == null || cachedChildren.length != 0;
	}

	protected void disarm() {
		if (object instanceof INotificationSupport) {
			((INotificationSupport) object).removeNotificationListener(
					AttributeChangedEvent.ID, listenObjectChange);
			((INotificationSupport) object).removeNotificationListener(
					DestroyedEvent.ID, listenObjectDestroy);
		}
	}

	/**
	 * Dispose all resources and associations. This object is not reused any
	 * more
	 */
	protected void dispose() {
		disarm();
		disposeChildren();
		if (attributes != null) {
			attributes.clear();
		}
		if (eventDispatcher != null) {
			eventDispatcher.clear();
		}
	}

	protected void disposeChildren() {
		if (cachedChildren == null) {
			return;
		}
		for (CommonNode<?> child : cachedChildren) {
			child.dispose();
		}
		updateChildren();
	}

	synchronized public Object getAttribute(Object key) {
		if (attributes == null) {
			return null;
		}
		return attributes.getAttribute(key);
	}

	/**
	 * Return all child nodes of this.
	 * 
	 * @return Return all child nodes of this.
	 */
	public CommonNode<?>[] getChildren() {
		if (cachedChildren == null) {
			if (nodeHandler == null) {
				cachedChildren = basicCreateChildren();
			} else {
				cachedChildren = nodeHandler.createChildren(this);
			}
		}
		return cachedChildren;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.presentation.IPresentationSupport#getDescription()
	 */
	public String getDescription() {
		if (nodeHandler != null) {
			return nodeHandler.getDescription(this);
		}
		return basicGetObjectDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.presentation.IPresentationSupport#getIconName()
	 */
	public String getIconName() {
		if (nodeHandler != null) {
			return nodeHandler.getIconName(this);
		}
		return basicGetObjectIconName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.presentation.IPresentationSupport#getLabel()
	 */
	public String getLabel() {
		if (nodeHandler != null) {
			return nodeHandler.getLabel(this);
		}
		return basicGetObjectLabel();
	}

	public INodeHandler getNodeHandler() {
		return nodeHandler;
	}

	/**
	 * The object represented by this node.
	 * 
	 * @return The object represented by this node.
	 */
	public T getObject() {
		return object;
	}

	/**
	 * The optional parent node.
	 * 
	 * @return The optional parent node.
	 */
	public CommonNode<?> getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.presentation.IPresentationSupport#getTip()
	 */
	public String getTip() {
		if (nodeHandler != null) {
			return nodeHandler.getTip(this);
		}
		return basicGetObjectTip();
	}

	/**
	 * <code>true</code> if this node has children.
	 * 
	 * @return <code>true</code> if this node has children.
	 */
	public boolean hasChildren() {
		if (nodeHandler == null) {
			return basicHasChildren();
		} else {
			return nodeHandler.hasChildren(this);
		}
	}

	protected boolean isReusable() {
		return true;
	}

	protected void onAttributeChanged(AttributeChangedEvent event) {
		// redefine to update whatever attribute / children have changed...
		triggerChange("label", null, null); //$NON-NLS-1$
	}

	protected void onDestroy(Event event) {
		dispose();
	}

	protected void onEvent(Event event) {
		if (event instanceof AttributeChangedEvent) {
			onAttributeChanged((AttributeChangedEvent) event);
		}
	}

	synchronized public Object removeAttribute(Object key) {
		if (attributes == null) {
			return null;
		}
		return attributes.removeAttribute(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.event.INotificationSupport#removeNotificationListener
	 * (de.intarsys.tools.event.EventType,
	 * de.intarsys.tools.event.INotificationListener)
	 */
	public void removeNotificationListener(EventType type,
			INotificationListener listener) {
		eventDispatcher.removeNotificationListener(type, listener);
	}

	synchronized public Object setAttribute(Object key, Object value) {
		if (attributes == null) {
			attributes = new AttributeMap();
		}
		return attributes.setAttribute(key, value);
	}

	public void setNodeHandler(INodeHandler nodeHandler) {
		this.nodeHandler = nodeHandler;
		unlinkChildren();
	}

	protected void triggerChange(Object attribute, Object oldValue,
			Object newValue) {
		eventDispatcher.triggerEvent(new AttributeChangedEvent(this, attribute,
				oldValue, newValue));
	}

	protected void unlink() {
		if (!isReusable()) {
			dispose();
		}
	}

	protected void unlinkChildren() {
		if (cachedChildren == null) {
			return;
		}
		for (CommonNode<?> child : cachedChildren) {
			child.unlink();
		}
		updateChildren();
	}

	protected void updateChildren() {
		if (cachedChildren == null) {
			return;
		}
		cachedChildren = null;
		triggerChange("children", null, null); //$NON-NLS-1$
	}
}
