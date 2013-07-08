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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IBaseFunctor;

/**
 * 
 */
public class ListTools {

	static public <A, B> List<B> collect(Iterator<A> it,
			IBaseFunctor<B> converter) {
		List<B> result = new ArrayList<B>();
		while (it.hasNext()) {
			try {
				result.add(converter.perform(it.next()));
			} catch (FunctorInvocationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	static public <A, B> List<B> collect(List<A> list, IBaseFunctor<B> converter) {
		List<B> result = new ArrayList<B>();
		for (A a : list) {
			try {
				result.add(converter.perform(a));
			} catch (FunctorInvocationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	static public <A> A detect(List<A> list, IBaseFunctor<Boolean> selector) {
		for (A a : list) {
			try {
				if (selector.perform(a)) {
					return a;
				}
			} catch (FunctorInvocationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	static public <T> T first(List<T> list) {
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	static public <T> T last(List<T> list) {
		if (list == null) {
			return null;
		}
		if (list.size() == 0) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	static public <A> List<A> reject(List<A> list,
			IBaseFunctor<Boolean> selector) {
		List<A> result = new ArrayList<A>();
		for (A a : list) {
			try {
				if (!selector.perform(a)) {
					result.add(a);
				}
			} catch (FunctorInvocationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	static public <A> List<A> select(List<A> list,
			IBaseFunctor<Boolean> selector) {
		List<A> result = new ArrayList<A>();
		for (A a : list) {
			try {
				if (selector.perform(a)) {
					result.add(a);
				}
			} catch (FunctorInvocationException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return result;
	}

	static public <T> List<T> toList(Enumeration<T> e) {
		List<T> result = new ArrayList<T>();
		while (e.hasMoreElements()) {
			result.add(e.nextElement());
		}
		return result;
	}

	static public <T> List<T> toList(Iterable<T> iterable) {
		List<T> result = new ArrayList<T>();
		Iterator<T> it = iterable.iterator();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	static public <T> List<T> toList(Iterator<T> it) {
		List<T> result = new ArrayList<T>();
		while (it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}

	static public <T> List<T> with(T... object) {
		List result = new ArrayList();
		for (int i = 0; i < object.length; i++) {
			result.add(object[i]);
		}
		return result;
	}

	private ListTools() {
		// tool class
	}

}
