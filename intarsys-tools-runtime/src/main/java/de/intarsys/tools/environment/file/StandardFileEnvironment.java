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
package de.intarsys.tools.environment.file;

import java.io.File;

/**
 * A standard implementation for {@link IFileEnvironment}.
 * 
 */
public class StandardFileEnvironment implements IFileEnvironment {

	private final File baseDir;

	private final File dataDir;

	private final File profileDir;

	private final File workingDir;

	private final File tempDir;

	/**
	 * Create an {@link IFileEnvironment} where
	 * 
	 * <code>
	 * baseDir = System.getProperty("user.dir")
	 * profileDir = System.getProperty("user.home")
	 * tempDir = System.getProperty("java.io.tmpdir")
	 * workingDir = System.getProperty("user.dir")
	 * </code>
	 */
	public StandardFileEnvironment() {
		baseDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
		profileDir = new File(System.getProperty("user.home")); //$NON-NLS-1$
		dataDir = profileDir;
		tempDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		workingDir = new File(System.getProperty("user.dir")); //$NON-NLS-1$
	}

	/**
	 * Create an {@link IFileEnvironment} where
	 * 
	 * <code>
	 * baseDir = as defined in pBaseDir
	 * profileDir = as defined in pProfileDir
	 * tempDir = System.getProperty("java.io.tmpdir")
	 * workingDir = as defined in pBaseDir
	 * </code>
	 * 
	 * @param pBaseDir
	 * @param pProfileDir
	 */
	public StandardFileEnvironment(File pBaseDir, File pProfileDir) {
		baseDir = pBaseDir;
		profileDir = pProfileDir;
		dataDir = profileDir;
		tempDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
		workingDir = pBaseDir;
	}

	/**
	 * Create an {@link IFileEnvironment} as defined in the parameters.
	 * 
	 * @param pBaseDir
	 * @param pProfileDir
	 * @param pTempDir
	 * @param pWorkingDir
	 */
	public StandardFileEnvironment(File pBaseDir, File pProfileDir, File pTempDir, File pWorkingDir) {
		baseDir = pBaseDir;
		profileDir = pProfileDir;
		dataDir = profileDir;
		tempDir = pTempDir;
		workingDir = pWorkingDir;
	}

	/**
	 * Create an {@link IFileEnvironment} as defined in the parameters.
	 * 
	 * @param pBaseDir
	 * @param pProfileDir
	 * @param pTempDir
	 * @param pWorkingDir
	 */
	public StandardFileEnvironment(File pBaseDir, File pProfileDir, File pDataDir, File pTempDir, File pWorkingDir) {
		baseDir = pBaseDir;
		profileDir = pProfileDir;
		dataDir = pDataDir;
		tempDir = pTempDir;
		workingDir = pWorkingDir;
	}

	public StandardFileEnvironment(String pBaseDir, String pProfileDir) {
		this(new File(pBaseDir), new File(pProfileDir));
	}

	public StandardFileEnvironment(String pBaseDir, String pProfileDir, String pTempDir, String pWorkingDir) {
		this(new File(pBaseDir), new File(pProfileDir), new File(pTempDir), new File(pWorkingDir));
	}

	public StandardFileEnvironment(String pBaseDir, String pProfileDir, String pDataDir, String pTempDir, String pWorkingDir) {
		this(new File(pBaseDir), new File(pProfileDir), new File(pDataDir), new File(pTempDir), new File(pWorkingDir));
	}

	@Override
	public File getBaseDir() {
		return baseDir;
	}

	@Override
	public File getDataDir() {
		return dataDir;
	}

	@Override
	public File getProfileDir() {
		return profileDir;
	}

	@Override
	public File getTempDir() {
		return tempDir;
	}

	@Override
	public File getWorkingDir() {
		return workingDir;
	}

}
