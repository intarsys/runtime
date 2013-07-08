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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IExtensionPoint;
import de.intarsys.claptz.IExtensionPointHandler;
import de.intarsys.claptz.IInstrument;
import de.intarsys.claptz.IInstrumentRegistry;
import de.intarsys.claptz.InstrumentRegistry;
import de.intarsys.claptz.InstrumentRegistryException;
import de.intarsys.claptz.InstrumentTools;
import de.intarsys.claptz.PACKAGE;
import de.intarsys.claptz.StateExcluded;
import de.intarsys.claptz.StateFailed;
import de.intarsys.claptz.StatePrepared;
import de.intarsys.claptz.io.IInstrumentStore;
import de.intarsys.tools.collection.ReverseListIterator;
import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.reporter.IReporter;
import de.intarsys.tools.reporter.Reporter;
import de.intarsys.tools.string.StringTools;

/**
 * A registry for instruments.
 * 
 * <p>
 * The instrument registry loads and controls the instruments for Stage. First
 * all instruments are loaded in the registry. When all instruments are
 * available, they are started. In this process all instrument object are
 * started, thereby starting their prerequisites.
 * </p>
 * 
 * <br>
 * SECURITY CONSTRAINT this class may not be leaked to non platform code
 */
public class StandardInstrumentRegistry implements IStartStop {

	/**
	 * The public portion of the InstrumentRegistry, which itself is never
	 * leaked to client code.
	 * 
	 */
	public class Facade implements IInstrumentRegistry {

		@Override
		public IInstrument createInstrument(String id, IInstrumentStore store) {
			return StandardInstrumentRegistry.this.createInstrument(id, store);
		}

		@Override
		public IExtensionPoint[] getExtensionPoints() {
			return StandardInstrumentRegistry.this.getExtensionPointFacades();
		}

		@Override
		public IInstrument[] getInstruments() {
			return StandardInstrumentRegistry.this.getInstruments();
		}

		protected StandardInstrumentRegistry getOwner() {
			return StandardInstrumentRegistry.this;
		}

		@Override
		public void load(IInstrumentStore store)
				throws InstrumentRegistryException {
			StandardInstrumentRegistry.this.load(store);
		}

		@Override
		public IExtensionPoint lookupExtensionPoint(String name) {
			StandardExtensionPoint temp = StandardInstrumentRegistry.this
					.lookupExtensionPoint(name);
			return temp == null ? null : temp.getFacade();
		}

		@Override
		public IInstrument lookupInstrument(String name) {
			StandardInstrument instrument = StandardInstrumentRegistry.this
					.lookupInstrument(name);
			return instrument == null ? null : instrument.getFacade();
		}

		@Override
		public void registerInstrument(IInstrument instrument)
				throws InstrumentRegistryException {
			StandardInstrumentRegistry.this
					.registerInstrument(((StandardInstrument.Facade) instrument)
							.getOwner());
		}

		@Override
		public void unregisterInstrument(IInstrument instrument)
				throws InstrumentRegistryException {
			StandardInstrumentRegistry.this
					.unregisterInstrument(((StandardInstrument.Facade) instrument)
							.getOwner());
		}
	}

	public static final String XE_EXTENSIONPOINT = "extensionpoint";

	public static final String XP_EXTENSIONPOINTS = "de.intarsys.claptz.extensionpoints";

	private Map<String, StandardExtensionPoint> extensionPoints = new LinkedHashMap<>();

