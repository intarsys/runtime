/*
 * Copyright (c) 2007, intarsys consulting GmbH
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

/**
 * Adapt {@link Preferences} to {@link IPreferences}.
 */
public class PreferencesAdapter implements IPreferences, INotificationSupport,
		PreferenceChangeListener, IBasicAccessSupport {

	private final Map<String, PreferencesAdapter> children = new HashMap<String, PreferencesAdapter>();

	/** The wrapped java properties */
	private final Preferences jPrefs;

	private final PreferencesAdapter parent;

	private EventDispatcher dispatcher;

	private final Object lock = new Object();

	private final PreferencesAdapter root;

	public static final String NODE_METADATA = "__metadata";

	public static final String MODIFIER_NULL = "null";

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

	public String absolutePath() {
		return jPrefs.absolutePath();
	}

	synchronized public void addNotificationListener(EventType type,
			INotificationListener listener) {
		if (dispatcher == null) {
			dispatcher = new EventDispatcher(this);
			jPrefs.addPreferenceChangeListener(this);
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
	public Object basicSetValue(String name, Object value)
			throws FieldException {
		String oldValue = get(name);
		if (value == null) {
			remove(name);
		} else {
			String stringValue;
			try {
				stringValue = ConverterRegistry.get().convert(value,
						String.class);
			} catch (ConversionException e) {
				stringValue = String.valueOf(value);
			}
			put(name, stringValue);
		}
		return oldValue;
	}

	public IPreferences[] children() {
		List<IPreferences> children = new ArrayList<IPreferences>();
		String[] names = childrenNames();
		for (int i = 0; i < names.length; i++) {
			children.add(node(names[i]));
		}
		return children.toArray(new IPreferences[children.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferenceStore#childrenNames()
	 */
	public String[] childrenNames() {
		try {
			String[] basicNames = jPrefs.childrenNames();
			List<String> result = new ArrayList<String>();
			for (String name : basicNames) {
				if (!NODE_METADATA.equals(name)) {
					result.add(name);
				}
			}
			return result.toArray(new String[result.size()]);
		} catch (BackingStoreException e) {
			return new String[0];
		}
	}

	public void clear() throws BackingStoreException {
		jPrefs.clear();
	}

	protected PreferencesAdapter createPreferencesNode(String pathName) {
		return new PreferencesAdapter(this, jPrefs.node(pathName));
	}

	public void flush() {
		try {
			jPrefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getString(java.lang.String)
	 */
	public String get(String name) {
		if (hasModifier(name, MODIFIER_NULL)) {
			return null;
		}
		return jPrefs.get(name, null);
	}

	public String get(String key, String def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.get(key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getBoolean(java.lang.String
	 * )
	 */
	public boolean getBoolean(String name) {
		return getBoolean(name, false);
	}

	public boolean getBoolean(String key, boolean def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getBoolean(key, def);
	}

	public byte[] getByteArray(String key, byte[] def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getByteArray(key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getDouble(java.lang.String)
	 */
	public double getDouble(String name) {
		return getDouble(name, 0);
	}

	public double getDouble(String key, double def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getDouble(key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getFloat(java.lang.String)
	 */
	public float getFloat(String name) {
		return getFloat(name, 0);
	}

	public float getFloat(String key, float def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getFloat(key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getInt(java.lang.String)
	 */
	public int getInt(String name) {
		return getInt(name, 0);
	}

	public int getInt(String key, int def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getInt(key, def);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.IPreferenceStore#getLong(java.lang.String)
	 */
	public long getLong(String name) {
		return getLong(name, 0);
	}

	public long getLong(String key, long def) {
		if (hasModifier(key, MODIFIER_NULL)) {
			return def;
		}
		return jPrefs.getLong(key, def);
	}

	protected Preferences getMetadata() {
		return jPrefs.node(NODE_METADATA);
	}

	@Override
	public String getModifierString(String key) {
		return getMetadata().get(key, null);
	}

	@Override
	public boolean hasModifier(String key, String modifier) {
		String modifierString = getModifierString(key);
		if (modifierString == null) {
			return false;
		}
		return modifierString.indexOf(modifier) >= 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferenceStore#keys()
	 */
	public String[] keys() {
		try {
			return jPrefs.keys();
		} catch (BackingStoreException e) {
			return new String[0];
		}
	}

	public String name() {
		return jPrefs.name();
	}

	synchronized public IPreferences node(String pathName) {
		synchronized (lock) {
			if (pathName.equals(""))
				return this;
			if (pathName.equals("/"))
				return root;
			if (pathName.charAt(0) != '/')
				return node(new StringTokenizer(pathName, "/", true));
		}
		return root.node(new StringTokenizer(pathName.substring(1), "/", true));
	}

	protected IPreferences node(StringTokenizer path) {
		String token = path.nextToken();
		if (token.equals("/")) // Check for consecutive slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		if (hasModifier(token, MODIFIER_NULL)) {
			return null;
		}
		synchronized (lock) {
			PreferencesAdapter child = children.get(token);
			if (child == null) {
				child = createPreferencesNode(token);
				children.put(token, child);
			}
			if (!path.hasMoreTokens())
				return child;
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens())
				throw new IllegalArgumentException("Path ends with slash");
			return child.node(path);
		}
	}

	public boolean nodeExists(String pathName) throws BackingStoreException {
		return jPrefs.nodeExists(pathName);
	}

	public IPreferences parent() {
		return parent;
	}

	public void preferenceChange(java.util.prefs.PreferenceChangeEvent evt) {
		triggerChange(evt);
	}

	public Map<String, String> properties() {
		Map<String, String> properties = new HashMap<String, String>();
		String[] keys = keys();
		for (int i = 0; i < keys.length; i++) {
			properties.put(keys[i], get(keys[i]));
		}
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * boolean)
	 */
	public void put(String name, boolean value) {
		jPrefs.putBoolean(name, value);
	}

	public void put(String name, byte[] value) {
		if (value == null) {
			jPrefs.remove(name);
		} else {
			jPrefs.putByteArray(name, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * double)
	 */
	public void put(String name, double value) {
		jPrefs.putDouble(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * float)
	 */
	public void put(String name, float value) {
		jPrefs.putFloat(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * int)
	 */
	public void put(String name, int value) {
		jPrefs.putInt(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * long)
	 */
	public void put(String name, long value) {
		jPrefs.putLong(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferenceStore#put(java.lang.String,
	 * java.lang.String)
	 */
	public void put(String name, String value) {
		if (value == null) {
			jPrefs.remove(name);
		} else {
			jPrefs.put(name, value);
		}
	}

	public void putBoolean(String key, boolean value) {
		jPrefs.putBoolean(key, value);
	}

	public void putByteArray(String name, byte[] value) {
		if (value == null) {
			jPrefs.remove(name);
		} else {
			jPrefs.putByteArray(name, value);
		}
	}

	public void putDouble(String key, double value) {
		jPrefs.putDouble(key, value);
	}

	public void putFloat(String key, float value) {
		jPrefs.putFloat(key, value);
	}

	public void putInt(String key, int value) {
		jPrefs.putInt(key, value);
	}

	public void putLong(String key, long value) {
		jPrefs.putLong(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferenceStore#remove(java.lang.String)
	 */
	public void remove(String name) {
		jPrefs.remove(name);
		removeModifiers(name);
	}

	public void removeModifiers(String key) {
		getMetadata().remove(key);
	}

	public void removeNode() throws BackingStoreException {
		if (parent == null) {
			throw new UnsupportedOperationException("Can't remove the root!");
		}
		synchronized (parent.lock) {
			jPrefs.removeNode();
			parent.children.remove(jPrefs.name());
		}
	}

	synchronized public void removeNotificationListener(EventType type,
			INotificationListener listener) {
		if (dispatcher == null) {
			return;
		}
		dispatcher.removeNotificationListener(type, listener);
		if (dispatcher.isEmpty()) {
			jPrefs.removePreferenceChangeListener(this);
		}
	}

	public IPreferences restrict(String scopeName) {
		if (jPrefs instanceof IScopedPlatformPreferences) {
			return new PreferencesAdapter(null,
					((IScopedPlatformPreferences) jPrefs).restrict(scopeName));
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

	public void sync() throws BackingStoreException {
		jPrefs.sync();
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
