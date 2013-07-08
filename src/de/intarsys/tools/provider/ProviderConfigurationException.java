/*
 * @(#)ServiceConfigurationError.java	1.5 06/04/10
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package de.intarsys.tools.provider;

/**
 * Error thrown when something goes wrong while loading a service provider.
 * 
 */

public class ProviderConfigurationException extends RuntimeException {

	/**
	 * Constructs a new instance with the specified message.
	 * 
	 * @param msg
	 *            The message, or <tt>null</tt> if there is no message
	 * 
	 */
	public ProviderConfigurationException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a new instance with the specified message and cause.
	 * 
	 * @param msg
	 *            The message, or <tt>null</tt> if there is no message
	 * 
	 * @param cause
	 *            The cause, or <tt>null</tt> if the cause is nonexistent or
	 *            unknown
	 */
	public ProviderConfigurationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
