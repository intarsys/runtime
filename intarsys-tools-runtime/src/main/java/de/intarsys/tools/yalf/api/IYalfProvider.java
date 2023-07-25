/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.yalf.api;

import de.intarsys.tools.locator.ILocator;

/**
 * The main entry point for implementing a concrete YALF component.
 * 
 * @param <R>
 *            The type of log record / event that is created by the logging
 *            service provider (e.g. java.util.logging.LogRecord)
 */
public interface IYalfProvider<R> {

	/**
	 * Reconfigure the log component with the configuration script referenced by
	 * locator. The script needs to be in an appropriate syntax for the target
	 * component.
	 * 
	 * @param locator
	 */
	public void configure(ILocator locator);

	/**
	 * A name that may be used for looking up a resource containing the default
	 * configuration for the provider (e.g. "logging.properties").
	 * 
	 * @return
	 */
	public String getDefaultConfigurationName();

	/**
	 * Get an {@link IHandlerFactory} for a goven {@link IHandler} class.
	 * 
	 * @param clazz
	 * @return
	 */
	public <H extends IHandler<R>> IHandlerFactory<R, H> getFactory(Class<H> clazz);

	/**
	 * Get the {@link ILogger} named "name". The name "" AND "ROOT" designates
	 * the ROOT logger.
	 * 
	 * @param name
	 * @return
	 */
	public <H extends IHandler<R>> ILogger getLogger(String name);

	/**
	 * Get the {@link IMDC} for this log component.
	 * 
	 * @return
	 */
	public IMDC getMDC();

	/**
	 * Return true if the provider has already been configured via its own
	 * internal initialization mechanism.
	 */
	boolean isConfigured();
}
