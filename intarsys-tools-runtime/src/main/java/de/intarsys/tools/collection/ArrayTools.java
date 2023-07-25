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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.functor.IBaseFunctor;
import de.intarsys.tools.lang.LangTools;

/**
 * Tools for dealing with Java arrays.
 * 
 */
public class ArrayTools {

	public static class ArrayBinding implements IBinding {
		private final Object[] array;
		private final int index;

		public ArrayBinding(Object[] array, int index) {
			super();
			this.array = array;
			this.index = index;
		}

		@Override
		public String getName() {
			return "" + index;
		}

		@Override
		public Object getValue() {
			return array[index];
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
			array[index] = value;
		}
	}

	/**
	 * Apply the given {@link Function} recursively to all leaf values (non-container) in
	 * {@code array}. {@code array} is modified to contain the results of the function.
	 * 
	 * @param array
	 * @param function
	 * @return The {@code array} object with modified members.
	 */
	public static Object[] applyDeep(Object[] array, Function<IBinding, Object> function) {
		int i = 0;
		for (Object value : array) {
			LangTools.applyDeep(value, function, new ArrayBinding(array, i++));
		}
		return array;
	}

	public static <A, B> B[] collect(A[] array, IBaseFunctor<B> converter, Class<B> clazz) {
		List<B> result = new ArrayList<>(array.length);
		for (A a : array) {
			try {
				result.add(converter.perform(a));
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result.toArray((B[]) Array.newInstance(clazz, result.size()));
	}

	/**
	 * Create a deep copy of {@code array}.
	 * 
	 * The result is a new {@link List} where all collection like objects are deep-copied, all leaf objects are directly
	 * referenced.
	 * 
	 * @param array
	 * @return A deep copy of {@code array}
	 */
	@SuppressWarnings("java:S1168")
	public static Object[] copyDeep(Object[] array) {
		if (array == null) {
			return null;
		}
		Object[] result = new Object[array.length];
		int i = 0;
		for (Object value : array) {
			result[i++] = LangTools.copyDeep(value);
		}
		return result;
	}

	public static <A> A[] createFromList(Class<A> objectClass, List<A> list) {
		A[] array = (A[]) Array.newInstance(objectClass, list.size());
		int index = 0;
		for (Iterator<A> i = list.iterator(); i.hasNext(); index++) {
			Object element = i.next();
			Array.set(array, index, element);
		}
		return array;
	}

	public static <A> A detect(A[] list, IBaseFunctor<Boolean> selector) {
		for (A a : list) {
			try {
				if (Boolean.TRUE.equals(selector.perform(a))) {
					return a;
				}
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	public static <T> T first(T[] array) {
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return null;
		}
		return array[0];
	}

	public static <T> T last(T[] array) {
		if (array == null) {
			return null;
		}
		if (array.length == 0) {
			return null;
		}
		return array[array.length - 1];
	}

	public static <A> A[] reject(A[] array, IBaseFunctor<Boolean> selector, Class<A> clazz) {
		List<A> result = new ArrayList<>();
		for (A a : array) {
			try {
				if (!Boolean.TRUE.equals(selector.perform(a))) {
					result.add(a);
				}
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result.toArray((A[]) Array.newInstance(clazz, result.size()));
	}

	public static void reverse(byte[] array) {
		reverse(array, 0, array.length);
	}

	public static void reverse(byte[] array, int index, int length) {
		byte temp;
		for (int i = 0; i < length / 2; i++) {
			temp = array[index + i];
			array[index + i] = array[index + length - i - 1];
			array[index + length - i - 1] = temp;
		}
	}

	public static <T> void reverse(T[] array) {
		reverse(array, 0, array.length);
	}

	public static <T> void reverse(T[] array, int index, int length) {
		T temp;
		for (int i = 0; i < length / 2; i++) {
			temp = array[index + i];
			array[index + i] = array[index + length - i - 1];
			array[index + length - i - 1] = temp;
		}
	}

	public static <A> A[] select(A[] array, IBaseFunctor<Boolean> selector, Class<A> clazz) {
		List<A> result = new ArrayList<>();
		for (A a : array) {
			try {
				if (Boolean.TRUE.equals(selector.perform(a))) {
					result.add(a);
				}
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result.toArray((A[]) Array.newInstance(clazz, result.size()));
	}

	public static <T> T[] toArray(Class<T> clazz, Collection collection) {
		T[] array = (T[]) Array.newInstance(clazz, collection.size());
		return (T[]) collection.toArray(array);
	}

	private ArrayTools() {
	}
}
