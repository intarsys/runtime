/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.claptz.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * A class loader for instruments.
 * 
 * <p>
 * An instrument that is loaded at runtime from the file system is equipped with
 * its own classloader. The classloader is a direct or indirect child of the
 * IStage classloader. If the instrument has prerequisites, the prerequiste's
 * classloaders are the "parents" of this.
 * 
 * </p>
 */
public class StandardInstrumentClassLoader extends URLClassLoader {

	protected static ClassLoader create(StandardInstrument instrument,
			ClassLoader parent) {
		File base = new File(instrument.getBaseDir(),
				StandardInstrument.META_DIRECTORY);
		URL[] urls = createURLs(base);
		if (urls.length == 0) {
			return parent;
		}
		return new StandardInstrumentClassLoader(urls, instrument, parent);
	}

	protected static URL[] createURLs(File metaDir) {
		if (metaDir == null) {
			return new URL[0];
		}

		List result = new ArrayList();
		File jarDir = new File(metaDir, "lib"); //$NON-NLS-1$
		File classesDir = new File(metaDir, "classes"); //$NON-NLS-1$

		// check all jar files in the lib directory
		String[] jarFiles = jarDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(".jar")) && //$NON-NLS-1$
						(new File(dir, name).canRead());
			}
		});

		try {
			if (classesDir.exists()) {
				result.add(new URL("file", //$NON-NLS-1$
						"", //$NON-NLS-1$
						classesDir.getAbsolutePath() + "/" //$NON-NLS-1$
				));
			}
			if (jarFiles != null) {
				for (int i = 0; i < jarFiles.length; i++) {
					result.add(new URL("file", //$NON-NLS-1$
							"", //$NON-NLS-1$
							jarDir.getAbsolutePath() + "/" + jarFiles[i] //$NON-NLS-1$
					));
				}
			}
			return (URL[]) result.toArray(new URL[result.size()]);
		} catch (MalformedURLException e) {
			return new URL[0];
		}
	}

	final private StandardInstrument instrument;

	protected StandardInstrumentClassLoader(URL[] urls,
			StandardInstrument instrument, ClassLoader parent) {
		super(urls, parent);
		this.instrument = instrument;
	}

	public StandardInstrument getInstrument() {
		return instrument;
	}

	@Override
	public URL getResource(String name) {
		URL url = findResource(name);
		if (url == null) {
			if (getParent() != null) {
				url = getParent().getResource(name);
			}
		}
		return url;
	}

	/**
	 * Redefines loading sequence so that implementations "local" to the
	 * instrument are preferred
	 * 
	 * @param name
	 * @param resolve
	 * 
	 * @return The newly loaded class.
	 * 
	 * @throws ClassNotFoundException
	 */
	@Override
	protected synchronized Class loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class c = findLoadedClass(name);
		if (c == null) {
			try {
				c = findClass(name);
			} catch (ClassNotFoundException e) {
				if (getParent() != null) {
					c = getParent().loadClass(name);
				} else {
					c = getSystemClassLoader().loadClass(name);
				}
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}
}
