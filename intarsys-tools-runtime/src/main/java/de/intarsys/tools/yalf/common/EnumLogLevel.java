/*
 * Copyright (c) 2007, intarsys GmbH
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

import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.yalf.api.Level;

/**
 * An enumeration of the standard Java log levels.
 * 
 */
@SuppressWarnings("java:S2160") // no equals required
public class EnumLogLevel extends EnumItem {

	/** The meta data for the enumeration. */
	public static final EnumMeta<EnumLogLevel> META = getMeta(EnumLogLevel.class);

	public static final EnumLogLevel UNDEFINED = new EnumLogLevel(Level.OFF); // $NON-NLS-1$
	public static final EnumLogLevel SEVERE = new EnumLogLevel(Level.SEVERE); // $NON-NLS-1$
	public static final EnumLogLevel WARNING = new EnumLogLevel(Level.WARN); // $NON-NLS-1$
	public static final EnumLogLevel INFO = new EnumLogLevel(Level.INFO); // $NON-NLS-1$
	public static final EnumLogLevel DEBUG = new EnumLogLevel(Level.DEBUG); // $NON-NLS-1$
	public static final EnumLogLevel TRACE = new EnumLogLevel(Level.TRACE); // $NON-NLS-1$

	static {
		UNDEFINED.setDefault();
	}

	private final Level level;

	/**
	 * 
	 */
	protected EnumLogLevel(Level level) {
		super(level.getName());
		this.level = level;
	}

	public Level getLevel() {
		return level;
	}

}
