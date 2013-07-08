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
package de.intarsys.tools.locator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import de.intarsys.tools.file.TempTools;
import de.intarsys.tools.stream.StreamTools;

/**
 * An abstract superclass for implementing {@link ILocator}.
 * 
 */
abstract public class CommonLocator implements ILocator, Serializable {

	static final long serialVersionUID = 1;

	private boolean readOnly = false;

	protected ILocator createTempFileLocator() throws IOException {
		File file = TempTools.createTempFile("locator", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
		file.deleteOnExit();
		InputStream is = null;
		OutputStream os = null;
		try {
			is = getInputStream();
			os = new FileOutputStream(file);
			// can't use auto close here
			StreamTools.copyStream(is, false, os, false);
		} finally {
			StreamTools.close(is);
			StreamTools.close(os);
		}
		ILocator tempFileLocator = new FileLocator(file);
		tempFileLocator.setReadOnly();
		return tempFileLocator;
	}

	public void delete() throws IOException {
		throwUnsupported();
	}

	public long getLength() throws IOException {
		return -1;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void rename(String newName) throws IOException {
		throwUnsupported();
	}

	public void setReadOnly() {
		readOnly = true;
	}

	protected void throwReadOnly() throws IOException {
		throw new IOException("read only"); //$NON-NLS-1$
	}

	protected void throwUnsupported() throws IOException {
		throw new IOException("unsupported operation"); //$NON-NLS-1$
	}
}
