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

/**
 * An entry in the cache implementation.
 * 
 * <p>
 * A cache entries "importance" is expressed by a "sample". The CacheEntry with
 * the smallest sample is considered the least valuable.
 * </p>
 * 
 * <p>
 * This implementation supports a simple "most recently used" strategy.
 * </p>
 */
public class CacheEntry<T> implements Comparable {
	/** remember the last used sample to ensure no two samples are the same */
	private static int LASTSAMPLE = 0;

	/** The key of the cache entry */
	private final Object key;

	/** The wrapped value of the entry */
	private final T value;

	/** The sample representing the "importance" of the entry */
	private int sample;

	/**
	 * Create a cache entry.
	 * 
	 * @param key
	 *            The key for the entry
	 * @param value
	 *            The value for the entry
	 */
	protected CacheEntry(Object key, T value) {
		super();
		this.key = key;
		this.value = value;
		touch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		CacheEntry other = (CacheEntry) o;
		if (getSample() == other.getSample()) {
			return 0;
		}
		if (getSample() < other.getSample()) {
			return 1;
		}
		return -1;
	}

	/**
	 * The key of the cache entry.
	 * 
	 * @return Returns the key.
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * The sample representing the "importance" of the entry.
	 * 
	 * @return Returns the sample.
	 */
	public int getSample() {
		return sample;
	}

	/**
	 * The value of the cache entry.
	 * 
	 * @return Returns the value.
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Mark the Cache entry as recently used.
	 */
	synchronized protected void touch() {
		sample = LASTSAMPLE++;
	}
}
