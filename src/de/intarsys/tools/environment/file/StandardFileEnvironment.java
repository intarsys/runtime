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
package de.intarsys.tools.environment.file;

import java.io.File;

public class StandardFileEnvironment implements IFileEnvironment {

	private File baseDir;

	private File profileDir;

	private File workingDir;

	private File tempDir;

	public StandardFileEnvironment() {
		baseDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
		profileDir = new File(System.getProperty("user.home")); //$NON-NLS-1$
		tempDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		workingDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
	}

	public StandardFileEnvironment(File pBaseDir, File pProfileDir) {
		baseDir = pBaseDir;
		profileDir = pProfileDir;
		tempDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		workingDir = pBaseDir;
	}

	public StandardFileEnvironment(File pBaseDir, File pProfileDir, File pTempDir,
			File pWorkingDir) {
		baseDir = pBaseDir;
		profileDir = pProfileDir;
		tempDir = pTempDir;
		workingDir = pWorkingDir;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public File getTempDir() {
		return tempDir;
	}

	public File getWorkingDir() {
		return workingDir;
	}

}