	private IExtensionPointHandler installer = new ExtensionPointHandlerAdapter() {

		@Override
		protected Object basicInstallInsert(IExtension extension,
				IElement element) {
			if (XE_EXTENSIONPOINT.equals(element.getName())) {
				StandardExtensionPoint newEP = createExtensionPoint(
						extension.getProvider(), element);
				try {
					if (element.attributeValue("installer", null) != null) {
						IExtensionPointHandler installer = ElementTools.createObject(element, "installer", IExtensionPointHandler.class,
						extension.getProvider());
						newEP.setInstaller(installer);
					}
					registerExtensionPoint(newEP);
				} catch (Exception e) {
					String msg = "error installing extension";
					log(Log, Level.SEVERE, extension, element, msg, e);
				}
			} else {
				return super.basicInstallInsert(extension, element);
			}
			return null;
		}

		@Override
		protected void basicUninstall(IExtension extension, IElement element) {
			if (XE_EXTENSIONPOINT.equals(element.getName())) {
				String id = element.attributeValue("id", null);
				StandardExtensionPoint newEP = lookupExtensionPoint(id);
				if (newEP != null) {
					try {
						unregisterExtensionPoint(newEP);
					} catch (InstrumentRegistryException e) {
						String msg = StringTools.safeString(extension)
								+ " error unregistering extension";
						Log.log(Level.SEVERE, msg, e);
					}
				}
			} else {
				super.basicUninstall(extension, element);
			}
		}
	};

	private StandardExtensionPoint root;

	private static final Logger Log = PACKAGE.Log;

	/**
	 * NLS
	 */
	static private final MessageBundle Msg = PACKAGE.Messages;

	private ClassLoader classLoader;

	private Set<StandardInstrument> failedInstruments = new HashSet<>();

	/**
	 * The collection of registered IInstrumentContext objects, keyed by their
	 * id's.
	 * 
	 * <p>
	 * An instrument is started when registered with the registry and stopped
	 * upon removal.
	 * </p>
	 */
	private final Map<String, StandardInstrument> instruments = new HashMap<>();

	/**
	 * SECURITY CONSTRAINT A facade implementation to deny access to the real
	 * implementation.
	 */
	private final IInstrumentRegistry facade = new Facade();

	private boolean started = false;

	private Set<StandardInstrument> startedInstruments = new HashSet<>();

	private List<StandardInstrument> startList;

	private StandardInstrument rootInstrument;

	final private List<IInstrumentStore> stores = new ArrayList<>();

	final private Set<String> defines = new HashSet<String>();

	public static final String ACTION_DEFAULT = null;

	public static final String ACTION_FAIL = "fail"; //$NON-NLS-1$

	public static final String ACTION_IGNORE = "ignore"; //$NON-NLS-1$

	public static final String ACTION_SKIP = "skip"; //$NON-NLS-1$

