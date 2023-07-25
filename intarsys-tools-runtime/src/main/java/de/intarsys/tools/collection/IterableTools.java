package de.intarsys.tools.collection;

import java.util.Iterator;

public class IterableTools {

	/**
	 * Wraps an iterator in an Iterable interface, so it can be used in
	 * simplified for-loops.
	 * 
	 * @param iterator
	 * @return the wrapped iterator
	 */
	public static <T> Iterable<T> in(final Iterator<T> iterator) {
		return new Iterable<T>() {

			private boolean called;

			@Override
			public Iterator<T> iterator() {
				if (!called) {
					called = true;
					return iterator;
				} else {
					throw new IllegalArgumentException("iterator() can only be called once!"); //$NON-NLS-1$
				}
			}

		};
	}

	private IterableTools() {
		//
	}

}
