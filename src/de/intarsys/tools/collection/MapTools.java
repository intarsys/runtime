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
package de.intarsys.tools.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for Map extensions.
 */
public class MapTools {
	//
	public static final String SEPARATOR = "=";

	/**
	 * Define new entries in <code>map</code> from all declaration strings in
	 * <code>declarations</code>. For every string in the collection,
	 * {@link #defineEntries(Map, String)} is called.
	 * 
	 * @param map
	 *            The map to receive the new declarations
	 * @param declarations
	 *            A collection of declaration strings.
	 * 
	 * @return The parameter <code>map</code>
	 */
	public static Map defineEntries(Map map, List<String> declarations) {
		if (declarations == null) {
			return map;
		}
		for (Iterator<String> i = declarations.iterator(); i.hasNext();) {
			defineEntries(map, i.next());
		}
		return map;
	}

	/**
	 * Define new entries in <code>map</code> from <code>declarations</code>.
	 * For the "declarations" syntax see {@link ReaderTools#readEntry}.
	 * 
	 * @param map
	 *            The map to receive the new declarations
	 * @param declarations
	 *            A collection of declaration strings.
	 * 
	 * @return The parameter <code>map</code>
	 */
	public static Map defineEntries(Map map, String declaration) {
		if (StringTools.isEmpty(declaration)) {
			return map;
		}
		Reader r = new StringReader(declaration);
		try {
			while (true) {
				Map.Entry entry = ReaderTools.readEntry(r, ';');
				if (entry == null) {
					break;
				}
				if (entry.getKey() != null) {
					map.put(entry.getKey(), entry.getValue());
				}
			}
		} catch (IOException e) {
			// no io exception from string reader
		}
		return map;
	}

	public static Object get(Map map, Object key, Object defaultValue) {
		if (map == null) {
			return defaultValue;
		}
		Object value = map.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	public static String get(Map map, Object key, String defaultValue) {
		if (map == null) {
			return defaultValue;
		}
		Object value = map.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value.toString();
	}

	public static String toStringDeep(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder();
		toStringDeep(map, null, sb);
		return sb.toString();
	}

	/**
	 * Serialize the {@link Map} to a {@link String}. This is done recursively,
	 * i.e. all {@link Map} substructures are converted, too. The result is a
	 * String where the keys are path names.
	 * 
	 * Example, .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * a="b";x.i="12";x.j.last="nn"
	 * </pre>
	 * 
	 * 
	 * @param map
	 * @return The string representation of a {@link Map}
	 */
	static public void toStringDeep(Map<String, ?> map, String prefix,
			StringBuilder sb) {
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, ?> entry = (Map.Entry<String, ?>) it.next();
			String key = entry.getKey();
			if (!StringTools.isEmpty(prefix)) {
				key = prefix + "." + key;
			}
			Object value = entry.getValue();
			if (value instanceof Map) {
				toStringDeep((Map) value, key, sb);
			} else {
				sb.append(key);
				sb.append("=");
				sb.append(StringTools.quote(String.valueOf(value)));
				sb.append(";");
			}
		}
	}

	private MapTools() {
		super();
	}

}
