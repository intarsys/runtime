/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.claptz.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IInstrument;
import de.intarsys.claptz.IInstrumentPrerequisite;
import de.intarsys.claptz.IInstrumentRegistry;
import de.intarsys.claptz.InstrumentException;
import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.claptz.InstrumentResolver;
import de.intarsys.claptz.InstrumentTools;
import de.intarsys.claptz.PACKAGE;
import de.intarsys.claptz.State;
import de.intarsys.claptz.StateExcluded;
import de.intarsys.claptz.StateFailed;
import de.intarsys.claptz.StateNew;
import de.intarsys.claptz.StateStarted;
import de.intarsys.claptz.StateStopped;
import de.intarsys.claptz.io.IInstrumentStore;
import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.collection.ReverseListIterator;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.component.IMetaInfoSupport;
import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.MapResolver;
import de.intarsys.tools.expression.MessageBundleMessageResolver;
import de.intarsys.tools.expression.MessageBundleStringResolver;
import de.intarsys.tools.expression.NLSMessageResolver;
import de.intarsys.tools.expression.NLSStringResolver;
import de.intarsys.tools.infoset.ElementFactory;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.message.MessageBundleTools;
import de.intarsys.tools.string.StringTools;

/**
 * An internal representation of an {@link IInstrument}.
 * 
 */
