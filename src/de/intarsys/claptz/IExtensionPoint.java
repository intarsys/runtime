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

import de.intarsys.tools.attribute.IAttributeSupport;

/**
 * An object defining a "plugin" point, where otherwise unrelated client code
 * may register an {@link IExtension} for use and activation by the
 * IExtensionPoint provider.
 * <p>
 * The {@link IExtensionPoint} provider gives other code the possibility to
 * enhance its implementation with code totally unknown at the time of providing
 * (designing) the provider.
 * <p>
 * The {@link IExtension} provided may be code or data of any kind, thats up to
 * the API contract defined by the {@link IExtensionPoint} provider.
 */
public interface IExtensionPoint extends IAttributeSupport {

	/**
	 * All IExtensions provided so far. The returned array does not reflect the
	 * internal structure, so you are safe to change without side effects. The
	 * returned array is always in the same order, this means especially that a
	 * new IExtension object is added to the end of the array.
	 * 
	 * When there are no extensions defined, this method returns an empty array.
	 * 
	 * @return All IExtensions provided so far.
	 */
	public IExtension[] getExtensions();

	/**
	 * The unique id for the {@link IExtensionPoint}.
	 * 
	 * @return The unique id for the {@link IExtensionPoint}.
	 */
	public String getId();

	/**
	 * An optional functor object that can handle the installation semantics of
	 * the {@link IExtensionPoint}. If declared, the
	 * {@link IExtensionPointHandler} is executed immediately after the
	 * registration of the {@link IExtensionPoint}. This means that
	 * {@link IExtension} declarations to the {@link IExtensionPoint} will
	 * arrive AFTER the installation, take care to handle
	 * {@link IExtensionPoint} event notification if necessary.
	 * 
	 * @return An optional functor object that can handle the installation
	 *         semantics of the {@link IExtensionPoint}.
	 */
	public IExtensionPointHandler getInstaller();

	/**
	 * The object providing the {@link IExtensionPoint}. This object defines the
	 * syntax and semantics associated with the {@link IExtensionPoint}.
	 * 
	 * @return The object providing the {@link IExtensionPoint}
	 */
	// todo review this method
	public IInstrument getProvider();

}
