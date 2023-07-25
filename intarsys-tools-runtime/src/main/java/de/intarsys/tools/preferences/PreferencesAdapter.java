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
package de.intarsys.tools.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.reflect.FieldAccessException;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.IBasicAccessSupport;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * Adapt {@link Preferences} to {@link IPreferences}.
 */
public class PreferencesAdapter
		implements IPreferences, INotificationSupport, PreferenceChangeListener, IBasicAccessSupport {

	private static final ILogger Log = PACKAGE.Log;

	public static final String NODE_METADATA = "__metadata";

	public static final String MODIFIER_NULL = "null";

	private final Map<String, PreferencesAdapter> children = new HashMap<>();

	/** The wrapped java properties */
	private final Preferences jPrefs;

	private final PreferencesAdapter parent;

	private EventDispatcher dispatcher;

	private final Object lock = new Object();

	private final PreferencesAdapter root;

	/**
	 * 
	 */
	public PreferencesAdapter(PreferencesAdapter parent, Preferences jPrefs) {
		super();
		this.parent = parent;
		this.jPrefs = jPrefs;
		if (parent != null) {
			this.root = parent.root;
		} else {
			this.root = this;
		}
	}

	@Override
	public String absolutePath() {
		return getJPrefs().absolutePath();
	}

	@Override
	public synchronized void addNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			dispatcher = new EventDispatcher(this);
			getJPrefs().addPreferenceChangeListener(this);
		}
		dispatcher.addNotificationListener(type, listener);
	}

	@Override
	public Object basicGetValue(String name) throws FieldException {
		try {
			if (nodeExists(name)) {
				return node(name);
			}
		} catch (BackingStoreException e) {
			throw new FieldAccessException(getClass(), name);
		}
		return get(name);
	}

	@Override
	public Object basicSetValue(String name, Object value) throws FieldException {
		String oldValue = get(name);
		if (value == null) {
			remove(name);
		} else {
			String stringValue;
			try {
				stringValue = ConverterRegistry.get().convert(value, String.class);
			} catch (ConversionException e) {
				stringValue = String.valueOf(value);
			}
			put(name, stringValue);
		}
		return oldValue;
	}

	@Override
	public IPreferences[] children() {
		List<IPreferences> result = new ArrayList<>();
		String[] names = childrenNames();
		for (int i = 0; i < names.length; i++) {
			result.add(node(names[i]));
		}
		return result.toArray(new IPreferences[result.size()]);
	}

	@Override
	public String[] childrenNames() {
		try {
			String[] basicNames = getJPrefs().childrenNames();
			List<String> result = new ArrayList<>();
			for (String name : basicNames) {
				if (!NODE_METADATA.equals(name)) {
					result.add(name);
				}
			}
			return result.toArray(new String[result.size()]);
		} catch (BackingStoreException e) {
			Log.warn(e.getMessage(), e);
			return new String[0];
		}
	}

	@Override
	public void clear() throws BackingStoreException {
		getJPrefs().clear();
	}

	protected PreferencesAdapter createPreferencesNode(String name) {
		return new PreferencesAdapter(this, getJPrefs().node(PreferencesTools.toKeyName(name)));
	}

	@Override
	public void flush() {
		try {
			getJPrefs().flush();
		} catch (BackingStoreException e) {
			Log.warn(e.getMessage(), e);
		}
	}

	@Override
	public String get(String name) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return null;
		}
		return getJPrefs().get(PreferencesTools.toKeyName(name), null);
	}

	@Override
	public String get(String name, String def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().get(PreferencesTools.toKeyName(name), def);
	}

	@Override
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	@Override
	public boolean getBoolean(String name, boolean def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getBoolean(PreferencesTools.toKeyName(name), def);
	}

	@Override
	public byte[] getByteArray(String name, byte[] def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getByteArray(PreferencesTools.toKeyName(name), def);
	}

	@Override
	public double getDouble(String name) {
		return getDouble(name, 0);
	}

	@Override
	public double getDouble(String name, double def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getDouble(PreferencesTools.toKeyName(name), def);
	}

	@Override
	public float getFloat(String name) {
		return getFloat(name, 0);
	}

	@Override
	public float getFloat(String name, float def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getFloat(PreferencesTools.toKeyName(name), def);
	}

	@Override
	public int getInt(String name) {
		return getInt(name, 0);
	}

	@Override
	public int getInt(String name, int def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getInt(PreferencesTools.toKeyName(name), def);
	}

	protected Preferences getJPrefs() {
		return jPrefs;
	}

	@Override
	public long getLong(String name) {
		return getLong(name, 0);
	}

	@Override
	public long getLong(String name, long def) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return def;
		}
		return getJPrefs().getLong(PreferencesTools.toKeyName(name), def);
	}

	protected Preferences getMetadata() {
		return getJPrefs().node(NODE_METADATA);
	}

	@Override
	public String getModifierString(String name) {
		return getMetadata().get(PreferencesTools.toKeyName(name), null);
	}

	@Override
	public boolean hasModifier(String key, String modifier) {
		String modifierString = getModifierString(key);
		if (modifierString == null) {
			return false;
		}
		return modifierString.indexOf(modifier) >= 0;
	}

	@Override
	public String[] keys() {
		try {
			return getJPrefs().keys();
		} catch (BackingStoreException e) {
			Log.warn(e.getMessage(), e);
			return new String[0];
		}
	}

	@Override
	public String name() {
		return getJPrefs().name();
	}

	@Override
	public synchronized IPreferences node(String pathName) {
		synchronized (lock) {
			if ("".equals(pathName)) {
				return this;
			}
			if (pathName.equals(IPreferences.SEPARATOR)) {
				return root;
			}
			if (!pathName.startsWith(IPreferences.SEPARATOR)) {
				return node(new StringTokenizer(pathName, IPreferences.SEPARATOR, true));
			}
		}
		return root.node(new StringTokenizer(pathName.substring(1), IPreferences.SEPARATOR, true));
	}

	protected IPreferences node(StringTokenizer path) {
		String token = path.nextToken();
		if (token.equals(IPreferences.SEPARATOR)) {
			// slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		}
		if (hasModifier(token, MODIFIER_NULL)) {
			return null;
		}
		synchronized (lock) {
			PreferencesAdapter child = children.computeIfAbsent(token, (key) -> createPreferencesNode(token));
			if (!path.hasMoreTokens()) {
				return child;
			}
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens()) {
				throw new IllegalArgumentException("Path ends with slash");
			}
			return child.node(path);
		}
	}

	@Override
	public boolean nodeExists(String path) throws BackingStoreException {
		return getJPrefs().nodeExists(path);
	}

	@Override
	public IPreferences parent() {
		return parent;
	}

	@Override
	public void preferenceChange(java.util.prefs.PreferenceChangeEvent evt) {
		triggerChange(evt);
	}

	@Override
	public Map<String, String> properties() {
		Map<String, String> properties = new HashMap<>();
		String[] keys = keys();
		for (int i = 0; i < keys.length; i++) {
			properties.put(keys[i], get(keys[i]));
		}
		return properties;
	}

	@Override
	public void put(String name, boolean value) {
		getJPrefs().putBoolean(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void put(String name, byte[] value) {
		if (value == null) {
			getJPrefs().remove(PreferencesTools.toKeyName(name));
		} else {
			getJPrefs().putByteArray(PreferencesTools.toKeyName(name), value);
		}
	}

	@Override
	public void put(String name, double value) {
		getJPrefs().putDouble(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void put(String name, float value) {
		getJPrefs().putFloat(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void put(String name, int value) {
		getJPrefs().putInt(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void put(String name, long value) {
		getJPrefs().putLong(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void put(String name, String value) {
		if (value == null) {
			getJPrefs().remove(PreferencesTools.toKeyName(name));
		} else {
			getJPrefs().put(PreferencesTools.toKeyName(name), value);
		}
	}

	@Override
	public void putBoolean(String name, boolean value) {
		getJPrefs().putBoolean(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void putByteArray(String name, byte[] value) {
		if (value == null) {
			getJPrefs().remove(PreferencesTools.toKeyName(name));
		} else {
			getJPrefs().putByteArray(PreferencesTools.toKeyName(name), value);
		}
	}

	@Override
	public void putDouble(String name, double value) {
		getJPrefs().putDouble(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void putFloat(String name, float value) {
		getJPrefs().putFloat(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void putInt(String name, int value) {
		getJPrefs().putInt(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void putLong(String name, long value) {
		getJPrefs().putLong(PreferencesTools.toKeyName(name), value);
	}

	@Override
	public void remove(String name) {
		getJPrefs().remove(PreferencesTools.toKeyName(name));
		removeModifiers(name);
	}

	public void removeModifiers(String name) {
		getMetadata().remove(PreferencesTools.toKeyName(name));
	}

	@Override
	public void removeNode() throws BackingStoreException {
		if (parent == null) {
			throw new UnsupportedOperationException("Can't remove the root!");
		}
		synchronized (parent.lock) {
			getJPrefs().removeNode();
			parent.children.remove(getJPrefs().name());
		}
	}

	@Override
	public synchronized void removeNotificationListener(EventType type, INotificationListener listener) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.removeNotificationListener(type, listener);
		if (dispatcher.isEmpty()) {
			getJPrefs().removePreferenceChangeListener(this);
		}
	}

	@Override
	public IPreferences restrict(String scopeName) {
		if (getJPrefs() instanceof IScopedPlatformPreferences) {
			return new PreferencesAdapter(null, ((IScopedPlatformPreferences) getJPrefs()).restrict(scopeName));
		} else {
			return this;
		}
	}

	@Override
	public void setModifierString(String key, String modifiers) {
		if (modifiers == null) {
			getMetadata().remove(key);
		} else {
			getMetadata().put(key, modifiers);
		}
	}

	@Override
	public void sync() throws BackingStoreException {
		getJPrefs().sync();
	}

	protected void triggerChange(java.util.prefs.PreferenceChangeEvent jEvent) {
		PreferencesChangeEvent event = new PreferencesChangeEvent(this);
		event.setKey(jEvent.getKey());
		event.setNewValue(jEvent.getNewValue());
		triggerEvent(event);
	}

	protected void triggerEvent(Event event) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.triggerEvent(event);
	}

}