public class StandardInstrument implements IAttributeSupport,
		IElementConfigurable {

	/**
	 * The "public" portion of the Instrument implementation, which itself is
	 * never leaked to client code.
	 * 
	 */
	public class Facade implements IInstrument {

		@Override
		public void addPrerequisite(IInstrument prerequisite,
				String absentAction) {
			StandardInstrument.this.addPrerequisite(prerequisite, absentAction);
		}

		@Override
		public IExtension createExtension(String extensionPointId, String id) {
			return StandardInstrument.this
					.createExtension(extensionPointId, id);
		}

		@Override
		public void flush() throws IOException {
			StandardInstrument.this.flush();
		}

		@Override
		final public Object getAttribute(Object key) {
			return StandardInstrument.this.getAttribute(key);
		}

		@Override
		public File getBaseDir() {
			return StandardInstrument.this.getBaseDir();
		}

		@Override
		public ClassLoader getClassLoader() {
			return StandardInstrument.this.getClassLoader();
		}

		@Override
		public IExtension[] getExtensions() {
			return StandardInstrument.this.getExtensionFacades();
		}

		@Override
		public String getId() {
			return StandardInstrument.this.getId();
		}

		@Override
		public IInstrumentRegistry getInstrumentRegistry() {
			return StandardInstrument.this.getInstrumentRegistry().getFacade();
		}

		@Override
		public MessageBundle getMessageBundle() {
			return StandardInstrument.this.getMessageBundle();
		}

		@Override
		public String getMetaInfo(String name) {
			return StandardInstrument.this.getMetaInfo(name);
		}

		protected StandardInstrument getOwner() {
			return StandardInstrument.this;
		}

		@Override
		public IInstrumentPrerequisite[] getPrerequisites() {
			return StandardInstrument.this.getPrerequisiteFacades();
		}

		@Override
		public File getProfileDir() {
			return StandardInstrument.this.getProfileDir();
		}

		@Override
		public State getState() {
			return StandardInstrument.this.getState();
		}

		@Override
		public File getTempDir() {
			return StandardInstrument.this.getTempDir();
		}

		@Override
		public File getWorkingDir() {
			return StandardInstrument.this.getWorkingDir();
		}

		@Override
		public void registerExtension(IExtension extension)
				throws InstrumentRegistryException {
			StandardInstrument.this
					.registerExtension(((StandardExtension.Facade) extension)
							.getOwner());
		}

		@Override
		final public Object removeAttribute(Object key) {
			return StandardInstrument.this.removeAttribute(key);
		}

		@Override
		public void removePrerequisite(IInstrument prerequisite) {
			StandardInstrument.this.removePrerequisite(prerequisite);
		}

		@Override
		final public Object setAttribute(Object key, Object o) {
			return StandardInstrument.this.setAttribute(key, o);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return StandardInstrument.this.toString();
		}

		@Override
		public void unregisterExtension(IExtension extension)
				throws InstrumentRegistryException {
			StandardInstrument.this
					.unregisterExtension(((StandardExtension.Facade) extension)
							.getOwner());
		}

	}

	final private IInstrumentStore store;

	/** The logger to be used in this package */
	static private final Logger Log = PACKAGE.Log;

	public static final String META_DEFINITION = "instrument.xml"; //$NON-NLS-1$

	//
	public static final String META_DIRECTORY = "INSTRUMENT-INF"; //$NON-NLS-1$

	static private final MessageBundle Msg = PACKAGE.Messages;

	final private AttributeMap attributes = new AttributeMap();

	/** The base directory from where the instrument was loaded. */
	private File baseDir;

	private String bundleName;

	private IInstrumentPrerequisite[] cachedPrerequisites;

	/** The class loader for this instrument. */
	private ClassLoader classLoader;

	private final List<StandardExtension> extensions = new ArrayList<StandardExtension>();

	/**
	 * SECURITY CONSTRAINT A facade implementation to deny access to the real
	 * implementation.
	 */
	final private IInstrument facade = new Facade();

	/**
	 * The name to be used for the instrument in the system.
	 */
	private String id;

	/**
	 * A message bundle to resolve instrument specific NLS entries.
	 */
	private MessageBundle messageBundle;

	private final Map<String, String> metaInfo = new HashMap<>();

	/**
	 * The list of prerequisites that are required to load the instrument
	 * represented by this.
	 */
	private final List<StandardInstrumentPrerequisite> prerequisites = new ArrayList<StandardInstrumentPrerequisite>();

	private final StandardInstrumentRegistry registry;

	private State state = new StateNew(""); //$NON-NLS-1$

	private String ifdef;

	private String ifnotdef;

	public static final boolean CHECK_VISIBILITY = false;

	private MapResolver expressionEvaluator;

	public static final String ACTION_DEFAULT = null;

	public static final String ACTION_FAIL = "fail"; //$NON-NLS-1$

	public static final String ACTION_IGNORE = "ignore"; //$NON-NLS-1$

	public static final String ACTION_SKIP = "skip"; //$NON-NLS-1$

	public StandardInstrument(StandardInstrumentRegistry registry,
			IInstrumentStore store) {
		super();
		this.registry = registry;
		this.store = store;
	}

	protected void addPrerequisite(IInstrument prerequisite, String absentAction) {
		StandardInstrumentPrerequisite tempPrerequisite = new StandardInstrumentPrerequisite(
				this);
		tempPrerequisite.setAbsentAction(absentAction);
		tempPrerequisite.setInstrumentId(prerequisite.getId());
		if (!InstrumentTools.isPrerequisiteImplied(getFacade(), prerequisite)) {
			addPrerequisite(tempPrerequisite);
		}
	}

	protected void addPrerequisite(StandardInstrumentPrerequisite prerequisite) {
		prerequisites.add(prerequisite);
		cachedPrerequisites = null;
	}

	protected List<StandardInstrumentPrerequisite> basicGetPrerequisites() {
		return prerequisites;
	}

	@Override
	public void configure(IElement element)
			throws ConfigurationException {
		configureInstrument(element);
		IElement eRequires = element.element("requires"); //$NON-NLS-1$
		if (eRequires != null) {
			configurePrerequisistes(eRequires);
		}
		Iterator<IElement> itExtension = element.elementIterator("extension"); //$NON-NLS-1$
		while (itExtension.hasNext()) {
			IElement eExtension = itExtension.next();
			configureExtension(eExtension);
		}
	}

	protected void configureExtension(IElement eExtension)
			throws ConfigurationException {
		String point = eExtension.attributeValue("point", null); //$NON-NLS-1$
		if (point == null) {
			throw new ConfigurationException(
					"<extension/> must define 'point' attribute"); //$NON-NLS-1$
		}
		String id = eExtension.attributeValue("id", null); //$NON-NLS-1$
		StandardExtension extension = new StandardExtension(this, point, id);
		String ifdef = eExtension.attributeValue("ifdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifdef)) {
			extension.setIfdef(ifdef);
		}
		String ifnotdef = eExtension.attributeValue("ifnotdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifnotdef)) {
			extension.setIfnotdef(ifnotdef);
		}
		String absent = eExtension.attributeValue("absent", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(absent)) {
			extension.setAbsentAction(absent);
		}
		extension.setElement(eExtension);
		try {
			registerExtension(extension);
		} catch (InstrumentRegistryException e) {
			throw new ConfigurationException(e);
		}
	}

	protected void configureInstrument(IElement element)
			throws ConfigurationException {
		String id = element.attributeValue("id", null); //$NON-NLS-1$
		if (id == null) {
			throw new ConfigurationException(
					Msg.getString("StandardInstrument.ExMustHaveID")); //$NON-NLS-1$
		}
		setId(id);
		String metaName = element.attributeValue("name", null); //$NON-NLS-1$
		if (metaName != null) {
			setMetaInfo(IMetaInfoSupport.META_NAME, metaName);
		}
		String bundleName = element.attributeValue(
				"bundle", getId() + ".messages"); //$NON-NLS-1$
		setMessageBundleName(bundleName);
		expressionEvaluator
				.put("bundlestr", new MessageBundleStringResolver(getMessageBundle())); //$NON-NLS-1$
		expressionEvaluator
				.put("bundlemsg", new MessageBundleMessageResolver(getMessageBundle())); //$NON-NLS-1$
		String metaVendor = element.attributeValue("vendor", null); //$NON-NLS-1$
		if (metaVendor != null) {
			setMetaInfo(IMetaInfoSupport.META_VENDOR, metaVendor);
		}
		String metaVersion = element.attributeValue("version", null); //$NON-NLS-1$
		if (metaVersion != null) {
			setMetaInfo(IMetaInfoSupport.META_VERSION, metaVersion);
		}
		String ifdef = element.attributeValue("ifdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifdef)) {
			setIfdef(ifdef);
		}
		String ifnotdef = element.attributeValue("ifnotdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifnotdef)) {
			setIfnotdef(ifnotdef);
		}
	}

	protected void configurePrerequisiste(IElement element) {
		StandardInstrumentPrerequisite prerequisite = new StandardInstrumentPrerequisite(
				this);
		boolean optional = Boolean.valueOf(
				element.attributeValue("optional", null)) //$NON-NLS-1$
				.booleanValue();
		if (optional) {
			prerequisite
					.setAbsentAction(StandardInstrumentRegistry.ACTION_IGNORE);
		} else {
			prerequisite
					.setAbsentAction(element.attributeValue("absent", null)); //$NON-NLS-1$
		}
		prerequisite
				.setInstrumentId(element.attributeValue("instrument", null)); //$NON-NLS-1$
		String ifdef = element.attributeValue("ifdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifdef)) {
			prerequisite.setIfdef(ifdef);
		}
		String ifnotdef = element.attributeValue("ifnotdef", null); //$NON-NLS-1$
		if (!StringTools.isEmpty(ifnotdef)) {
			prerequisite.setIfnotdef(ifnotdef);
		}
		addPrerequisite(prerequisite);
	}

	protected void configurePrerequisistes(IElement element) {
		Iterator<IElement> itExtension = element
				.elementIterator("prerequisite"); //$NON-NLS-1$
		while (itExtension.hasNext()) {
			IElement eExtension = itExtension.next();
			configurePrerequisiste(eExtension);
		}
	}

	protected ClassLoader createClassLoader() {
		ClassLoader parentLoader = getParentLoader();
		ClassLoader result = StandardInstrumentClassLoader.create(this,
				parentLoader);
		return result;
	}

	protected MapResolver createExpressionEvaluator() {
		MapResolver result = new MapResolver(true);
		result.put("instrument", new InstrumentResolver(getFacade())); //$NON-NLS-1$
		result.put("nlsstr", new NLSStringResolver(getClassLoader())); //$NON-NLS-1$
		result.put("nlsmsg", new NLSMessageResolver(getClassLoader())); //$NON-NLS-1$
		return result;
	}

	public IExtension createExtension(String extensionPointId, String id) {
		StandardExtension targetExtension = new StandardExtension(this,
				extensionPointId, id);
		IElement extensionElement = ElementFactory.get().createElement(
				"extension"); //$NON-NLS-1$
		targetExtension.setElement(extensionElement);
		extensionElement.setAttributeValue("point", //$NON-NLS-1$
				targetExtension.getExtensionPointId());
		extensionElement.setAttributeValue("id", targetExtension.getId()); //$NON-NLS-1$
		return targetExtension.getFacade();
	}

	protected void flush() throws IOException {
		if (getStore() != null) {
			getStore().flush(this);
		}
	}

	@Override
	final public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	public File getBaseDir() {
		return baseDir;
	}

	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = createClassLoader();
		}
		return classLoader;
	}

	public String getDescription() {
		return Msg
				.getString(
						"StandardInstrument.Description", getId(), getBaseDir().getName()); //$NON-NLS-1$
	}

	public IStringEvaluator getExpressionEvaluator() {
		synchronized (this) {
			if (expressionEvaluator == null) {
				expressionEvaluator = createExpressionEvaluator();
			}
			return expressionEvaluator;
		}
	}

	protected IExtension[] getExtensionFacades() {
		IExtension[] result = new IExtension[extensions.size()];
		int i = 0;
		for (StandardExtension current : extensions) {
			result[i] = current.getFacade();
			i++;
		}
		return result;
	}

	public List<StandardExtension> getExtensions() {
		return extensions;
	}

	public IInstrument getFacade() {
		return facade;
	}

	/**
	 * A unique name for the instrument.
	 * 
	 * <p>
	 * It is used for identifying uniquely a instrument within the
	 * {@link IInstrumentRegistry}. Clients may lookup instruments with well
	 * known names, for example the annotation handling instrument.
	 * </p>
	 * 
	 * @return A string identifying the instrument uniquely.
	 */
	public String getId() {
		return id;
	}

	public String getIfdef() {
		return ifdef;
	}

	public String getIfnotdef() {
		return ifnotdef;
	}

	public StandardInstrumentRegistry getInstrumentRegistry() {
		return registry;
	}

	public MessageBundle getMessageBundle() {
		if (messageBundle == null) {
			messageBundle = MessageBundleTools.getMessageBundle(
					getMessageBundleName(), getClassLoader());
		}
		return messageBundle;
	}

	public String getMessageBundleName() {
		return bundleName;
	}

	public String getMetaInfo(String name) {
		return metaInfo.get(name);
	}

	/**
	 * The loader to be used as the parent of the instrument loaders
	 * 
	 * @return The loader to be used as the parent of the instrument loaders
	 */
	protected ClassLoader getParentLoader() {
		return getRegistry().getClassLoader();
	}

	protected IInstrumentPrerequisite[] getPrerequisiteFacades() {
		if (cachedPrerequisites == null) {
			cachedPrerequisites = new IInstrumentPrerequisite[prerequisites
					.size()];
			int i = 0;
			for (StandardInstrumentPrerequisite prerequisite : prerequisites) {
				cachedPrerequisites[i] = prerequisite.getFacade();
				i++;
			}
		}
		return cachedPrerequisites;
	}

	public List<StandardInstrumentPrerequisite> getPrerequisites() {
		return prerequisites;
	}

	public File getProfileDir() {
		return FileEnvironment.get().getProfileDir();
	}

	protected StandardInstrumentRegistry getRegistry() {
		return registry;
	}

	protected State getState() {
		return state;
	}

	public IInstrumentStore getStore() {
		return store;
	}

	public File getTempDir() {
		return FileEnvironment.get().getTempDir();
	}

	public File getWorkingDir() {
		return FileEnvironment.get().getWorkingDir();
	}

	protected boolean isCheckVisibility(StandardExtension extension) {
		IInstrument provider = extension.getExtensionPoint().getProvider();
		return InstrumentTools.isPrerequisiteImplied(getFacade(), provider);
	}

	public void registerExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		extensions.add(extension);
		if (getState().isStarted()) {
			startExtension(extension);
		}
	}

	@Override
	final public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	public void removePrerequisite(IInstrument prerequisite) {
		// todo
	}

	@Override
	final public Object setAttribute(Object key, Object value) {
		return attributes.setAttribute(key, value);
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public void setId(String name) {
		this.id = name;
		setMetaInfo(IMetaInfoSupport.META_ID, name);
	}

	public void setIfdef(String ifdef) {
		this.ifdef = ifdef;
	}

	public void setIfnotdef(String ifnotdef) {
		this.ifnotdef = ifnotdef;
	}

	public void setMessageBundle(MessageBundle bundle) {
		this.messageBundle = bundle;
	}

	public void setMessageBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public void setMetaInfo(String name, String value) {
		metaInfo.put(name, value);
	}

	protected void setState(State state) {
		this.state = state;
	}

	/**
	 * Perform the startup sequence for the instrument.
	 * 
	 * @throws InstrumentException
	 */
	protected void start() {
		if (state.isStarted()) {
			return;
		}
		try {
			state = new StateStarted("started"); //$NON-NLS-1$
			startLocal();
		} catch (RuntimeException e) {
			state = new StateFailed(e.getLocalizedMessage());
			throw e;
		}
	}

	@SuppressWarnings("nls")
	protected void startExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		if (!registry.ppIsDefined(extension.getIfdef())
				|| !registry.ppIsNotDefined(extension.getIfnotdef())) {
			Log.log(Level.FINE,
					"exclude extension '" + StringTools.safeString(extension)); //$NON-NLS-1$
			return;
		}
		StandardExtensionPoint extensionPoint = getRegistry()
				.lookupExtensionPoint(extension.getExtensionPointId());
		if (extensionPoint == null) {
			// this is dangerous - watch out for undeclared extension points
			String action = extension.getAbsentAction();
			String reason = "missing extensionpoint '"
					+ extension.getExtensionPointId() + "'";
			if (action == StandardInstrument.ACTION_DEFAULT
					|| action.equals(StandardInstrument.ACTION_FAIL)) {
				Exception ex = new InstrumentRegistryException("fail: "
						+ reason + " in '" + extension.getProvider().getId()
						+ "' defined in "
						+ extension.getProvider().getBaseDir());
				extension.setState(new StateFailed(reason));
				extension.getProvider().setState(new StateFailed(reason));
				throw new IllegalArgumentException(ex);
			} else if (action.equals(StandardInstrument.ACTION_IGNORE)) {
				// go on...
				Log.log(Level.INFO, "ignore: " + reason + " in '"
						+ extension.getProvider().getId() + "' defined in "
						+ extension.getProvider().getBaseDir());
				extension.setState(new StateExcluded(reason));
				return;
			} else if (action.equals(StandardInstrument.ACTION_SKIP)) {
				// todo it's too late to skip, change
				Log.log(Level.INFO, "skip: " + reason + " in '"
						+ extension.getProvider().getId() + "' defined in "
						+ extension.getProvider().getBaseDir());
				extension.setState(new StateExcluded(reason));
				extension.getProvider().setState(new StateExcluded(reason));
				return;
			} else {
				Exception ex = new InstrumentRegistryException(
						"absent action '" + action + "' not recognized in '"
								+ extension.getProvider().getId()
								+ "' defined in "
								+ extension.getProvider().getBaseDir());
				extension.setState(new StateFailed(reason));
				extension.getProvider().setState(new StateFailed(reason));
				throw new IllegalArgumentException(ex);
			}
		}
		if (Log.isLoggable(Level.FINE)) {
			Log.log(Level.FINE, "register " + StringTools.safeString(extension));
		}
		extensionPoint.registerExtension(extension);
	}

	protected void startExtensions() {
		for (Iterator<StandardExtension> it = extensions.iterator(); it
				.hasNext();) {
			StandardExtension extension = it.next();
			try {
				startExtension(extension);
				if (CHECK_VISIBILITY && !isCheckVisibility(extension)) {
					throw new InstrumentException("invisible "
							+ StringTools.safeString(extension));
				}
			} catch (InstrumentRegistryException e) {
				throw new InstrumentException(StringTools.safeString(extension)
						+ " error when registering", e); //$NON-NLS-1$
			}
		}
	}

	protected boolean startLocal() throws InstrumentException {
		if (Log.isLoggable(Level.FINE)) {
			Log.fine("register extensions for " + StringTools.safeString(this)); //$NON-NLS-1$
		}
		startExtensions();
		if (Log.isLoggable(Level.FINE)) {
			Log.fine("loading ready for " + StringTools.safeString(this)); //$NON-NLS-1$
		}
		return true;
	}

	/**
	 * Perform the shutdown sequence for the instrument.
	 */
	protected void stop() {
		if (state.isStopped()) {
			return;
		}
		state = new StateStopped("stopped"); //$NON-NLS-1$
		stopLocal();
	}

	protected void stopExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		StandardExtensionPoint extensionPoint = getRegistry()
				.lookupExtensionPoint(extension.getExtensionPointId());
		if (extensionPoint == null) {
			return;
		}
		extensionPoint.unregisterExtension(extension);
		if (Log.isLoggable(Level.FINE)) {
			Log.log(Level.FINE,
					"deregistered " + StringTools.safeString(extension)); //$NON-NLS-1$
		}
	}

	protected void stopExtensions() {
		for (Iterator<StandardExtension> it = new ReverseListIterator<>(
				extensions); it.hasNext();) {
			StandardExtension extension = it.next();
			try {
				stopExtension(extension);
			} catch (InstrumentRegistryException e) {
				throw new InstrumentException(StringTools.safeString(extension)
						+ " error when unregistering", e); //$NON-NLS-1$
			}
		}
	}

	protected void stopLocal() {
		if (Log.isLoggable(Level.FINE)) {
			Log.fine("unregister extensions for " + StringTools.safeString(this)); //$NON-NLS-1$
		}
		stopExtensions();
	}

	protected boolean stopRequested(Set<?> visited) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("IInstrument '"); //$NON-NLS-1$
		sb.append(getId());
		sb.append("' defined in '"); //$NON-NLS-1$
		sb.append(getBaseDir());
		sb.append("'"); //$NON-NLS-1$
		return sb.toString();
	}

	protected void unregisterExtension(StandardExtension extension)
			throws InstrumentRegistryException {
		if (extensions.remove(extension)) {
			if (getState().isStarted()) {
				stopExtension(extension);
			}
		}
	}

}