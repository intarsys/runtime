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
package de.intarsys.tools.message;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A wrapper class for defining and accessing ResourceBundles.
 */
public class MessageBundle {
	protected static URL[] createURLs(String classpath) {
		if (classpath == null) {
			return new URL[0];
		}
		String[] names = classpath.split(";");
		URL[] urls = new URL[names.length];
		try {
			for (int i = 0; i < urls.length; i++)
				urls[i] = new File(names[i]).toURI().toURL();
		} catch (MalformedURLException e) {
			return new URL[0];
		}
		return urls;
	}

	private ClassLoader classLoader;

	private Locale locale = Locale.getDefault();

	private String path;

	private ResourceBundle resourceBundle;

	/**
	 * 
	 */
	public MessageBundle() {
		super();
	}

	public MessageBundle(String path, ClassLoader classLoader) {
		super();
		this.path = path;
		this.classLoader = classLoader;
	}

	public MessageBundle(String path, String classpath) {
		super();
		this.path = path;
		classLoader = new URLClassLoader(createURLs(classpath), Thread
				.currentThread().getContextClassLoader());
	}

	public String basicFormat(String pattern, Object... objects) {
		return MessageFormat.format(pattern, objects);
	}

	protected Object basicGetObject(String key) {
		try {
			ResourceBundle myBundle = getResourceBundle();
			if (myBundle != null) {
				return myBundle.getObject(key);
			}
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	public String basicGetString(String key) {
		try {
			ResourceBundle myBundle = getResourceBundle();
			if (myBundle != null) {
				return myBundle.getString(key);
			}
			return null;
		} catch (RuntimeException e) {
			return null;
		}
	}

	protected ResourceBundle createResourceBundle() {
		if (getClassLoader() == null) {
			return ResourceBundle.getBundle(getPath(), getLocale());
		}
		return ResourceBundle.getBundle(getPath(), getLocale(),
				getClassLoader());
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	protected String getFallbackString(String key, Object... objects) {
		StringBuilder sb = new StringBuilder();
		sb.append("{"); //$NON-NLS-1$
		sb.append(key);
		sb.append("}"); //$NON-NLS-1$
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				sb.append("["); //$NON-NLS-1$
				sb.append(objects[i]);
				sb.append("]"); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

	public Locale getLocale() {
		return locale;
	}

	public Message getMessage(String key) {
		return new Message(this, key);
	}

	public Object getObject(String key) {
		return basicGetObject(key);
	}

	public String getPath() {
		return path;
	}

	public ResourceBundle getResourceBundle() {
		if (resourceBundle == null) {
			resourceBundle = createResourceBundle();
		}
		return resourceBundle;
	}

	public String getString(String key) {
		String result = basicGetString(key);
		if (result == null) {
			return getFallbackString(key, new Object[] {});
		}
		return result.replace("''", "'");
	}

	public String getString(String key, Object... args) {
		String pattern = basicGetString(key);
		if (pattern == null) {
			return getFallbackString(key, args);
		}
		return MessageFormat.format(pattern, args);
	}

	public boolean lookupString(String key) {
		String result = basicGetString(key);
		if (result == null) {
			return false;
		}
		return true;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setPath(String resourcesName) {
		this.path = resourcesName;
	}
}
