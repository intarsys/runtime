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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A factory for an {@link ILocator}.
 * 
 */
public interface ILocatorFactory {
	/**
	 * Create an {@link ILocator}.
	 * 
	 * If location is null or an empty string, null is returned.
	 * 
	 * <code>location</code> may for example define a file in the file system
	 * and the factory returns the corresponding {@link FileLocator}.
	 * 
	 * The factory may return an {@link ILocator} to a resource that does not
	 * (yet) exist, but is a valid {@link ILocator}. The factory itself does not
	 * perform any check for physical existence on a syntactically valid
	 * {@link ILocator}.
	 * 
	 * If there's no way that a location can be resolved to a valid
	 * {@link ILocator}, the {@link ILocatorFactory} throws an
	 * {@link IOException}. If the failure is related to non-existence, the
	 * factory should throw a {@link FileNotFoundException}.
	 * 
	 * @param location
	 *            A location definition.
	 * @return The ILocator object created from <code>location</code>.
	 */
	public ILocator createLocator(String location) throws IOException;
}
