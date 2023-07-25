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
package de.intarsys.tools.yalf.common;

import de.intarsys.tools.yalf.api.ILogger;

/**
 * A common superclass for implementing {@link ILogger}.
 * 
 */
public abstract class CommonLogger implements ILogger {

	protected CommonLogger() {
		super();
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void config(String msg) {
		info(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void config(String msg, Object... args) {
		info(msg, args);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void debug(String msg) {
		debug(msg, new Object[0]);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void error(String msg) {
		severe(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void error(String msg, Object... args) {
		severe(msg, args);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void fine(String msg) {
		debug(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void fine(String msg, Object... args) {
		debug(msg, args);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void finer(String msg) {
		debug(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void finer(String msg, Object... args) {
		debug(msg, args);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void finest(String msg) {
		trace(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void finest(String msg, Object... args) {
		trace(msg, args);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void info(String msg) {
		info(msg, new Object[0]);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void severe(String msg) {
		severe(msg, new Object[0]);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void trace(String msg) {
		trace(msg, new Object[0]);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void warn(String msg) {
		warn(msg, new Object[0]);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 */
	public void warning(String msg) {
		warn(msg);
	}

	/**
	 * Provide convenience methods for seamless scripting access.
	 * 
	 * @param msg
	 * @param args
	 */
	public void warning(String msg, Object... args) {
		warn(msg, args);
	}

}
