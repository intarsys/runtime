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

import de.intarsys.claptz.io.IInstrumentStore;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A registry for {@link IInstrument} instances.
 * 
 */
public interface IInstrumentRegistry {

	public IInstrument createInstrument(String id, IInstrumentStore store)
			throws ObjectCreationException;

	public IExtensionPoint[] getExtensionPoints();

	/**
	 * A collection of all IInstrument instances registered.
	 * 
	 * <p>
	 * You can not modify the result of this request.
	 * </p>
	 * 
	 * @return A collection of all IInstrument instances registered.
	 */
	public IInstrument[] getInstruments();

	public void load(IInstrumentStore store) throws InstrumentRegistryException;

	public IExtensionPoint lookupExtensionPoint(String name);

	/**
	 * Lookup the instrument with the id "name".
	 * 
	 * @param name
	 *            The name of the instrument to look up.
	 * 
	 * @return The instrument with the id "name".
	 */
	public IInstrument lookupInstrument(String name);

	/**
	 * Register a new instrument in the registry.
	 * 
	 * @param instrument
	 *            The new instrument instance.
	 * 
	 * @throws InstrumentRegistryException
	 * 
	 */
	public void registerInstrument(IInstrument instrument)
			throws InstrumentRegistryException;

	/**
	 * Unregister an instrument from the registry.
	 * 
	 * @param instrument
	 *            The instrument to be unregistered
	 */
	public void unregisterInstrument(IInstrument instrument)
			throws InstrumentRegistryException;
}
