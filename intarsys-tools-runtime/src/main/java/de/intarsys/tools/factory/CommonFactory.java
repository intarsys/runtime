package de.intarsys.tools.factory;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.category.CategoryRegistry;
import de.intarsys.tools.category.ICategory;
import de.intarsys.tools.category.ICategorySupport;
import de.intarsys.tools.category.StandardCategoryRegistry;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.event.CreatedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.locator.ILocatorFactorySupport;
import de.intarsys.tools.locator.LocatorFactory;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.IPreferencesSupport;
import de.intarsys.tools.preferences.IPreferencesSyncher;
import de.intarsys.tools.preferences.NullPreferences;
import de.intarsys.tools.preferences.PreferencesFactory;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.presentation.PresentationMixin;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.tag.TagTools;

/**
 * A common versatile base class for implementing {@link IFactory}.
 * 
 * @param <T>
 */
public abstract class CommonFactory<T> extends BasicFactory<T>
		implements IPresentationSupport, IElementConfigurable, ILocatorFactorySupport, IFileEnvironment,
		ICategorySupport, IAttributeSupport, INotificationSupport, IPreferencesSyncher, IPreferencesSupport {

	public static final String ARG_CONFIGURATION = "configuration";

	public static final String ARG_CONTEXT = "context";

	public static final String ARG_CLASSLOADER = "classLoader";

	private final EventDispatcher dispatcher = new EventDispatcher(this);

	private ICategory category;

	private IElement configuration;

	private final AttributeMap attributes = new AttributeMap();

	private IPreferences preferences;

	private String preferencesName;

	private final PresentationMixin presentation = new PresentationMixin(this);

	private final ILocatorFactory locatorFactoryFacade = new ILocatorFactory() {
		@Override
		public ILocator createLocator(String location) throws IOException {
			return basicCreateLocator(location);
		}
	};

	protected CommonFactory() {
	}

	@Override
	public void addNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.addNotificationListener(type, listener);
	}

	protected ILocator basicCreateLocator(String location) throws IOException {
		ILocatorFactory resultFactory = LocatorTools.createLookupFactory(getContext());
		return resultFactory.createLocator(location);
	}

	@Override
	protected void basicStop() {
		super.basicStop();
		// this should go to @PreDestroy
		preferencesStore();
	}

	@Override
	public void configure(IElement pElement) throws ConfigurationException {
		this.configuration = pElement;
		String tempId = configuration.attributeValue("id", null); //$NON-NLS-1$
		if (tempId != null) {
			setId(tempId);
		}
		// presentation mixin
		presentation.configure(configuration);
		// tag "mixin"
		TagTools.configureTags(this, configuration);
		// handle preferences declaration
		setPreferencesName(configuration.attributeValue("preferences", null));
	}

	/**
	 * This utility method resolves a name to an {@link ILocator}.
	 * 
	 * If the associated context can resolve names locally (e.g. implements
	 * {@link ILocatorFactorySupport}, we first ask the context for local
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
		ILocatorFactory locatorFactory = LocatorTools.createLookupFactory(getContext(), LocatorFactory.get());
		return locatorFactory.createLocator(name);
	}

	protected IPreferences createPreferences() {
		String name = getPreferencesName();
		if (StringTools.isEmpty(name)) {
			return NullPreferences.ACTIVE;
		} else {
			IPreferences root = PreferencesFactory.get().getRoot();
			if (name.startsWith(IPreferences.SEPARATOR)) {
				name = name.substring(1);
			}
			IPreferences result = root.node(name);
			IPreferences defaultPreferences = result.restrict(IPreferences.SCOPE_DEFAULT);
			preferencesInit(defaultPreferences);
			return result;
		}
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
		if (category == null) {
			String categoryId = configuration.attributeValue("category", null); //$NON-NLS-1$
			setCategory(CategoryRegistry.get().lookupCategory(categoryId));
			if (category == null) {
				setCategory(StandardCategoryRegistry.UNKNOWN);
			}
		}
		return category;
	}

	protected ClassLoader getClassLoader(IArgs args) {
		Object result = args.get(ARG_CLASSLOADER);
		return result instanceof ClassLoader ? (ClassLoader) result : getClassLoader();
	}

	protected IElement getConfiguration() {
		return configuration;
	}

	protected IElement getConfiguration(IArgs args) {
		Object result = args.get(ARG_CONFIGURATION);
		return result instanceof IElement ? (IElement) result : null;
	}

	protected Object getContext(IArgs args) {
		Object result = args.get(ARG_CONTEXT);
		return result == null ? getContext() : result;
	}

	@Override
	public File getDataDir() {
		return getFileEnvironment().getDataDir();
	}

	@Override
	public String getDescription() {
		return presentation.getDescription();
	}

	protected EventDispatcher getDispatcher() {
		return dispatcher;
	}

	protected IFileEnvironment getFileEnvironment() {
		if (getContext() instanceof IFileEnvironment) {
			return (IFileEnvironment) getContext();
		}
		return FileEnvironment.get();
	}

	@Override
	public String getIconName() {
		return presentation.getIconName();
	}

	@Override
	public String getLabel() {
		return presentation.getLabel();
	}

	@Override
	public ILocatorFactory getLocatorFactory() {
		return locatorFactoryFacade;
	}

	@Override
	public final IPreferences getPreferences() {
		if (preferences == null) {
			preferences = createPreferences();
		}
		return preferences;
	}

	protected String getPreferencesName() {
		return preferencesName;
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
		return presentation.getTip();
	}

	@Override
	public File getWorkingDir() {
		return getFileEnvironment().getWorkingDir();
	}

	protected void preferencesInit(IPreferences preferences) {
		//
	}

	@Override
	@PostConstruct
	public final void preferencesRestore() {
		preferencesRestore(getPreferences());
	}

	protected void preferencesRestore(IPreferences preferences) {
		//
	}

	@Override
	public final void preferencesStore() {
		preferencesStore(getPreferences());
	}

	protected void preferencesStore(IPreferences preferences) {
		//
	}

	@Override
	public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	@Override
	public void removeNotificationListener(EventType type, INotificationListener listener) {
		dispatcher.removeNotificationListener(type, listener);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		return attributes.setAttribute(key, value);
	}

	public void setCategory(ICategory category) {
		this.category = category;
	}

	protected void setPreferences(IPreferences preferences) {
		this.preferences = preferences;
	}

	protected void setPreferencesName(String preferencesName) {
		this.preferencesName = preferencesName;
	}

	@Override
	public String toString() {
		return "factory " + getId(); //$NON-NLS-1$
	}

	protected void triggerCreated(Object result) {
		CreatedEvent event = new CreatedEvent(this);
		event.setInstance(result);
		triggerEvent(event);
	}

	/**
	 * Trigger an event.
	 * 
	 * @param event
	 *            The event to be sent to listeners.
	 */
	protected void triggerEvent(Event event) {
		dispatcher.triggerEvent(event);
	}

}
