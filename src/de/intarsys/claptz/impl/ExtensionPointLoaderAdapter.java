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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.claptz.IExtension;
import de.intarsys.claptz.IExtensionPointHandler;
import de.intarsys.claptz.PACKAGE;
import de.intarsys.tools.file.Loader;
import de.intarsys.tools.infoset.IElement;

/**
 * An {@link IExtensionPointHandler} that handles loading instructions for the
 * client.
 * 
 */
public class ExtensionPointLoaderAdapter extends ExtensionPointHandlerAdapter {
	public static final String VALUE_FALSE = "false";

	public static final String EA_READONLY = "readonly";

	public static final String EA_RECURSIVE = "recursive";

	public static final String XE_LOADNLS = "loadnls";

	public static final String EA_PATH = "path";

	public static final String XE_LOAD = "load";

	private static final Logger Log = PACKAGE.Log;

	private Loader loader;

	public ExtensionPointLoaderAdapter() {
		this(null);
	}

	public ExtensionPointLoaderAdapter(Loader loader) {
		if (loader == null) {
			loader = createLoader();
		}
		this.loader = loader;
	}

	@Override
	protected Object basicInstallInsert(IExtension extension, IElement element) {
		File parentDir = extension.getProvider().getBaseDir();
		if (XE_LOAD.equals(element.getName())) {
			String path = element.attributeValue(EA_PATH, null);
			boolean readOnly = Boolean.parseBoolean(element.attributeValue(
					EA_READONLY, VALUE_FALSE));
			boolean recursive = Boolean.parseBoolean(element.attributeValue(
					EA_RECURSIVE, VALUE_FALSE));
			try {
				loader.load(parentDir, path, readOnly, recursive);
			} catch (Exception e) {
				String msg = "error loading " + path;
				log(Log, Level.SEVERE, extension, element, msg, e);
			}
		} else if (XE_LOADNLS.equals(element.getName())) {
			String path = element.attributeValue(EA_PATH, null);
			boolean readOnly = Boolean.parseBoolean(element.attributeValue(
					EA_READONLY, VALUE_FALSE));
			boolean recursive = Boolean.parseBoolean(element.attributeValue(
					EA_RECURSIVE, VALUE_FALSE));
			try {
				loader.loadNLS(parentDir, path, readOnly, recursive);
			} catch (Exception e) {
				String msg = "error loading " + path;
				log(Log, Level.SEVERE, extension, element, msg, e);
			}
		} else {
			return super.basicInstallInsert(extension, element);
		}
		return null;
	}

	protected boolean basicLoadFile(File file, boolean readOnly)
			throws IOException {
		// this is only used by the default Loader
		return true;
	}

	protected Loader createLoader() {
		return new Loader() {
			@Override
			protected boolean basicLoadFile(File file, boolean readOnly,
					String path) throws IOException {
				return ExtensionPointLoaderAdapter.this.basicLoadFile(file,
						readOnly);
			}
		};
	}
}
