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

import java.util.prefs.BackingStoreException;

import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.yalf.api.Level;

/**
 * An {@link IValueHolder} implementation that is backed up by preferences.
 * 
 */
public abstract class PrefValueHolder<T> implements IValueHolder<T> {

	private final boolean autoFlush;

	private final T defaultValue;

	private final String key;

	private final IPreferences preferences;

	protected PrefValueHolder(IPreferences preferences, String key, T defaultValue) {
		this(preferences, key, defaultValue, false);
	}

	protected PrefValueHolder(IPreferences preferences, String key, T defaultValue, boolean autoFlush) {
		super();
		this.preferences = preferences;
		this.key = key;
		this.defaultValue = defaultValue;
		this.autoFlush = autoFlush;
	}

	protected abstract void basicSet(T newValue);

	public T getDefaultValue() {
		return defaultValue;
	}

	public String getKey() {
		return key;
	}

	public IPreferences getPreferences() {
		return preferences;
	}

	@Override
	public T set(T newValue) {
		basicSet(newValue);
		if (autoFlush) {
			try {
				getPreferences().flush();
			} catch (BackingStoreException ex) {
				PACKAGE.Log.log(Level.WARN, ex.getMessage(), ex);
			}
		}
		return null;
	}
}
