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
package de.intarsys.tools.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.component.ISynchronizable;

/**
 * A very simple cache implementation. The cache supports "null" entries.
 * 
 * <p>
 * The cache strategy depends on the samples taken in the CacheEntry.
 * </p>
 */
public class Cache<T> implements ISynchronizable {

	/** Map for associative access to elements */
	private final Map<Object, CacheEntry<T>> map = new HashMap<Object, CacheEntry<T>>();

	/** Collection for sorted access according to cache strategy */
	private final CacheEntry<T>[] elements;

	/** counter for currently available cache elements */
	private int ptr = 0;

	/**
	 * Create a cache with a maximum size of size elements.
	 * 
	 * @param size
	 *            The maximum number of elements held in the cache.
	 */
	public Cache(int size) {
		super();
		elements = new CacheEntry[size];
	}

	/**
	 * Clear all entries in the cache.
	 * 
	 */
	synchronized public void clear() {
		map.clear();
		for (int i = 0; i < ptr; i++) {
			elements[i] = null;
		}
		ptr = 0;
	}

	/**
	 * The object with the key "key" or null.
	 * 
	 * @param key
	 *            The key to be used for looking up the cache.
	 * 
	 * @return The object with the key "key" or null.
	 */
	synchronized public T get(Object key) {
		CacheEntry<T> result = map.get(key);
		if (result != null) {
			if (result.getValue() instanceof ISynchronizable) {
				ISynchronizable synch = (ISynchronizable) result.getValue();
				if (synch.isOutOfSynch()) {
					remove(key);
					result = null;
				}
			}
		}
		if (result != null) {
			result.touch();
			return result.getValue();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#isOutOfSynch()
	 */
	public boolean isOutOfSynch() {
		return false;
	}

	/**
	 * Store the object "value" with the key "key" in the cache.
	 * 
	 * @param key
	 *            The key to use for storing the object
	 * @param value
	 *            The value to put in the cache.
	 */
	synchronized public void put(Object key, T value) {
		CacheEntry<T> entry = new CacheEntry<T>(key, value);
		if (ptr >= elements.length) {
			removeStrategy();
		}
		elements[ptr++] = entry;
		map.put(key, entry);
	}

	/**
	 * Remove an object from the cache.
	 * 
	 * @param key
	 */
	synchronized public void remove(Object key) {
		CacheEntry<T> entry = map.remove(key);
		for (int i = 0; i < ptr; i++) {
			CacheEntry<T> current = elements[i];
			if (current == entry) {
				ptr--;
				if (i < ptr) {
					System.arraycopy(elements, i + 1, elements, i, ptr - i);
				}
				elements[ptr] = null;
			}
		}
	}

	/**
	 * Perform the "cleanup" of the cache. The "least valuable" cache entry is
	 * removed.
	 * 
	 */
	protected void removeStrategy() {
		Arrays.sort(elements, 0, ptr);
		--ptr;
		CacheEntry<T> entry = elements[ptr];
		map.remove(entry.getKey());
		elements[ptr] = null;
	}

	/**
	 * The actual size of the cache.
	 * 
	 * @return The actual size of the cache.
	 */
	synchronized public int size() {
		return ptr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISynchronizable#synch()
	 */
	synchronized public void synch() {
		for (int i = 0; i < ptr; i++) {
			CacheEntry<T> element = elements[i];
			if (element.getValue() instanceof ISynchronizable) {
				((ISynchronizable) element.getValue()).synch();
			}
		}
	}
}
