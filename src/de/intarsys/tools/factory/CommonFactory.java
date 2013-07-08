package de.intarsys.tools.factory;

import java.io.File;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.category.CategoryRegistry;
import de.intarsys.tools.category.ICategory;
import de.intarsys.tools.category.ICategorySupport;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IInitializeable;
import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IAttribute;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.locator.ILocatorFactorySupport;
import de.intarsys.tools.locator.LocatorFactory;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.message.Message;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.message.MessageBundleTools;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.tag.TagTools;

/**
 * A common versatile base class for implementing {@link IFactory}.
 * 
 * @param <T>
 */
abstract public class CommonFactory<T> implements IFactory<T>,
		IPresentationSupport, IElementConfigurable, IContextSupport,
		IClassLoaderSupport, ILocatorFactorySupport, IFileEnvironment,
		ICategorySupport, IAttributeSupport {

	static {
		new CanonicalFromFactoryConverter();
	}

	private ICategory category;

	private ClassLoader classLoader;

	private Object description;

	private IElement configuration;

	private IElement instanceConfiguration;

	final private ILocatorFactory locatorFactory;

	private String iconName;

	private String id;

	private Object label;

	private Object tip;

	private Object context;

	final private AttributeMap attributes = new AttributeMap();

	public static final String ARG_CONFIGURATION = "configuration";

	public static final String ARG_CONTEXT = "context";

	public static final String ARG_CLASSLOADER = "classLoader";

	protected CommonFactory() {
		locatorFactory = LocatorTools.createLocalLocatorFactory(this,
				LocatorFactory.get());
	}

	protected T basicCreateInstance(IArgs args) throws ObjectCreationException {
		return null;
	}

	protected void basicCreateInstanceConfig(T object, IArgs args)
			throws ObjectCreationException {
		try {
			ClassLoader classLoader = getClassLoader(args);
			boolean init = false;
			// apply the default factory based configuration
			if (getInstanceConfiguration() != null) {
				init = true;
				if (object instanceof IElementConfigurable) {
					((IElementConfigurable) object)
							.configure(getInstanceConfiguration());
				}
				ElementTools.setProperties(object, getInstanceConfiguration(),
						classLoader);
			}
			IElement configuration = getConfiguration(args);
			// apply the instance specific configuration from the arguments
			if (configuration != null) {
				init = true;
				if (object instanceof IElementConfigurable) {
					((IElementConfigurable) object).configure(configuration);
				}
				ElementTools.setProperties(object, configuration, classLoader);
			}
			if (init && object instanceof IInitializeable) {
				((IInitializeable) object).initializeAfterConstruction();
			}
		} catch (ObjectCreationException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
	}

	protected void basicCreateInstanceInit(T object, IArgs args)
			throws ObjectCreationException {
		ClassLoader classLoader = getClassLoader(args);
		if (classLoader != null && object instanceof IClassLoaderAccess) {
			((IClassLoaderAccess) object).setClassLoader(classLoader);
		}
		Object context = getContext(args);
		if (context != null && object instanceof IContextSupport) {
			try {
				((IContextSupport) object).setContext(context);
			} catch (ConfigurationException e) {
				throw new ObjectCreationException(e);
			}
		}
		if (object instanceof IInitializeable) {
			((IInitializeable) object).initializeAfterCreation();
		}
	}

	protected String basicGetDescription() {
		MessageBundle bundle = MessageBundleTools
				.getMessageBundle(getMessageBundleClass());
		String key = ClassTools.getUnqualifiedName(getMessageBundleClass())
				+ ".description";
		if (bundle.basicGetString(key) != null) {
			return bundle.getMessage(key).get();
		}
		return getTip();
	}

	protected String basicGetLabel() {
		MessageBundle bundle = MessageBundleTools
				.getMessageBundle(getMessageBundleClass());
		return bundle.getMessage(
				ClassTools.getUnqualifiedName(getMessageBundleClass())
						+ ".label").get();
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		this.configuration = pElement;
		String tempId = configuration.attributeValue("id", null); //$NON-NLS-1$
		if (tempId != null) {
			setId(tempId);
		}
		if (getCategory() == null) {
			String categoryId = configuration.attributeValue("category", null); //$NON-NLS-1$
			setCategory(CategoryRegistry.get().lookupCategory(categoryId));
		}
		setInstanceConfiguration(configuration.element("instanceConfiguration"));
		//
		setIconName(configuration.attributeValue("icon", null));
		IAttribute attribute;
		attribute = configuration.attribute("label");
		if (attribute != null) {
			setLabel(attribute.getData());
		}
		attribute = configuration.attribute("tip");
		if (attribute != null) {
			setTip(attribute.getData());
		}
		attribute = configuration.attribute("description");
		if (attribute != null) {
			setDescription(attribute.getData());
		}
		TagTools.configureTags(this, pElement);
	}

	@Override
	final public T createInstance(IArgs args) throws ObjectCreationException {
		T object = basicCreateInstance(args);
		basicCreateInstanceInit(object, args);
		basicCreateInstanceConfig(object, args);
		return object;
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	@Override
	public File getBaseDir() {
		return getFileEnvironment().getBaseDir();
	}

	@Override
	public ICategory getCategory() {
		return category;
	}

	@Override
	public ClassLoader getClassLoader() {
		if (classLoader != null) {
			return classLoader;
		}
		if (context instanceof IClassLoaderSupport) {
			return ((IClassLoaderSupport) context).getClassLoader();
		}
		return getClass().getClassLoader();
	}

	protected ClassLoader getClassLoader(IArgs args) {
		Object result = args.get(ARG_CLASSLOADER);
		return result instanceof ClassLoader ? (ClassLoader) result
				: getClassLoader();
	}

	protected IElement getConfiguration(IArgs args) {
		Object result = args.get(ARG_CONFIGURATION);
		return result instanceof IElement ? (IElement) result : null;
	}

	public Object getContext() {
		return context;
	}

	protected Object getContext(IArgs args) {
		Object result = args.get(ARG_CONTEXT);
		return result == null ? getContext() : result;
	}

	protected String getDefaultId() {
		return getClass().getName();
	}

	@Override
	public String getDescription() {
		if (description == null) {
			// support tip change, do not cache
			return basicGetDescription();
		}
		if (description instanceof Message) {
			description = ((Message) description).get();
		}
		return (String) description;
	}

	protected IFileEnvironment getFileEnvironment() {
		if (context instanceof IFileEnvironment) {
			return (IFileEnvironment) context;
		}
		return FileEnvironment.get();
	}

	@Override
	public String getIconName() {
		return iconName;
	}

	@Override
	public String getId() {
		if (id == null) {
			return getDefaultId();
		}
		return id;
	}

	public IElement getInstanceConfiguration() {
		return instanceConfiguration;
	}

	@Override
	public String getLabel() {
		if (label == null) {
			// support change, do not cache
			return basicGetLabel();
		}
		if (label instanceof Message) {
			label = ((Message) label).get();
		}
		return (String) label;
	}

	@Override
	public ILocatorFactory getLocatorFactory() {
		return locatorFactory;
	}

	protected Class getMessageBundleClass() {
		return getClass();
	}

	@Override
	public File getProfileDir() {
		return getFileEnvironment().getProfileDir();
	}

	@Override
	public File getTempDir() {
		return getFileEnvironment().getTempDir();
	}

	@Override
	public String getTip() {
		if (tip == null) {
			// support change, do not cache
			return getLabel();
		}
		if (tip instanceof Message) {
			tip = ((Message) tip).get();
		}
		return (String) tip;
	}

	@Override
	public File getWorkingDir() {
		return getFileEnvironment().getWorkingDir();
	}

	@Override
	public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return attributes.setAttribute(key, value);
	}

	protected void setCategory(ICategory category) {
		this.category = category;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public void setContext(Object context) throws ConfigurationException {
		this.context = context;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	protected void setId(String id) {
		this.id = id;
	}

	public void setInstanceConfiguration(IElement instanceConfiguration) {
		this.instanceConfiguration = instanceConfiguration;
	}

	public void setLabel(Object label) {
		this.label = label;
	}

	public void setTip(Object tip) {
		this.tip = tip;
	}

	@Override
	public String toString() {
		return "factory " + getId(); //$NON-NLS-1$
	}

}
