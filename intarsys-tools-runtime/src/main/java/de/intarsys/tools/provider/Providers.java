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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.LogTools;

/**
 * This is an alternative service- or provider loading tool.
 * <p>
 * The standard {@link ServiceLoader} architecture is quite unfriendly to
 * reflective access and is even more unfriendly to a lightweight generic build
 * &amp; deployment procedure.
 * <p>
 * This implementation uses a single file "META-INF/provider/provider.list" to
 * enumerate service/implementation pairs. Each service is listed on a line of
 * its own. The service is followed by a ";", then followed by the
 * implementation class. Empty lines are ignored.
 */
public class Providers {

	static class ProviderEntry {
		protected String serviceName;
		protected String providerName;
		protected CompletableFuture<Object> provider = new CompletableFuture<>();
	}

	public static final char COMMENT = '#';

	public static final String SEPARATOR = ";";

	private static final Map<ClassLoader, Providers> PROVIDERS = new ConcurrentHashMap<>();

	private static final String PROVIDERLIST = "META-INF/provider/provider.list";

	private static final ILogger Log = LogTools.getLogger("de.intarsys.tools.provider");

	public static Providers get() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		return get(classLoader);
	}

	public static Providers get(ClassLoader classloader) {
		return PROVIDERS.computeIfAbsent(classloader, (key) -> new Providers(classloader));
	}

	private ClassLoader loader;

	private List<ProviderEntry> entries = new ArrayList<>();

	protected Providers(ClassLoader cl) {
		loader = cl;
		try {
			init();
		} catch (IOException e) {
			Log.log(Level.WARN, "loading provider definitions failed ({})", ExceptionTools.getMessage(e));
		}
	}

	protected void addProvider(ProviderEntry entry) {
		for (ProviderEntry temp : entries) {
			if (temp.serviceName.equals(entry.serviceName) && temp.providerName.equals(entry.providerName)) {
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

			private Object current;

			private String serviceName = service.getName();

			private Iterator<ProviderEntry> it = entries.iterator();

			@Override
			public boolean hasNext() {
				if (current != null) {
					return true;
				}
				while (it.hasNext()) {
					ProviderEntry providerEntry = it.next();
					if (serviceName.equals(providerEntry.serviceName)) {
						if (!providerEntry.provider.isDone()) {
							try {
								providerEntry.provider.complete(
										Class.forName(providerEntry.providerName, true, loader).getDeclaredConstructor()
												.newInstance());
							} catch (Throwable t) {
								Log.log(Level.WARN, "loading provider for {} failed ({})", providerEntry.serviceName,
										ExceptionTools.getMessage(t));
								providerEntry.provider.completeExceptionally(t);
							}
						}
						if (!providerEntry.provider.isCompletedExceptionally()) {
							current = ExceptionTools.futureSimpleGet(providerEntry.provider);
							return true;
						}
					}
				}
				return false;
			}

			@Override
			public S next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Object provider = current;
				current = null;
				return (S) provider;
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
			r = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			while (registerLine(r)) {
				//
			}
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
		if (ci >= 0) {
			ln = ln.substring(0, ci);
		}
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
