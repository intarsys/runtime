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
package de.intarsys.tools.message;

import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import de.intarsys.tools.nls.INlsContext;
import de.intarsys.tools.nls.NlsContext;

/**
 * The basic {@link IMessageBundle} implementation.
 * 
 * This delegates directly to the Java {@link ResourceBundle} implementation.
 * 
 * The default {@link Locale} is provided by the {@link INlsContext} component.
 */
public class BasicMessageBundle extends CommonMessageBundle {

	private ResourceBundle resourceBundle;

	private final Locale locale;

	private ResourceBundle.Control control = new ResourceBundle.Control() {
		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			if (baseName == null) {
				throw new NullPointerException();
			}
			Locale defaultLocale = NlsContext.get().getLocale();
			return locale.equals(defaultLocale) ? null : defaultLocale;
		}
	};

	protected BasicMessageBundle(CommonMessageBundleFactory factory, String name, Locale locale,
			ClassLoader classLoader) {
		super(factory, name, classLoader);
		this.locale = locale;
	}

	protected Object basicGetObject(String code) {
		try {
			ResourceBundle myBundle = getResourceBundle();
			if (myBundle != null) {
				return myBundle.getObject(code);
			}
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	protected ResourceBundle createResourceBundle() {
		if (getClassLoader() == null) {
			return ResourceBundle.getBundle(getName(), getLocale(), control);
		}
		return ResourceBundle.getBundle(getName(), getLocale(), getClassLoader(), control);
	}

	@Override
	public Set<String> getCodes() {
		try {
			ResourceBundle myBundle = getResourceBundle();
			if (myBundle != null) {
				return myBundle.keySet();
			}
			return Collections.emptySet();
		} catch (RuntimeException e) {
			return Collections.emptySet();
		}
	}

	public Locale getLocale() {
		return locale;
	}

	@Override
	public String getPattern(String code) {
		try {
			ResourceBundle myBundle = getResourceBundle();
			if (myBundle != null) {
				return myBundle.getString(code);
			}
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = createResourceBundle();
		}
		return resourceBundle;
	}

	@Override
	public String getString(String code, Object... args) {
		String pattern = getPattern(code);
		if (pattern == null) {
			return getFallbackString(code, args);
		}
		return format(pattern, args);
	}

}
