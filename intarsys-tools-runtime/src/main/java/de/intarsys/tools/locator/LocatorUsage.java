/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.PreferencesTools;

/**
 * A helper class to provide context sensitive storage of least recently used
 * {@link ILocator} instances.
 *
 * <pre>
 * Structure is:
 *
 * - last locator
 * - last locator per action name
 * - last locator per action name per context name
 *
 * - recent locators per context
 *
 * </pre>
 */
@SingletonClass
public class LocatorUsage {

	public static final String DEFAULT_CONTEXT = "main";

	/**
	 * A generic attribute to associate an {@code de.intarsys.document.model.IDocument} with the context
	 * tag to be used with regard to recent files.
	 */
	public static final Attribute ATTR_LOCATORRECENTCONTEXT = new Attribute("recentContext");

	public static final int MAXRECENT = 4;

	private static final LocatorUsage INSTANCE = new LocatorUsage();

	public static final String ACTION_ANY = "any"; //$NON-NLS-1$

	public static final String CONTEXT_ANY = "any"; //$NON-NLS-1$

	private static final String PREF_LOCATORUSE = "locatorUse"; //$NON-NLS-1$

	private static final String PREF_LOCATORUSERECENT = "locatorUseRecent"; //$NON-NLS-1$

	private static final String ATTR_LOCATOR = "locator"; //$NON-NLS-1$

	public static final String ACTION_LOAD = "load"; //$NON-NLS-1$

	public static final String ACTION_SAVE = "save"; //$NON-NLS-1$

	public static LocatorUsage get() {
		return INSTANCE;
	}

