/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import javax.annotation.PostConstruct;

import de.intarsys.tools.bean.IBeanInstallationInstruction;
import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.preferences.IPreferences;
import de.intarsys.tools.preferences.IPreferencesSupport;
import de.intarsys.tools.preferences.NullPreferences;
import de.intarsys.tools.preferences.PreferencesFactory;
import de.intarsys.tools.string.StringTools;

/**
 * A preferences aware namespace. Before accessing namespace content, current
 * values are imported from the associated preferences if declared. Setting
 * variables result in storing the new values in the respective preferences.
 * 
 */
public class VariableNamespace implements IVariableNamespace, IPreferencesSupport, IElementConfigurable,
		IBeanInstallationInstruction {

	// do not change new String, as we need a unique marker object (not
	// interned)
	private static final String NA = new String(); // NOSONAR

	private Map<String, String> namespace = new HashMap<>();

	private IPreferences preferences;

	private String preferencesName;

	private String preferencesScope;

	private String id;

	public String basicGetVariable(String key) {
		return namespace.get(key);
	}

	public Map basicGetVariables() {
		Map result = new HashMap<String, String>(namespace);
		return result;
	}

	public void basicPutVariable(String key, String value) {
		namespace.put(key, value);
	}

	@Override
	public void configure(IElement element) throws ConfigurationException {
		setId(element.attributeValue("id", getId()));
		setPreferencesName(element.attributeValue("preferences", getPreferencesName()));
		setPreferencesScope(element.attributeValue("preferencesscope", getPreferencesScope()));
	}

	protected IPreferences createPreferences() {
		if (preferencesName == null) {
			// todo should use transient preferences
			return NullPreferences.ACTIVE;
		} else {
			IPreferences tempPrefs = PreferencesFactory.get().getRoot().node(preferencesName);
			if (preferencesScope != null) {
				tempPrefs = tempPrefs.restrict(preferencesScope);
			}
			return tempPrefs;
		}
	}

	public String getId() {
		return id;
	}

	@Override
	public IPreferences getPreferences() {
		if (preferences == null) {
			preferences = createPreferences();
		}
		return preferences;
	}

	public String getPreferencesName() {
		return preferencesName;
	}

	public String getPreferencesScope() {
		return preferencesScope;
	}

	@Override
	@SuppressWarnings("java:S4973")
	public String getVariable(String key) {
		String result = getPreferences().get(key, NA);
		if (result == NA) {
			result = namespace.get(key);
		}
		return result;
	}

	@Override
	@SuppressWarnings("java:S4973")
	public String getVariable(String key, String defaultValue) {
		String result = getPreferences().get(key, NA);
		if (result == NA) {
			result = namespace.get(key);
			if (result == null) {
				result = defaultValue;
			}
		}
		return result;
	}

	@Override
	public Iterator getVariableIterator() {
		return getVariables().entrySet().iterator();
	}

	@Override
	public Map getVariables() {
		Map result = new HashMap<String, String>(namespace);
		result.putAll(getPreferences().properties());
		return result;
	}

	@Override
	public void putVariable(String key, String value) {
		getPreferences().put(key, value);
	}

	@PostConstruct
	public void register() {
		if (StringTools.isEmpty(getId())) {
			return;
		}
		IVariableNamespaces namespaces = VariableNamespaces.get();
		namespaces.setNamespace(getId(), this);
	}

	public void reset() {
		try {
			getPreferences().clear();
		} catch (BackingStoreException e) {
			// ignore
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPreferencesName(String preferencesName) {
		this.preferencesName = preferencesName;
	}

	public void setPreferencesScope(String preferencesScope) {
		this.preferencesScope = preferencesScope;
	}
}
