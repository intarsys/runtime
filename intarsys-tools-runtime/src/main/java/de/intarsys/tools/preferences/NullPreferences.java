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

import java.util.Collections;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.string.StringTools;

/**
 * A "null" {@link IPreferences} implementation.
 */
@SingletonClass
public class NullPreferences implements IPreferences {

	public static final NullPreferences ACTIVE = new NullPreferences();

	protected NullPreferences() {
	}

	@Override
	public String absolutePath() {
		return StringTools.EMPTY;
	}

	@Override
	public IPreferences[] children() {
		return new IPreferences[0];
	}

	@Override
	public String[] childrenNames() throws BackingStoreException {
		return new String[0];
	}

	@Override
	public void clear() throws BackingStoreException {
		// do nothing
	}

	@Override
	public void flush() throws BackingStoreException {
		// do nothing
	}

	@Override
	public String get(String name) {
		return null;
	}

	@Override
	public String get(String key, String def) {
		return def;
	}

	@Override
	public boolean getBoolean(String name) {
		return false;
	}

	@Override
	public boolean getBoolean(String key, boolean def) {
		return def;
	}

	@Override
	public byte[] getByteArray(String key, byte[] def) {
		return def;
	}

	@Override
	public double getDouble(String name) {
		return 0;
	}

	@Override
	public double getDouble(String key, double def) {
		return def;
	}

	@Override
	public float getFloat(String name) {
		return 0;
	}

	@Override
	public float getFloat(String key, float def) {
		return def;
	}

	@Override
	public int getInt(String name) {
		return 0;
	}

	@Override
	public int getInt(String key, int def) {
		return def;
	}

	@Override
	public long getLong(String name) {
		return 0;
	}

	@Override
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

	@Override
	public String[] keys() {
		return new String[0];
	}

	@Override
	public String name() {
		return StringTools.EMPTY;
	}

	@Override
	public IPreferences node(String pathName) {
		return this;
	}

	@Override
	public boolean nodeExists(String pathName) throws BackingStoreException {
		return false;
	}

	@Override
	public IPreferences parent() {
		return null;
	}

	@Override
	public Map<String, String> properties() {
		return Collections.emptyMap();
	}

	@Override
	public void put(String name, boolean value) {
		// do nothing
	}

	@Override
	public void put(String key, byte[] value) {
		// do nothing
	}

	@Override
	public void put(String name, double value) {
		// do nothing
	}

	@Override
	public void put(String name, float value) {
		// do nothing
	}

	@Override
	public void put(String name, int value) {
		// do nothing
	}

	@Override
	public void put(String name, long value) {
		// do nothing
	}

	@Override
	public void put(String name, String value) {
		// do nothing
	}

	@Override
	public void putBoolean(String key, boolean value) {
		// do nothing
	}

	@Override
	public void putByteArray(String key, byte[] value) {
		// do nothing
	}

	@Override
	public void putDouble(String key, double value) {
		// do nothing
	}

	@Override
	public void putFloat(String key, float value) {
		// do nothing
	}

	@Override
	public void putInt(String key, int value) {
		// do nothing
	}

	@Override
	public void putLong(String key, long value) {
		// do nothing
	}

	@Override
	public void remove(String key) {
		// do nothing
	}

	@Override
	public void removeNode() throws BackingStoreException {
		// do nothing
	}

	@Override
	public IPreferences restrict(String scopeName) {
		return this;
	}

	@Override
	public void setModifierString(String key, String modifiers) {
		// do nothing
	}

	@Override
	public void sync() throws BackingStoreException {
		// do nothing
	}
}
