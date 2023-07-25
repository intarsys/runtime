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
package de.intarsys.tools.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import de.intarsys.tools.component.SingletonClass;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * This is a central registry to support the concept of "aliasing" a new name to
 * a well known one.
 * 
 * Possible uses are aliased names in registries and so on.
 * 
 * Registering aliases is done by providing an "META-INF/aliases.properties" in
 * the classpath, the format is the classic properties format with
 * 
 * <pre>
 * alias=id
 * ...
 * </pre>
 * 
 */
@SingletonClass
public class Aliases {

	private static final ILogger Log = LogTools.getLogger(Aliases.class.getName());

	private static final Aliases ACTIVE = new Aliases();

	private static final String PATH = "META-INF/aliases.properties";

	public static Aliases get() {
		return ACTIVE;
	}

	private final Map<String, String> aliases = new HashMap<>();

	public Aliases() {
		try {
			init();
		} catch (IOException e) {
			Log.log(Level.WARN, "aliases error loading", e);
		}
	}

	public void addAlias(String alias, String id) {
		aliases.put(alias, id);
	}

	public void clear() {
		aliases.clear();
	}

	private void init() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		if (classloader == null) {
			classloader = ClassLoader.getSystemClassLoader();
		}
		Enumeration<URL> urls = classloader.getResources(PATH);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			Log.debug("aliases register from {}", url);
			InputStream is = null;
			try {
				is = url.openStream();
				register(is);
			} finally {
				StreamTools.close(is);
			}
		}
	}

	public void register(InputStream is) throws IOException {
		Properties p = new Properties();
		p.load(is);
		p.forEach((key, value) -> {
			Log.debug("aliases register {}={}", key, value);
			aliases.put((String) key, (String) value);
		});
	}

	public String resolve(String name) {
		String result = aliases.get(name);
		return result == null ? name : result;
	}
}
