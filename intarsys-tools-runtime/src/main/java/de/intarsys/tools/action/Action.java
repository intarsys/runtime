/*
 * Copyright (c) 2007, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.action;

import javax.annotation.PostConstruct;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.category.CategoryRegistry;
import de.intarsys.tools.category.ICategory;
import de.intarsys.tools.category.ICategorySupport;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IIdentifiable;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.infoset.ElementSerializationException;
import de.intarsys.tools.infoset.IAttribute;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.infoset.IElementSerializable;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.Level;

/**
 * Abstract superclass for implementing IAction objects.
 * 
 */
public abstract class Action extends ActionAdapter implements IClassLoaderAccess, Comparable, IElementConfigurable,
		IElementSerializable, ICategorySupport, IContextSupport {

	private static final String STYLE_CHECK = "check";

	private static final String ATTR_STYLE = "style";

	private static final String ATTR_ICON = "icon";

	private static final String ATTR_DESCRIPTION = "description";

	private static final String ATTR_TIP = "tip";

	private static final String ATTR_LABEL = "label";

	private static final String ATTR_ID = "id";

	private static final String ATTR_CATEGORY = "category";

	/** Collection of generic attributes */
	private AttributeMap attributes;

	/** Flag if this action is checked (if it is a check style action) */
	private boolean checked;

	/** The class loader for accessing dependent resources */
	private ClassLoader classLoader;

	/** The complete description for the action */
	private Object description;

	/** A helper for managing the event mechanics */
	private EventDispatcher dispatcher;

	private IElement element;

	/** Flag if this action is enabled. */
	private boolean enabled = true;

	/** An optional icon for the action */
	private String iconName;

	/** The name of the action */
	private String id;

	/** A label for the action */
	private Object label;

	/**
	 * The implementation context of the {@link IAction}. This may be
	 * interpreted as the object implementing the business logic implemented by
	 * this.
	 */
	private Object owner;

	/** Flag if the action is represented by a "push" or "check" style */
	private boolean pushStyle = true;

	/** A short description for the action */
	private Object tip;

	private ICategory category;

	/**
	 * A no-arg constructor for easy reflective access
	 */
	protected Action() {
		this((String) null, null);
	}

	/**
	 * 
	 */
	protected Action(Object owner) {
		this((String) null, owner);
	}

	protected Action(Object owner, boolean checked) {
		this((String) null, owner, checked);
	}

	protected Action(String id, Object owner) {
		super();
		this.id = id == null ? getDefaultId() : id;
		setOwner(owner);
	}

	protected Action(String id, Object owner, boolean checked) {
		this(id, owner);
		setCheckStyleOn();
		setChecked(checked);
	}

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			dispatcher = new EventDispatcher(this);
		}
		dispatcher.addNotificationListener(type, listener);
	}

	/**
	 * Order is defined by id.
	 * 
	 * Note: this class has a natural ordering that is inconsistent with equals
	 */
	@Override
	public int compareTo(Object other) { // NOSONAR
		if (other instanceof IAction) {
			return getId().compareTo(((IAction) other).getId());
		}
		return 1;
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		element = pElement;
		// allow id to be null!
		setId(pElement.attributeValue(ATTR_ID, null));
		IAttribute attribute;
		attribute = element.attribute(ATTR_LABEL);
		if (attribute != null) {
			setLabel(attribute.getData());
		}
		attribute = element.attribute(ATTR_TIP);
		if (attribute != null) {
			setTip(attribute.getData());
		}
		attribute = element.attribute(ATTR_DESCRIPTION);
		if (attribute != null) {
			setDescription(attribute.getData());
		}
		setIconName(element.attributeValue(ATTR_ICON, null));
		setStyle(element.attributeValue(ATTR_STYLE, null));
		if (getCategory() == null) {
			String categoryId = pElement.attributeValue(ATTR_CATEGORY, ICategory.OTHER); // $NON-NLS-1$
			setCategory(CategoryRegistry.get().lookupCategory(categoryId));
		}
	}

	@Override
	public final Object getAttribute(Object key) {
		if (attributes == null) {
			return null;
		}
		return attributes.getAttribute(key);
	}

	@Override
	public ICategory getCategory() {
		return category;
	}

	@Override
	public ClassLoader getClassLoader() {
		ClassLoader result = classLoader;
		if (result == null) {
			if (getOwner() == null) {
				result = getClass().getClassLoader();
			} else {
				result = getOwner().getClass().getClassLoader();
			}
		}
		return result;
	}

	protected String getDefaultDescription() {
		return getTip();
	}

	private String getDefaultId() {
		return getClass().getName();
	}

	protected String getDefaultLabel() {
		if (getId() == null) {
			return "No Name";
		}
		return getId();
	}

	protected String getDefaultTip() {
		return getLabel();
	}

	@Override
	public String getDescription() {
		if (description == null) {
			// support tip change, do not cache
			return getDefaultDescription();
		}
		if (description instanceof IMessage) {
			return ((IMessage) description).getString();
		}
		return (String) description;
	}

	public IElement getElement() {
		return element;
	}

	@Override
	public String getIconName() {
		return iconName;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getLabel() {
		if (label == null) {
			return getDefaultLabel();
		}
		if (label instanceof IMessage) {
			return ((IMessage) label).getString();
		}
		return (String) label;
	}

	public Object getOwner() {
		return owner;
	}

	protected String getSerializeIconName() {
		return iconName;
	}

	protected String getSerializeId() {
		return id;
	}

	@Override
	public String getTip() {
		if (tip == null) {
			// support change, do not cache
			return getDefaultTip();
		}
		if (tip instanceof IMessage) {
			return ((IMessage) tip).getString();
		}
		return (String) tip;
	}

	@Override
	public boolean isChecked(IFunctorCall call) {
		return checked;
	}

	@Override
	public boolean isCheckStyle() {
		return !isPushStyle();
	}

	@Override
	public boolean isEnabled(IFunctorCall call) {
		return enabled;
	}

	@Override
	public boolean isPushStyle() {
		return pushStyle;
	}

	public Object perform() throws FunctorException {
		return null;
	}

	@Override
	public Object perform(IFunctorCall call) throws FunctorException {
		return perform();
	}

	@PostConstruct
	public void register() {
		String myId = getId();
		if (ActionRegistry.get().lookupAction(myId) != null) {
			PACKAGE.Log.log(Level.WARN, "action '" + myId + "' redefined");
		}
		ActionRegistry.get().registerAction(this);
	}

	@Override
	public final Object removeAttribute(Object key) {
		if (attributes == null) {
			return null;
		}
		Object result = attributes.removeAttribute(key);
		triggerChange(key);
		return result;
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.removeNotificationListener(type, listener);
	}

	@Override
	public void serialize(IElement element) throws ElementSerializationException {
		// todo should not set the role here
		element.setName("object");
		element.setAttributeValue("class", getClass().getName());
		element.setAttributeValue(ATTR_ID, getId());
		if (label != null) {
			element.setAttributeValue(ATTR_LABEL, getLabel());
		}
		if (tip != null) {
			element.setAttributeValue(ATTR_TIP, getTip());
		}
		if (description != null) {
			element.setAttributeValue(ATTR_DESCRIPTION, getDescription());
		}
		if (!StringTools.isEmpty(getIconName())) {
			element.setAttributeValue(ATTR_ICON, getIconName());
		}
		if (isCheckStyle()) {
			element.setAttributeValue(ATTR_STYLE, STYLE_CHECK);
		}
		if (getCategory() != null) {
			element.setAttributeValue(ATTR_CATEGORY, getCategory().getId());
		}
	}

	@Override
	public final Object setAttribute(Object key, Object value) {
		if (attributes == null) {
			attributes = new AttributeMap(1);
		}
		Object result = attributes.setAttribute(key, value);
		triggerChange(key);
		return result;
	}

	public void setCategory(ICategory category) {
		this.category = category;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		triggerChange(IAction.ATTR_CHECKED);
	}

	public void setCheckStyleOn() {
		pushStyle = false;
		triggerChange(IAction.ATTR_STYLE);
	}

	/**
	 * Set the class loader to be used with this action. This should be the
	 * class loader instance that would give access to all resources needed by
	 * the action's business logic implementation.
	 * 
	 * @param classLoader
	 *            The class loader to be used to access the resources needed by
	 *            the action's business logic implementation.
	 */
	@Override
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		setOwner(context);
	}

	public void setDescription(Object description) {
		this.description = description;
		triggerChange(IPresentationSupport.ATTR_DESCRIPTION);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		triggerChange(IAction.ATTR_ENABLED);
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
		triggerChange(IPresentationSupport.ATTR_ICON);
	}

	public void setId(String id) {
		this.id = id;
		triggerChange(IIdentifiable.ATTR_ID);
	}

	public void setLabel(Object label) {
		this.label = label;
		triggerChange(IPresentationSupport.ATTR_LABEL);
	}

	/**
	 * This may be used in conjunction with the no-arg constructor.
	 * 
	 * @param owner
	 */
	public void setOwner(Object owner) {
		if (this.owner != null) {
			throw new InvalidRequestException("can not reassign owner"); //$NON-NLS-1$
		}
		this.owner = owner;
	}

	public void setPushStyleOn() {
		pushStyle = true;
		triggerChange(IAction.ATTR_STYLE);
	}

	public void setStyle(String style) {
		if (style == null) {
			return;
		}
		if (style.indexOf(STYLE_CHECK) != -1) { // $NON-NLS-1$
			setCheckStyleOn();
		}
	}

	public void setTip(Object shortDescription) {
		this.tip = shortDescription;
		triggerChange(IPresentationSupport.ATTR_TIP);
	}

	@Override
	public String toString() {
		try {
			return "Action '" + getId() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		} catch (RuntimeException e) {
			return "<unprintable Action>"; //$NON-NLS-1$
		}
	}

	@Override
	public void touch() {
		triggerChange(null);
	}

	protected void triggerChange(Object attribute) {
		if (dispatcher == null) {
			return;
		}
		AttributeChangedEvent event = new AttributeChangedEvent(this, attribute, null, null);
		triggerEvent(event);
	}

	protected void triggerEvent(Event event) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.triggerEvent(event);
	}

}
