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
package de.intarsys.tools.monitor;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A monitor allows "monitoring" events along thread as a sequence of samples.
 * 
 */
public interface IMonitor {
	/**
	 * Attach this {@link IMonitor} to the current {@link Thread}.
	 * 
	 * <p>
	 * Calling this method indicates the beginning of a measuring period
	 * represented by a {@link ITrace}. This method must always be paired with a
	 * call to "detach()".
	 * </p>
	 * 
	 * @return The new IMonitorTrace.
	 */
	public ITrace attach();

	/**
	 * Detach this {@link IMonitor} from the current {@link Thread}. This call
	 * terminates the current {@link ITrace} instance.
	 */
	public void detach();

	/**
	 * Return an {@link ITrace} that is currently under construction.
	 * 
	 * @return The {@link ITrace} that is currently under construction.
	 */
	public ITrace getCurrentTrace();

	/**
	 * A map containing key/value pairs representing all statistical
	 * informations derived from the samples.
	 * 
	 * @return A map containing key/value pairs representing all statistical
	 *         informations derived from the samples.
	 */
	public Map getData();

	/**
	 * A map containing key/value pairs with string representations for the
	 * statistical information from <code>getData</code>.
	 * 
	 * @return A map containing key/value pairs with string representations for
	 *         the statistical information from <code>getData</code>.
	 */
	public Map getFormattedData();

	/**
	 * The associated {@link Logger} instance.
	 * 
	 * @return The associated {@link Logger} instance.
	 */
	public Logger getLogger();

	/**
	 * The name of this monitor.
	 * 
	 * @return The name of this monitor.
	 */
	public String getName();

	/**
	 * A list of {@link ITrace} instances.
	 * 
	 * <p>
	 * This may be null if no traces are stored (see setCollectAll()).
	 * </p>
	 * 
	 * @return A list of {@link ITrace} instances.
	 */
	public List<ITrace> getTraces();

	/**
	 * Reset all information in this monitor.
	 */
	public void reset();
}
