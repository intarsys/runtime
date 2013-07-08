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
import java.io.IOException;

import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.valueholder.IValueHolder;
import de.intarsys.tools.valueholder.ObjectValueHolder;

/**
 * The factory for {@link FileLocator} objects.
 * 
 * {@link FileLocator} instances are created either using an absolute path name
 * or are looked up relative to the factory's parent. The default parent is the
 * base directory of the {@link IFileEnvironment}.
 * 
 */
public class FileLocatorFactory extends CommonLocatorFactory {

	/** flag if we synchronize synchronously with every check */
	private boolean synchSynchronous = true;

	final private IValueHolder<File> parent;

	public FileLocatorFactory() {
		super();
		parent = new ObjectValueHolder<File>(FileEnvironment.get().getBaseDir());
	}

	public FileLocatorFactory(File file) {
		super();
		this.parent = new ObjectValueHolder<File>(file);
	}

	public FileLocatorFactory(IValueHolder<File> vh) {
		super();
		this.parent = vh;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		location = FileTools.trimPath(location);
		File absolutePath = FileTools.resolvePath(parent.get(), location);
		FileLocator result = new FileLocator(absolutePath);
		result.setSynchSynchronous(isSynchSynchronous());
		return result;
	}

	public boolean isSynchSynchronous() {
		return synchSynchronous;
	}

	public void setSynchSynchronous(boolean synchSynchronous) {
		this.synchSynchronous = synchSynchronous;
	}
}
