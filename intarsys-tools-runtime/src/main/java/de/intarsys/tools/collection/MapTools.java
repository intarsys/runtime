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
package de.intarsys.tools.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.lang.LangTools;
import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for Map extensions.
 */
public class MapTools {

	public static class Builder<K, V> extends CommonBuilder {

		private Map<K, V> map;

		protected Builder(CommonBuilder parent, Map map) {
			super(parent);
			this.map = map;
		}

		@Override
		public Map<K, V> build() {
			return map;
		}

		public Builder<K, V> put(K key, V value) {
			map.put(key, value);
			return this;
		}

		public ListTools.Builder startList(K key) {
			return new ListTools.Builder(this, new ArrayList<>()) {
				@Override
				public CommonBuilder end() {
					MapTools.Builder.this.put(key, (V) this.build());
					return super.end();
				}
			};
		}

		public MapTools.Builder startMap(K key) {
			return new MapTools.Builder(this, new HashMap<>()) {
				@Override
				public CommonBuilder end() {
					MapTools.Builder.this.put(key, (V) this.build());
					return super.end();
				}
			};
		}

	}

	public static class MapBinding implements IBinding {
		private final Map.Entry entry;

		public MapBinding(Map.Entry entry) {
			this.entry = entry;
		}

		@Override
		public String getName() {
			return (String) entry.getKey();
		}

		@Override
		public Object getValue() {
			return entry.getValue();
		}

		@Override
		public boolean isDefined() {
			return true;
		}

		@Override
		public void setName(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setValue(Object value) {
			entry.setValue(value);
		}
	}

	private static final String TERMINATOR = ";";

	//
	public static final String SEPARATOR = "=";

	/**
	 * Apply the given {@link Function} recursively to all leaf values (non-collection) in
	 * {@code map}. {@code map} is modified to contain the results of the function.
	 * 
	 * @param map
	 * @param function
	 * @return The modified {@code map} object.
	 */
	public static Map applyDeep(Map map, Function<IBinding, Object> function) {
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			LangTools.applyDeep(entry.getValue(), function, new MapBinding(entry));
		}
		return map;
	}

	public static <K, V> Builder<K, V> builder() {
		return builder(new HashMap<>());
	}

	public static <K, V> Builder<K, V> builder(Map<K, V> map) {
		return new Builder<>(null, map);
	}

	/**
	 * Create a deep copy of {@code map}.
	 * 
	 * The result is a new {@link Map} where all collection like objects are
	 * deep-copied, all leaf objects are directly referenced.
	 * 
	 * @param map
	 * @return A deep copy of {@code map}
	 */
	public static Map copyDeep(Map map) {
		if (map == null) {
			return null; // NOSONAR
		}
		LinkedHashMap<Object, Object> result = new LinkedHashMap<>(map.size());
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			result.put(entry.getKey(), LangTools.copyDeep(entry.getValue()));
		}
		return result;
	}

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
	 * Define new entries in <code>map</code> from <code>declarations</code>. For
	 * the "declarations" syntax see {@link ReaderTools#readEntry}.
	 * 
	 * @param map        The map to receive the new declarations
	 * @param definition A collection of declaration strings.
	 * 
	 * @return The parameter <code>map</code>
	 */
	public static Map defineEntries(Map map, String definition) {
		if (StringTools.isEmpty(definition)) {
			return map;
		}
		Reader r = new StringReader(definition);
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

	/**
	 * Convert the <code>map</code> to a single-level {@link Map}. This is
	 * done recursively, i.e. all Map substructures are converted, too. The
	 * result is a {@link Map} where the keys are path names.
	 * 
	 * Example, where {} denotes a {@link Map} structure. .
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
	 * {
	 *   a -> "b"
	 *   x.i -> 12
	 *   x.j.last -> "nn"
	 * ]
	 * </pre>
	 * 
	 * 
	 * @param map
	 * @return The flat {@link Map} representation of the <code>map</code>
	 */
	public static Map<String, Object> flatten(Map<String, Object> map) {
		return flatten(map, null, new HashMap<>());
	}

	public static Map flatten(Map<String, Object> map, String prefix, Map<String, Object> target) {
		if (map == null) {
			return null; // NOSONAR
		}
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			if (!StringTools.isEmpty(prefix)) {
				key = prefix + "." + key;
			}
			Object value = entry.getValue();
			if (value instanceof Map) {
				flatten((Map) value, key, target);
			} else {
				target.put(key, value);
			}
		}
		return target;
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

	public static Object getPath(Map map, String path, Object defaultValue) {
		if (map == null) {
			return null;
		}
		if (StringTools.isEmpty(path) || ".".equals(path)) {
			return map;
		}
		String[] segments = path.split("\\.");
		int position = 0;
		while (position < segments.length - 1) {
			Object tempValue = get(map, segments[position], defaultValue);
			if (tempValue instanceof Map) {
				map = (Map) tempValue;
			} else {
				return null;
			}
			position++;
		}
		return get(map, segments[position], defaultValue);
	}

	public static String toStringDeep(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder();
		toStringDeep(map, null, sb);
		return sb.toString();
	}

	/**
	 * Serialize the {@link Map} into a {@link StringBuilder}. This is done
	 * recursively, i.e. all {@code Map} substructures are converted, too. The
	 * result is a String where the keys are path names.
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
	 * @param prefix
	 * @param sb
	 */
	public static void toStringDeep(Map<String, ?> map, String prefix, StringBuilder sb) {
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
				sb.append(SEPARATOR);
				sb.append(StringTools.quote(String.valueOf(value)));
				sb.append(TERMINATOR);
			}
		}
	}

	public static Map<?, ?> with(Object... objects) {
		Map result = new HashMap<>();
		int i = 0;
		while (i < objects.length) {
			result.put(objects[i], objects[i + 1]);
			i += 2;
		}
		return result;
	}

	private MapTools() {
		super();
	}

}
