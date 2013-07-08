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

import java.util.Collections;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import de.intarsys.tools.string.StringTools;

/**
 * A "null" {@link IPreferences} implementation.
 */
public class NullPreferences implements IPreferences {
	public static final NullPreferences ACTIVE = new NullPreferences();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#absolutePath()
	 */
	public String absolutePath() {
		return StringTools.EMPTY;
	}

	public IPreferences[] children() {
		return new IPreferences[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#childrenNames()
	 */
	public String[] childrenNames() throws BackingStoreException {
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#clear()
	 */
	public void clear() throws BackingStoreException {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#flush()
	 */
	public void flush() throws BackingStoreException {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#get(java.lang.String)
	 */
	public String get(String name) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#get(java.lang.String,
	 * java.lang.String)
	 */
	public String get(String key, String def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String name) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getBoolean(java.lang.String,
	 * boolean)
	 */
	public boolean getBoolean(String key, boolean def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getByteArray(java.lang.String,
	 * byte[])
	 */
	public byte[] getByteArray(String key, byte[] def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getDouble(java.lang.String)
	 */
	public double getDouble(String name) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getDouble(java.lang.String,
	 * double)
	 */
	public double getDouble(String key, double def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getFloat(java.lang.String)
	 */
	public float getFloat(String name) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#getFloat(java.lang.String,
	 * float)
	 */
	public float getFloat(String key, float def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#getInt(java.lang.String)
	 */
	public int getInt(String name) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#getInt(java.lang.String,
	 * int)
	 */
	public int getInt(String key, int def) {
		return def;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#getLong(java.lang.String)
	 */
	public long getLong(String name) {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#getLong(java.lang.String,
	 * long)
	 */
	public long getLong(String key, long def) {
		return def;
	}

	@Override
	public String getModifierString(String key) {
		return StringTools.EMPTY;
	}

	@Override
	public boolean hasModifier(String key, String modifier) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#keys()
	 */
	public String[] keys() {
		return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#name()
	 */
	public String name() {
		return StringTools.EMPTY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#node(java.lang.String)
	 */
	public IPreferences node(String pathName) {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#nodeExists(java.lang.String)
	 */
	public boolean nodeExists(String pathName) throws BackingStoreException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#parent()
	 */
	public IPreferences parent() {
		return null;
	}

	public Map<String, String> properties() {
		return Collections.EMPTY_MAP;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * boolean)
	 */
	public void put(String name, boolean value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * byte[])
	 */
	public void put(String key, byte[] value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * double)
	 */
	public void put(String name, double value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * float)
	 */
	public void put(String name, float value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * int)
	 */
	public void put(String name, int value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * long)
	 */
	public void put(String name, long value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#put(java.lang.String,
	 * java.lang.String)
	 */
	public void put(String name, String value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#putBoolean(java.lang.String,
	 * boolean)
	 */
	public void putBoolean(String key, boolean value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#putByteArray(java.lang.String,
	 * byte[])
	 */
	public void putByteArray(String key, byte[] value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#putDouble(java.lang.String,
	 * double)
	 */
	public void putDouble(String key, double value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#putFloat(java.lang.String,
	 * float)
	 */
	public void putFloat(String key, float value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#putInt(java.lang.String,
	 * int)
	 */
	public void putInt(String key, int value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#putLong(java.lang.String,
	 * long)
	 */
	public void putLong(String key, long value) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#remove(java.lang.String)
	 */
	public void remove(String key) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#removeNode()
	 */
	public void removeNode() throws BackingStoreException {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.preferences.IPreferences#restrict(java.lang.String)
	 */
	public IPreferences restrict(String scopeName) {
		return this;
	}

	@Override
	public void setModifierString(String key, String modifiers) {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.preferences.IPreferences#sync()
	 */
	public void sync() throws BackingStoreException {
		// do nothing
	}
}
