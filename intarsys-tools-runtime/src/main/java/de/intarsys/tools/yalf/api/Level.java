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

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

/**
 * An importance marker for a logging event.
 * 
 */
public class Level implements Comparable<Level> {

	public static final Level OFF = new Level("OFF", 1000);

	public static final Level SEVERE = new Level("SEVERE", 500);

	public static final Level WARN = new Level("WARN", 400);

	public static final Level INFO = new Level("INFO", 300);

	public static final Level DEBUG = new Level("DEBUG", 200);

	public static final Level TRACE = new Level("TRACE", 100);

	public static final Level ALL = new Level("ALL", 0);

	private static final Map<String, Level> LEVELS = new HashMap<>();

	static {
		LEVELS.put("SEVERE", SEVERE);
		LEVELS.put("FATAL", SEVERE);
		LEVELS.put("ERROR", SEVERE);
		LEVELS.put("WARN", WARN);
		LEVELS.put("WARNING", WARN);
		LEVELS.put("INFO", INFO);
		LEVELS.put("FINE", DEBUG);
		LEVELS.put("FINER", DEBUG);
		LEVELS.put("FINEST", TRACE);
		LEVELS.put("DEBUG", DEBUG);
		LEVELS.put("TRACE", TRACE);
		LEVELS.put("OFF", OFF);
		LEVELS.put("ALL", ALL);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Level}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Level}.
	 */
	public static Level getLogLevel(IArgs args, String name, Level defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = ArgTools.getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Level) {
			return (Level) optionValue;
		}
		if (optionValue instanceof String) {
			try {
				return parse(((String) optionValue).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return INFO;
	}

	public static Level parse(String name) {
		if (name == null) {
			return INFO;
		}
		Level result = LEVELS.get(name.toUpperCase());
		if (result == null) {
			return INFO;
		}
		return result;
	}

	private final String name;

	private final int weight;

	public Level(String name, int weight) {
		super();
		this.name = name;
		this.weight = weight;
	}

	/**
	 * Order is defined by weight.
	 * 
	 * Note: this class has a natural ordering that is inconsistent with equals
	 */
	@Override
	public int compareTo(Level o) { // NOSONAR
		return getWeight() - o.getWeight();
	}

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}

}
