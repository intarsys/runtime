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
package de.intarsys.claptz;

/**
 * An object that handles the semantics of {@link IExtensionPoint} and/or
 * {@link IExtension} instances.
 * 
 * An {@link IExtensionPointHandler} has a 1:1 relationship to an
 * {@link IExtensionPoint}. The relationship is established exactly once after
 * creation by calling
 * {@link IExtensionPointHandler#setExtensionPoint(IExtensionPoint)}
 * 
 */
public interface IExtensionPointHandler {

	public IExtensionPoint getExtensionPoint();

	/**
	 * Install a new {@link IExtensionPoint}.
	 * 
	 * @throws InstrumentRegistryException
	 *             TODO
	 */
	public void install() throws InstrumentRegistryException;

	/**
	 * Install a new {@link IExtension}.
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 *             TODO
	 */
	public void install(IExtension extension)
			throws InstrumentRegistryException;

	/**
	 * <code>true</code> if this installer is currently active.
	 * 
	 * An inactive installer is not requested to install/uninstall an
	 * {@link IExtensionPoint} or {@link IExtension} by the framework. It is
	 * assumed that the user triggers deferred installation at some later
	 * moment.
	 * 
	 * @return <code>true</code> if this installer is currently active.
	 */
	public boolean isActive();

	public void setExtensionPoint(IExtensionPoint extensionPoint);

	/**
	 * Uninstall a previously installed {@link IExtensionPoint}.
	 * 
	 * @throws InstrumentRegistryException
	 *             TODO
	 */
	public void uninstall() throws InstrumentRegistryException;

	/**
	 * Uninstall a previously installed {@link IExtension}.
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 *             TODO
	 */
	public void uninstall(IExtension extension)
			throws InstrumentRegistryException;

	/**
	 * Update an {@link IExtension}.
	 * <p>
	 * This method updates the serialization representation of the extension.
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 *             TODO
	 */
	public void update(IExtension extension) throws InstrumentRegistryException;
}
