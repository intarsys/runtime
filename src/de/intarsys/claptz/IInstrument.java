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

import java.io.IOException;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IMetaInfoSupport;
import de.intarsys.tools.environment.file.IFileEnvironment;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * The instrument specification. An instrument is a dynamic extension to the
 * system runtime.
 * <p>
 * The platform is designed to be extended via {@link IInstrument} objects.
 * These objects are registered with the {@link IInstrumentRegistry}. An
 * {@link IInstrument} may be anything from a static initialization, a
 * sophisticated handler for special document features, up to a endpoint for web
 * service call-ins or a scripting engine.
 * </p>
 * 
 * <p>
 * The instrument is a dynamically loadable component that is not known in
 * advance at development time. The instrument is represented by an
 * {@link IInstrument} instance
 * </p>
 * 
 * <p>
 * The {@link IInstrument} itself is loaded by a new classloader instance, so
 * that the implementation of the instrument need not be referenced in the vm
 * class path. This class loader tries to load its implementations from the
 * "classes" and "lib" directories described below.
 * </p>
 * 
 * <p>
 * An instrument is physically represented by a directory structure in the
 * "instruments" directory of the installation. The structure contains the
 * following information:
 * 
 * <ul>
 * <li>a "instrument.xml" file with configuration information.</li>
 * <li>an optional "classes" directory that contain ".class" files</li>
 * <li>an optional "lib" directory that contain ".jar" files</li>
 * </ul>
 * </p>
 * 
 * <pre>
 *           example:
 *            
 *           +-instruments
 *             | 
 *             +-instrument1
 *             | | 
 *             | +-INSTRUMENT-INF
 *             | | | 
 *             | | + instrument.xml 
 *             | | | 
 *             | | +-lib 
 *             | | | |
 *             | | | + ... your libraries
 *             | | | 
 *             | | +-classes
 *             | |   |
 *             | |   + ... your classes
 *             | |
 *             | + ... your files
 *             |
 *             + ... more instruments
 * 
 * </pre>
 * 
 * <p>
 * this example defines a single instrument "instrument1" with the home
 * directory "instrument1"
 * </p>
 * 
 */
public interface IInstrument extends IFileEnvironment, IClassLoaderSupport,
		IMessageBundleSupport, IMetaInfoSupport, IAttributeSupport {

	public void addPrerequisite(IInstrument prerequisite, String absentAction);

	public IExtension createExtension(String extensionPointId, String id)
			throws ObjectCreationException;

	public void flush() throws IOException;

	/**
	 * The extensions made by this {@link IExtensionProvider}.
	 * 
	 * @return The extensions made by this {@link IExtensionProvider}.
	 */
	public IExtension[] getExtensions();

	/**
	 * A unique name for the instrument.
	 * 
	 * <p>
	 * It is used for identifying uniquely a instrument within the
	 * {@link IInstrumentRegistry}. Clients may lookup instruments with well
	 * known names, for example the annotation handling instrument.
	 * </p>
	 * 
	 * <p>
	 * By convention this should conform to the standard java notation for class
	 * names.
	 * </p>
	 * 
	 * @return A string identifying the instrument uniquely.
	 */
	public String getId();

	/**
	 * The {@link IInstrumentRegistry} where this {@link IInstrument} is
	 * currently registered.
	 * 
	 * @return The {@link IInstrumentRegistry} where this {@link IInstrument} is
	 *         currently registered.
	 */
	public IInstrumentRegistry getInstrumentRegistry();

	/**
	 * The collection of IInstrumentPrerequisite objects that are required to be
	 * available for this one to run.
	 * 
	 * @return The collection of IInstrumentPrerequisite objects that are
	 *         required to be available for this one to run.
	 */
	public IInstrumentPrerequisite[] getPrerequisites();

	public State getState();

	/**
	 * Add <code>extension</code> to the {@link IExtension} instances managed by
	 * this {@link IExtensionProvider}
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 */
	public void registerExtension(IExtension extension)
			throws InstrumentRegistryException;

	public void removePrerequisite(IInstrument prerequisite);

	/**
	 * Remove <code>extension</code> from the {@link IExtension} instances
	 * managed by this {@link IExtensionProvider}
	 * 
	 * @param extension
	 * @throws InstrumentRegistryException
	 */
	public void unregisterExtension(IExtension extension)
			throws InstrumentRegistryException;
}
