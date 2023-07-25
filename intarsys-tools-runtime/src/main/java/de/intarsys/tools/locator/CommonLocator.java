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
package de.intarsys.tools.locator;

import java.io.File;
import java.io.IOException;

import de.intarsys.tools.file.TempTools;

/**
 * An abstract superclass for implementing {@link ILocator}.
 * 
 */
public abstract class CommonLocator extends AbstractLocator {

	static final long serialVersionUID = 1;

	protected ILocator createTempFileLocator() throws IOException {
		File file = TempTools.createTempFile("locator", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
		file.deleteOnExit();
		ILocator tempFileLocator = new FileLocator(file);
		LocatorTools.copy(this, tempFileLocator);
		tempFileLocator.setReadOnly();
		return tempFileLocator;
	}

	@Override
	public void delete() throws IOException {
		throwUnsupported();
	}

	@Override
	public long getLength() throws IOException {
		return -1;
	}

	@Override
	public void rename(String newName) throws IOException {
		throwUnsupported();
	}

	protected void throwReadOnly() throws IOException {
		throw new IOException("read only"); //$NON-NLS-1$
	}

	protected void throwUnsupported() throws IOException {
		throw new IOException("unsupported operation"); //$NON-NLS-1$
	}
}
