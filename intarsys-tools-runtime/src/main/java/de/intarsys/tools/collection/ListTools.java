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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.functor.IBaseFunctor;
import de.intarsys.tools.lang.LangTools;

/**
 * Tool class for handling {@link List} instances.
 * 
 */
public class ListTools {

	public static class Builder<E> extends CommonBuilder {

		private List<E> list;

		protected Builder(CommonBuilder parent, List<E> list) {
			super(parent);
			this.list = list;
		}

		public Builder<E> add(E value) {
			list.add(value);
			return this;
		}

		@Override
		public List<E> build() {
			return list;
		}

		public ListTools.Builder startList() {
			return new ListTools.Builder(this, new ArrayList<>()) {
				@Override
				public CommonBuilder end() {
					ListTools.Builder.this.add((E) this.build());
					return super.end();
				}
			};
		}

		public MapTools.Builder startMap() {
			return new MapTools.Builder(this, new HashMap<>()) {
				@Override
				public CommonBuilder end() {
					ListTools.Builder.this.add((E) this.build());
					return super.end();
				}
			};
		}

	}

	public static class ListBinding implements IBinding {
		private final List list;
		private final int index;

		public ListBinding(List list, int index) {
			super();
			this.list = list;
			this.index = index;
		}

		@Override
		public String getName() {
			return "" + index;
		}

		@Override
		public Object getValue() {
			return list.get(index);
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
			list.set(index, value);
		}
	}

	public static <T> List<T> addAll(List<T> list, Enumeration<T> e) {
		while (e.hasMoreElements()) {
			list.add(e.nextElement());
		}
		return list;
	}

	public static <T> List<T> addAll(List<T> list, Iterable<T> iterable) {
		Iterator<T> it = iterable.iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	public static <T> List<T> addAll(List<T> list, Iterator<T> it) {
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	public static <T> List<T> allocate(List<T> list, int count) {
		for (int i = count; i > 0; i--) {
			list.add(null);
		}
		return list;
	}

	/**
	 * Apply the given {@link Function} recursively to all leaf values (non-collection) in
	 * {@code list}. {@code list} is modified to contain the results of the function.
	 * 
	 * @param list
	 * @param function
	 * @return The modified {@code list} object.
	 */
	public static List applyDeep(List list, Function<IBinding, Object> function) {
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext();) {
			LangTools.applyDeep(it.next(), function, new ListBinding(list, i++));
		}
		return list;
	}

	public static <E> Builder<E> builder() {
		return builder(new ArrayList());
	}

	public static <E> Builder<E> builder(List<E> list) {
		return new Builder<>(null, list);
	}

	public static <A, B> List<B> collect(Iterator<A> it, IBaseFunctor<B> converter) {
		List<B> result = new ArrayList<>(5);
		while (it.hasNext()) {
			try {
				result.add(converter.perform(it.next()));
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	public static <A, B> List<B> collect(List<A> list, IBaseFunctor<B> converter) {
		List<B> result = new ArrayList<>(list.size());
		for (A a : list) {
			try {
				result.add(converter.perform(a));
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	/**
	 * Create a deep copy of {@code list}.
	 * 
	 * The result is a new {@link List} where all collection like objects are deep-copied, all leaf objects are directly
	 * referenced.
	 * 
	 * @param list
	 * @return A deep copy of {@code list}
	 */
	public static List copyDeep(List list) {
		if (list == null) {
			return null; // NOSONAR
		}
		ArrayList result = new ArrayList<>(list.size());
		for (Iterator it = list.iterator(); it.hasNext();) {
			result.add(LangTools.copyDeep(it.next()));
		}
		return result;
	}

	public static <A> A detect(List<A> list, IBaseFunctor<Boolean> selector) {
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

	public static <T> T first(List<T> list) {
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public static <T> T last(List<T> list) {
		if (list == null) {
			return null;
		}
		if (list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	public static <A> List<A> reject(List<A> list, IBaseFunctor<Boolean> selector) {
		List<A> result = new ArrayList<>(list.size());
		for (A a : list) {
			try {
				if (!Boolean.TRUE.equals(selector.perform(a))) {
					result.add(a);
				}
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	public static <A> List<A> select(List<A> list, IBaseFunctor<Boolean> selector) {
		List<A> result = new ArrayList<>(list.size());
		for (A a : list) {
			try {
				if (Boolean.TRUE.equals(selector.perform(a))) {
					result.add(a);
				}
			} catch (FunctorException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	public static <T> List<T> toList(Enumeration<T> e) {
		List<T> result = new ArrayList<>(5);
		while (e.hasMoreElements()) {
			result.add(e.nextElement());
		}
		return result;
	}

	public static <T> List<T> toList(Iterable<T> iterable) {
		List<T> result = new ArrayList<>(5);
		Iterator<T> it = iterable.iterator();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	public static <T> List<T> toList(Iterator<T> it) {
		List<T> result = new ArrayList<>(5);
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	public static <T> List<T> with(T... objects) {
		List<T> list = new ArrayList<T>();
		Collections.addAll(list, objects);
		return list;
	}

	private ListTools() {
		// tool class
	}

}
