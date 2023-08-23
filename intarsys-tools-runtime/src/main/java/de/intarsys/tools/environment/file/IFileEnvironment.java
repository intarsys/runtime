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
 * An object describing the file system context.
 */
public interface IFileEnvironment {

	/**
	 * The base directory. Most operations will be performed relative to this.
	 * 
	 * <p>
	 * Normally this will be equal to the VM "user.dir" property.
	 * 
	 * @return The platform base directory.
	 */
	File getBaseDir();

	/**
	 * The directory used for application private data.
	 * 
	 * <p>
	 * This defaults to the {@link #getProfileDir()}
	 * 
	 * @return The platform data directory.
	 */
	default File getDataDir() {
		return getProfileDir();
	}

	/**
	 * Some user specific private directory. Most user related concepts like
	 * preferences will be defined relative to this directory.
	 * 
	 * <p>
	 * Normally this will be equal to the VM "user.home" property or an application
	 * defined subdirectory.
	 * 
	 * @return Some private directory.
	 * 
	 */
	File getProfileDir();

	/**
	 * The directory for temporary files.
	 * 
	 * <p>
	 * Normally this will be equal to the VM "java.io.tmpdir" property.
	 * 
	 * @return The directory for temporary files.
	 */
	File getTempDir();

	/**
	 * The working directory. There's currently no use of this one.
	 * 
	 * <p>
	 * Normally this will be equal to the VM "user.dir" property.
	 * 
	 * @return The working directory.
	 */
	File getWorkingDir();

}
