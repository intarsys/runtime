/*
 * @(#)ServiceLoader.java	1.10 06/04/10
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package de.intarsys.tools.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.stream.StreamTools;

/**
 * This is an alternative service- or provider loading tool.
 * <p>
 * The standard {@link ServiceLoader} architecture is quite unfriendly to
 * reflective access and is even more unfriendly to a lightweight generic
 * build&deployment procedure.
 * <p>
 * This implementation uses a single file "META-INF/provider/provider.list" to
 * enumerate service/implementation pairs. Each service is listed on a line of
 * its own. The service is followed by a ";", then followed by the
 * implementation class. Empty lines are ignored.
 */

public class Providers {

	static class ProviderEntry {
		public String serviceName;
		public String providerName;
		public Object provider;
	}

	public static final char COMMENT = '#';

	public static final String SEPARATOR = ";";

	private static final Map<ClassLoader, Providers> providers = new HashMap<ClassLoader, Providers>();

	private static final String PROVIDERLIST = "META-INF/provider/provider.list";

	static public Providers get() {
		return get(Thread.currentThread().getContextClassLoader());
	}

	static public Providers get(ClassLoader classloader) {
		synchronized (providers) {
			Providers result = providers.get(classloader);
			if (result == null) {
				try {
					result = new Providers(classloader);
				} catch (IOException e) {
					throw new ProviderConfigurationException(
							"error creating providers", e);
				}
				providers.put(classloader, result);
			}
			return result;
		}
	}

	private ClassLoader loader;

	private List<ProviderEntry> entries = new ArrayList<ProviderEntry>();

	private static final Logger Log = Logger
			.getLogger("de.intarsys.tools.provider");

	protected Providers(ClassLoader cl) throws IOException {
		loader = cl;
		init();
	}

	protected void addProvider(ProviderEntry entry) {
		for (ProviderEntry temp : entries) {
			if (temp.serviceName.equals(entry.serviceName)
					&& temp.providerName.equals(entry.providerName)) {
				return;
			}
		}
		entries.add(entry);
	}

	private void init() throws IOException {
		Enumeration<URL> providerlistUrls;
		if (loader == null) {
			providerlistUrls = ClassLoader.getSystemResources(PROVIDERLIST);
		} else {
			providerlistUrls = loader.getResources(PROVIDERLIST);
		}
		while (providerlistUrls.hasMoreElements()) {
			URL providerlistUrl = providerlistUrls.nextElement();
			InputStream is = null;
			try {
				is = providerlistUrl.openStream();
				register(is);
			} finally {
				StreamTools.close(is);
			}
		}
	}

	public <S> Iterator<S> lookupProviders(final Class<S> service) {
		return new Iterator<S>() {

			private ProviderEntry current;

			private String serviceName = service.getName();

			private Iterator<ProviderEntry> it = entries.iterator();

			@Override
			public boolean hasNext() {
				if (current != null) {
					return true;
				}
				while (it.hasNext()) {
					ProviderEntry temp = it.next();
					if (serviceName.equals(temp.serviceName)) {
						current = temp;
						return true;
					}
				}
				return false;
			}

			@Override
			public S next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				ProviderEntry temp = current;
				current = null;
				if (temp.provider == null) {
					try {
						temp.provider = Class.forName(temp.providerName, true,
								loader).newInstance();
					} catch (Throwable e) {
						Log.log(Level.FINEST, "loading provider failed", e);
						throw new ProviderConfigurationException("error loading "
								+ temp.providerName, e);
					}
				}
				return (S) temp.provider;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public void register(InputStream is) throws IOException {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(is, "utf-8"));
			while (registerLine(r))
				;
		} finally {
			StreamTools.close(r);
		}
	}

	protected boolean registerLine(BufferedReader r) throws IOException {
		String ln = r.readLine();
		if (ln == null) {
			return false;
		}
		int ci = ln.indexOf(COMMENT);
		if (ci >= 0)
			ln = ln.substring(0, ci);
		ln = ln.trim();
		if (ln.length() == 0) {
			return true;
		}
		String[] parts = ln.split(SEPARATOR);
		if (parts.length < 2) {
			return true;
		}
		ProviderEntry entry = new ProviderEntry();
		entry.serviceName = parts[0].trim();
		entry.providerName = parts[1].trim();
		addProvider(entry);
		return true;
	}
}