	public StandardInstrumentRegistry() {
		super();
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = getClass().getClassLoader();
		}
		// the registry bootstraps itself..
		rootInstrument = new StandardInstrument(this, null);
		root = new StandardExtensionPoint(rootInstrument.getFacade(),
				XP_EXTENSIONPOINTS);
		root.setInstaller(installer);
		try {
			registerExtensionPoint(root);
		} catch (InstrumentRegistryException e) {
			throw new IllegalStateException("can't register "
					+ XP_EXTENSIONPOINTS, e);
		}
		InstrumentRegistry.set(getFacade());
	}

	public void addInstrumentStore(IInstrumentStore store) {
		stores.add(store);
	}

	protected void buildStartList(List<StandardInstrument> startList,
			StandardInstrument instrument) {
		if (!instrument.getState().isNew()) {
			return;
		}
		if (!ppIsDefined(instrument.getIfdef())) {
			String reason = "ifdef " + instrument.getIfdef() + " not defined";
			Log.log(Level.INFO,
					reason + " exclude instrument '" + instrument.getId()
							+ "' defined in " + instrument.getBaseDir());
			instrument.setState(new StateExcluded(reason));
			return;
		}
		if (!ppIsNotDefined(instrument.getIfnotdef())) {
			String reason = "ifnotdef " + instrument.getIfnotdef() + " defined";
			Log.log(Level.INFO,
					reason + " exclude instrument '" + instrument.getId()
							+ "' defined in " + instrument.getBaseDir());
			instrument.setState(new StateExcluded(reason));
			return;
		}
		List<StandardInstrumentPrerequisite> prerequisites = instrument
				.getPrerequisites();
		for (StandardInstrumentPrerequisite pre : prerequisites) {
			buildStartListPrerequisite(startList, instrument, pre);
		}
		if (!instrument.getState().isFailed()
				&& !instrument.getState().isExcluded()) {
			instrument.setState(new StatePrepared(""));
			startList.add(instrument);
		}
	}

	protected void buildStartListPrerequisite(List startList,
			StandardInstrument instrument,
			StandardInstrumentPrerequisite prerequisite) {
		if (!ppIsDefined(prerequisite.getIfdef())) {
			String reason = "ifdef " + prerequisite.getIfdef() + " not defined";
			Log.log(Level.INFO,
					reason + ", exclude prerequisite '"
							+ prerequisite.getInstrumentId() + "' for '"
							+ instrument.getId() + "' defined in "
							+ instrument.getBaseDir());
			prerequisite.setState(new StateExcluded(reason));
			return;
		}
		if (!ppIsNotDefined(prerequisite.getIfnotdef())) {
			String reason = "ifnotdef " + prerequisite.getIfnotdef()
					+ " defined";
			Log.log(Level.INFO,
					reason + ", exclude prerequisite '"
							+ prerequisite.getInstrumentId() + "' for '"
							+ instrument.getId() + "' defined in "
							+ instrument.getBaseDir());
			prerequisite.setState(new StateExcluded(reason));
			return;
		}
		StandardInstrument preInstrument = prerequisite.getInstrument();
		if (preInstrument != null) {
			buildStartList(startList, preInstrument);
		}
		if (preInstrument == null || preInstrument.getState().isFailed()
				|| preInstrument.getState().isExcluded()) {
			String action = prerequisite.getAbsentAction();
			String reason = "missing prerequisite '"
					+ prerequisite.getInstrumentId() + "'";
			if (action == StandardInstrumentRegistry.ACTION_DEFAULT
					|| action.equals(StandardInstrumentRegistry.ACTION_FAIL)) {
				Exception ex = new InstrumentRegistryException("fail: "
						+ reason + " in '" + instrument.getId()
						+ "' defined in " + instrument.getBaseDir());
				prerequisite.setState(new StateFailed(reason));
				instrument.setState(new StateFailed(reason));
				throw new IllegalArgumentException(ex);
			} else if (action.equals(StandardInstrumentRegistry.ACTION_IGNORE)) {
				// go on...
				Log.log(Level.INFO,
						"ignore: " + reason + " in '" + instrument.getId()
								+ "' defined in " + instrument.getBaseDir());
				prerequisite.setState(new StateExcluded(reason));
			} else if (action.equals(StandardInstrumentRegistry.ACTION_SKIP)) {
				// don't add to startable instruments and return
				Log.log(Level.INFO,
						"skip: " + reason + " in '" + instrument.getId()
								+ "' defined in " + instrument.getBaseDir());
				prerequisite.setState(new StateExcluded(reason));
				instrument.setState(new StateExcluded(reason));
			} else {
				Exception ex = new InstrumentRegistryException(
						"absent action '" + action + "' not recognized in '"
								+ instrument.getId() + "' defined in "
								+ instrument.getBaseDir());
				prerequisite.setState(new StateFailed(reason));
				instrument.setState(new StateFailed(reason));
				throw new IllegalArgumentException(ex);
			}
		}
	}

	protected StandardExtensionPoint createExtensionPoint(IInstrument provider,
			IElement childElement) {
		String id = childElement.attributeValue("id", null);
		return new StandardExtensionPoint(provider, id);
	}

	protected StandardExtensionPoint createExtensionPoint(String id) {
		return new StandardExtensionPoint(null, id);
	}

	public IInstrument createInstrument(String id, IInstrumentStore store) {
		StandardInstrument instrument = new StandardInstrument(this, store);
		instrument.setId(id);
		return instrument.getFacade();
	}

	public void flush() throws IOException {
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	protected IExtensionPoint[] getExtensionPointFacades() {
		Collection<StandardExtensionPoint> tempEps = getExtensionPoints();
		IExtensionPoint[] result = new IExtensionPoint[tempEps.size()];
		int i = 0;
		for (StandardExtensionPoint tempEp : tempEps) {
			result[i++] = tempEp.getFacade();
		}
		return result;
	}

	protected Collection<StandardExtensionPoint> getExtensionPoints() {
		return extensionPoints.values();
	}

	public IInstrumentRegistry getFacade() {
		return facade;
	}

	/**
	 * A collection of all instruments registered.
	 * 
	 * <p>
	 * You can not modify the result of this request.
	 * </p>
	 * 
	 * @return A collection of all instruments registered.
	 */
	protected IInstrument[] getInstruments() {
		IInstrument[] result = new IInstrument[instruments.size()];
		int i = 0;
		for (StandardInstrument current : instruments.values()) {
			result[i] = current.getFacade();
			i++;
		}
		return result;
	}

	public List<IInstrumentStore> getStores() {
		return stores;
	}

	protected boolean isFailed(StandardInstrument instrument) {
		return failedInstruments.contains(instrument);
	}

	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	protected boolean isStarted(StandardInstrument instrument) {
		return startedInstruments.contains(instrument);
	}

	public void load(IInstrumentStore store) throws InstrumentRegistryException {
		store.load(this);
	}

	public StandardExtensionPoint lookupExtensionPoint(String name) {
		return extensionPoints.get(name);
	}

	public StandardInstrument lookupInstrument(String name) {
		return instruments.get(name);
	}

	public void ppDefine(String symbolList) {
		if (symbolList == null) {
			return;
		}
		String[] symbols = symbolList.split(";");
		for (String symbol : symbols) {
			String trimmed = symbol.trim();
			if (trimmed.length() > 0) {
				defines.add(trimmed);
			}
		}
	}

	public Set<String> ppGetDefines() {
		return new HashSet<String>(defines);
	}

	/**
	 * <code>true</code> if ALL of the defines in expr are set.
	 * <p>
	 * Be especially aware that this is NOT equal to !ppIsNotDefined(expr)
	 * 
	 * @param symbolList
	 * @return
	 */
	public boolean ppIsDefined(String symbolList) {
		if (symbolList == null) {
			return true;
		}
		String[] symbols = symbolList.split(";");
		for (String symbol : symbols) {
			String trimmed = symbol.trim();
			if (trimmed.length() > 0 && !defines.contains(trimmed)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <code>true</code> if NONE of the defines in expr are set.
	 * <p>
	 * Be especially aware that this is NOT equal to !ppIsDefined(expr)
	 * 
	 * @param symbolList
	 * @return
	 */
	public boolean ppIsNotDefined(String symbolList) {
		if (symbolList == null) {
			return true;
		}
		String[] symbols = symbolList.split(";");
		for (String symbol : symbols) {
			String trimmed = symbol.trim();
			if (trimmed.length() > 0 && defines.contains(trimmed)) {
				return false;
			}
		}
		return true;
	}

	public void ppUndefine(String symbolList) {
		if (symbolList == null) {
			return;
		}
		String[] symbols = symbolList.split(";");
		for (String symbol : symbols) {
			String trimmed = symbol.trim();
			defines.remove(trimmed);
		}
	}

	protected void registerExtensionPoint(StandardExtensionPoint extensionPoint)
			throws InstrumentRegistryException {
		StandardExtensionPoint prev = extensionPoints.get(extensionPoint
				.getId());
		if (prev != null) {
			// we can not overwrite here as extension points are
			// provided lazy - look out for warnings in log file!!
			return;
		}
		if (Log.isLoggable(Level.FINE)) {
			Log.log(Level.FINE,
					"register " + StringTools.safeString(extensionPoint));
		}
		extensionPoints.put(extensionPoint.getId(), extensionPoint);
		if (extensionPoint.getInstaller() != null) {
			extensionPoint.getInstaller().setExtensionPoint(
					extensionPoint.getFacade());
			if (!extensionPoint.getInstaller().isActive()) {
				extensionPoint.getInstaller().install();
			}
		}
	}

	/**
	 * Register a new instrument implementation along with its instrument in the
	 * registry.
	 * 
	 * <p>
	 * The registry will <code>start</code> the instrument before registration.
	 * </p>
	 * 
	 * @param instrument
	 * 
	 * @throws InstrumentRegistryException
	 */
	public void registerInstrument(StandardInstrument instrument)
			throws InstrumentRegistryException {
		String id = instrument.getId();
		if (instruments.get(id) != null) {
			throw new InstrumentRegistryException(
					Msg.getString(
							"StandardInstrumentRegistry.ExAlreadyRegistered", instrument.getId()) //$NON-NLS-1$
			);
		}
		ppDefine(id);
		instruments.put(id, instrument);
		if (isStarted()) {
			instrument.start();
		}
		// instrument.addPrerequisite(rootInstrument.getFacade(), null);
	}

	@Override
	public void start() {
		started = true;
		startList = new ArrayList<>(100);
		List<StandardInstrument> tempInstruments = new ArrayList<>(
				instruments.values());
		for (Iterator<StandardInstrument> it = tempInstruments.iterator(); it
				.hasNext();) {
			StandardInstrument instrument = it.next();
			buildStartList(startList, instrument);
		}
		for (Iterator<StandardInstrument> it = startList.iterator(); it
				.hasNext();) {
			StandardInstrument instrument = it.next();
			start(instrument);
		}
	}

	protected void start(StandardInstrument instrument) {
		if (isFailed(instrument) || isStarted(instrument)) {
			return;
		}
		try {
			instrument.start();
			startedInstruments.add(instrument);
		} catch (RuntimeException e) {
			failedInstruments.add(instrument);
			Reporter.get().reportError(
					Msg.getString("StandardInstrumentRegistry.TitleError"), //$NON-NLS-1$
					e.getMessage(), e, IReporter.STYLE_NONE);
		}
	}

	@Override
	public void stop() {
		if (!started) {
			return;
		}
		for (Iterator<StandardInstrument> it = new ReverseListIterator<>(
				startList); it.hasNext();) {
			StandardInstrument instrument = it.next();
			stop(instrument);
		}
		startList.clear();
		instruments.clear();
		started = false;
	}

	protected void stop(StandardInstrument instrument) {
		if (isFailed(instrument) || !isStarted(instrument)) {
			return;
		}
		try {
			instrument.stop();
			startedInstruments.remove(instrument);
		} catch (RuntimeException e) {
			Reporter.get().reportError(
					Msg.getString("StandardInstrumentRegistry.TitleError"), //$NON-NLS-1$
					e.getMessage(), e, IReporter.STYLE_NONE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.IStartStop#stopRequested()
	 */
	@Override
	public boolean stopRequested(Set visited) {
		return true;
	}

	protected void unregisterExtensionPoint(
			StandardExtensionPoint extensionPoint)
			throws InstrumentRegistryException {
		if (extensionPoint.getInstaller() != null) {
			extensionPoint.getInstaller().uninstall();
		}
		extensionPoints.remove(extensionPoint.getId());
		if (Log.isLoggable(Level.FINE)) {
			Log.log(Level.FINE,
					"deregistered " + StringTools.safeString(extensionPoint));
		}
	}

	protected void unregisterInstrument(StandardInstrument instrument) {
		if (instrument != null) {
			ppUndefine(instrument.getId());
			instruments.remove(instrument.getId());
		}
	}
}
