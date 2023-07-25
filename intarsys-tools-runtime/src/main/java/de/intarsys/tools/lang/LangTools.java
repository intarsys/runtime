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
package de.intarsys.tools.lang;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.intarsys.tools.collection.ArrayTools;
import de.intarsys.tools.collection.ListTools;
import de.intarsys.tools.collection.MapTools;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.StringTools;

public class LangTools {

	/**
	 * An {@link IBinding} that wraps a non-contained object.
	 */
	public static class SingletonBinding implements IBinding {
		private Object object;

		public SingletonBinding(Object object) {
			this.object = object;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Object getValue() {
			return object;
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
			this.object = value;
		}
	}

	/**
	 * Apply the given {@link Function} recursively to all leaf values (non-collection);
	 * 
	 * @param value
	 * @param function
	 * @return The modified {@code object}
	 */
	public static Object applyDeep(Object value, Function<IBinding, Object> function) {
		return applyDeep(value, function, new SingletonBinding(value));
	}

	/**
	 * Apply the given {@link Function} recursively to all leaf values (non-collection);
	 * 
	 * @param value
	 * @param function
	 * @return The modified {@code object}
	 */
	public static Object applyDeep(Object value, Function<IBinding, Object> function, IBinding binding) {
		if (value instanceof List) {
			return ListTools.applyDeep((List) value, function);
		} else if (value instanceof Map) {
			return MapTools.applyDeep((Map) value, function);
		} else if (value instanceof Object[]) {
			return ArrayTools.applyDeep((Object[]) value, function);
		} else if (value instanceof IArgs) {
			return ArgTools.applyDeep((IArgs) value, function);
		} else {
			Object mapped = function.apply(binding);
			if (mapped != value) {
				binding.setValue(mapped);
			}
			return mapped;
		}
	}

	/**
	 * Create a deep copy of {@code object} if it is a container type.
	 * 
	 * @param object
	 * @return A deep copy of {@code object}
	 */
	public static Object copyDeep(Object object) {
		if (object instanceof List) {
			return ListTools.copyDeep((List) object);
		} else if (object instanceof Map) {
			return MapTools.copyDeep((Map) object);
		} else if (object instanceof Object[]) {
			return ArrayTools.copyDeep((Object[]) object);
		} else if (object instanceof IArgs) {
			return ((IArgs) object).copy();
		} else if (object instanceof ICopyDeep) {
			return ((ICopyDeep) object).copyDeep();
		} else {
			return object;
		}
	}

	/**
	 * Answer {@code true} if a and b are equal.
	 * 
	 * @param a
	 * @param b
	 */
	public static boolean equals(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}

	public static Object firstNonNull(Object... values) {
		for (Object value : values) {
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Answer {@code true} if {@code value} is "empty-ish"
	 * 
	 * @param value
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			return StringTools.isEmpty((String) value);
		}
		if (value instanceof char[]) {
			return CharacterTools.isEmpty((char[]) value);
		}
		return false;
	}

	private LangTools() {
	}
}
