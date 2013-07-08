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
package de.intarsys.tools.reporter;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.logging.LogTools;

/**
 * The default reporting implementation for the Stage kernel. This one is just
 * logging to java standard logging.
 * 
 */
public class DefaultReporter implements IReporter {

	final private Logger logger;

	public DefaultReporter() {
		this(LogTools.getLogger(DefaultReporter.class));
	}

	public DefaultReporter(Logger logger) {
		super();
		this.logger = logger;
	}

	protected Logger getLogger() {
		return logger;
	}

	public void reportActivityEnd() {
		if (getLogger().isLoggable(Level.FINEST)) {
			getLogger().log(Level.FINEST, "end activity "); //$NON-NLS-1$
		}
	}

	public void reportActivityStart(String activity, int style) {
		if (getLogger().isLoggable(Level.FINEST)) {
			getLogger().log(Level.FINEST, "start activity " + activity); //$NON-NLS-1$
		}
	}

	public void reportError(String title, String message, Throwable t, int style) {
		getLogger().log(Level.SEVERE, message, t);
	}

	public void reportMessage(String title, String message, int style) {
		getLogger().log(Level.INFO, message);
	}

	public void reportProgress(String text, int percent, int style) {
		if (getLogger().isLoggable(Level.FINEST)) {
			getLogger().log(Level.FINEST, text + " [" + percent + "%]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void reportStatus(String text, int style) {
		getLogger().log(Level.INFO, text);
	}
}