	protected static ILocator toLocator(String value, ILocator defaultValue) {
		if (value != null && value.length() > 0) {
			try {
				return BasicLocatorFactory.get().createLocator(value);
			} catch (IOException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	private String sharedContext;

	private String lastLocation = System.getProperty("user.dir"); //$NON-NLS-1$

	private final Map lastLocationPerAction = new HashMap();

	private final Map lastLocationPerActionAndContext = new HashMap();

	private final Map<String, List<String>> recentLocationsPerContext = new HashMap();

	/**
	 * Add a new file to the list of recently used files.
	 *
	 * <p>
	 * The length of the list is bound, the oldest entry is discarded.
	 * </p>
	 *
	 * @param recentContext
	 * @param pLocator      The name of the new file.
	 */
	public void addRecentLocator(String recentContext, ILocator pLocator) {
		if (recentContext == null) {
			return;
		}
		if (pLocator == null) {
			return;
		}
		String tempLocation = pLocator.getPath();
		synchronized (recentLocationsPerContext) {
			List<String> recent = recentLocationsPerContext.computeIfAbsent(recentContext, (key) -> new ArrayList<>());
			recent.remove(tempLocation);
			recent.add(0, tempLocation);
			while (recent.size() > MAXRECENT) {
				recent.remove(MAXRECENT);
			}
		}
	}

	public void clearRecentLocations(String recentContext) {
		synchronized (recentLocationsPerContext) {
			recentLocationsPerContext.remove(recentContext);
		}
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public String getLastLocation(String action) {
		synchronized (lastLocationPerAction) {
			return (String) lastLocationPerAction.get(action);
		}
	}

	public String getLastLocation(String action, String context) {
		synchronized (lastLocationPerActionAndContext) {
			Map contextLocators = (Map) lastLocationPerActionAndContext.get(action);
			if (contextLocators == null) {
				return null;
			}
			return (String) contextLocators.get(context);
		}
	}

	/**
	 * The collection of files that were used recently.
	 *
	 * @return The collection of files that were used recently.
	 */
	public List getRecentLocations(String context) {
		synchronized (recentLocationsPerContext) {
			List recent = recentLocationsPerContext.get(context);
			if (recent == null) {
				return Collections.emptyList();
			}
			return new ArrayList(recent);
		}
	}

	public String getSharedContext() {
		return sharedContext;
	}

	public void preferencesRestore(IPreferences preferences) {
		try {
			IPreferences prefLocatorUse = preferences.node(PREF_LOCATORUSE);
			String[] actionNames = prefLocatorUse.childrenNames();
			for (int i = 0; i < actionNames.length; i++) {
				String actionName = actionNames[i];
				IPreferences prefActionLocator = prefLocatorUse.node(actionName);
				String[] contextNames = prefActionLocator.childrenNames();
				for (int j = 0; j < contextNames.length; j++) {
					String contextName = contextNames[j];
					IPreferences prefContextLocator = prefActionLocator.node(contextName);
					String locatorDef = prefContextLocator.get(ATTR_LOCATOR);
					setLastLocator(actionName, contextName, toLocator(locatorDef, null));
				}
				String locatorDef = prefActionLocator.get(ATTR_LOCATOR);
				setLastLocator(actionName, toLocator(locatorDef, null));
			}
			String locatorDef = prefLocatorUse.get(ATTR_LOCATOR);
			setLastLocator(toLocator(locatorDef, null));
			//
			IPreferences prefLocatorRecent = preferences.node(PREF_LOCATORUSERECENT);
			String[] contextNames = prefLocatorRecent.keys();
			for (int i = 0; i < contextNames.length; i++) {
				String contextName = contextNames[i];
				String locatorsDef = prefLocatorRecent.get(contextName);
				String[] locatorsDefArray = PreferencesTools.toStringArray(locatorsDef);
				for (int j = locatorsDefArray.length - 1; j >= 0; j--) {
					addRecentLocator(contextName, toLocator(locatorsDefArray[j], null));
				}
			}
		} catch (BackingStoreException e) {
			//
		}
	}

	public void preferencesStore(IPreferences preferences) {
		IPreferences prefLocatorUse = preferences.node(PREF_LOCATORUSE);
		prefLocatorUse.put(ATTR_LOCATOR, getLastLocation());
		Map tempActionLocations;
		synchronized (lastLocationPerAction) {
			tempActionLocations = new HashMap(lastLocationPerAction);
		}
		for (Iterator itActionLocators = tempActionLocations.entrySet().iterator(); itActionLocators.hasNext();) {
			Map.Entry entry = (Map.Entry) itActionLocators.next();
			String actionName = (String) entry.getKey();
			String tempLocations = (String) entry.getValue();
			IPreferences prefActionLocator = prefLocatorUse.node(actionName);
			prefActionLocator.put(ATTR_LOCATOR, tempLocations);
		}
		Map tempActionContextLocations;
		synchronized (lastLocationPerActionAndContext) {
			tempActionContextLocations = new HashMap(lastLocationPerActionAndContext);
		}
		for (Iterator itActionContextLocators = tempActionContextLocations.entrySet()
				.iterator(); itActionContextLocators.hasNext();) {
			Map.Entry acEntry = (Map.Entry) itActionContextLocators.next();
			String actionName = (String) acEntry.getKey();
			Map contextLocators = (Map) acEntry.getValue();
			IPreferences prefActionLocator = prefLocatorUse.node(actionName);
			for (Iterator itContextLocators = contextLocators.entrySet().iterator(); itContextLocators.hasNext();) {
				Map.Entry cEntry = (Map.Entry) itContextLocators.next();
				String contextName = (String) cEntry.getKey();
				String tempLocation = (String) cEntry.getValue();
				IPreferences prefActionContextLocator = prefActionLocator.node(contextName);
				prefActionContextLocator.put(ATTR_LOCATOR, tempLocation);
			}
		}
		IPreferences prefLocatorRecent = preferences.node(PREF_LOCATORUSERECENT);
		Map tempContextRecentLocations;
		synchronized (recentLocationsPerContext) {
			tempContextRecentLocations = new HashMap(recentLocationsPerContext);
		}
		for (Iterator itRecentLocators = tempContextRecentLocations.entrySet().iterator(); itRecentLocators
				.hasNext();) {
			Map.Entry entry = (Map.Entry) itRecentLocators.next();
			String contextName = (String) entry.getKey();
			List tempLocations = (List) entry.getValue();
			prefLocatorRecent.put(contextName, PreferencesTools.toString(tempLocations));
		}
	}

	public void setLastLocator(ILocator pLocator) {
		if (pLocator == null) {
			return;
		}
		lastLocation = pLocator.getPath();
	}

	public void setLastLocator(String action, ILocator pLocator) {
		if (pLocator == null) {
			return;
		}
		synchronized (lastLocationPerAction) {
			lastLocationPerAction.put(action, pLocator.getPath());
		}
		setLastLocator(pLocator);
	}

	public void setLastLocator(String action, String context, ILocator pLocator) {
		if (pLocator == null || action == null || context == null) {
			return;
		}
		synchronized (lastLocationPerActionAndContext) {
			Map contextLocators = (Map) lastLocationPerActionAndContext.computeIfAbsent(action, (key) -> new HashMap());
			contextLocators.put(context, pLocator.getPath());
		}
		setLastLocator(action, pLocator);
	}

	public void setSharedContext(String context) {
		this.sharedContext = context;
	}

}
