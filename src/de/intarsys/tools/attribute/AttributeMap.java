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
package de.intarsys.tools.attribute;

import java.util.Map;

/**
 * A default implementation for {@link IAttributeSupport}. The API is "doubled"
 * to be usable as a simple replacement for a {@link Map}.
 * <p>
 * <code>null</code> is not a legal key.
 */
final public class AttributeMap implements IAttributeSupport {

	private Object[] keys;
	private Object[] values;
	private int length = 0;

	public AttributeMap() {
		this(4);
	}

	public AttributeMap(int initialCapacity) {
		keys = new Object[initialCapacity];
		values = new Object[initialCapacity];
	}

	synchronized public void clear() {
		for (int i = 0; i < length; i++) {
			values[i] = null;
			keys[i] = null;
		}
		length = 0;
	}

	synchronized public AttributeMap copy() {
		AttributeMap copy = new AttributeMap(length);
		for (int i = 0; i < length; i++) {
			copy.values[i] = values[i];
			copy.keys[i] = keys[i];
		}
		copy.length = length;
		return copy;
	}

	public Object get(Object key) {
		return getAttribute(key);
	}

	synchronized public Object getAttribute(Object key) {
		for (int i = 0; i < length; i++) {
			if (keys[i].equals(key)) {
				return values[i];
			}
		}
		return null;
	}

	/**
	 * The keys used in this attribute lookup map.
	 * <p>
	 * !! This is not intended to be published to client code, as this would
	 * compromise {@link IAttributeSupport} security !!
	 * 
	 * @return The keys used in this attribute lookup map.
	 */
	public Object[] getKeys() {
		Object[] tempKeys = new Object[length];
		System.arraycopy(keys, 0, tempKeys, 0, length);
		return tempKeys;
	}

	public Object put(Object key, Object o) {
		return setAttribute(key, o);
	}

	public Object remove(Object key) {
		return removeAttribute(key);
	}

	synchronized public Object removeAttribute(Object key) {
		for (int i = 0; i < length; i++) {
			if (keys[i].equals(key)) {
				Object oldValue = values[i];
				length--;
				System.arraycopy(keys, i + 1, keys, i, length - i);
				System.arraycopy(values, i + 1, values, i, length - i);
				values[length] = null;
				keys[length] = null;
				return oldValue;
			}
		}
		return null;
	}

	synchronized public Object setAttribute(Object key, Object value) {
		int i = 0;
		// replace existing
		while (i < length) {
			if (keys[i].equals(key)) {
				Object oldValue = values[i];
				values[i] = value;
				return oldValue;
			}
			i++;
		}
		if (i >= values.length) {
			// expand
			Object[] newKeys = new Object[length + 4];
			System.arraycopy(keys, 0, newKeys, 0, length);
			keys = newKeys;
			Object[] newValues = new Object[length + 4];
			System.arraycopy(values, 0, newValues, 0, length);
			values = newValues;
		}
		// add new
		values[length] = value;
		keys[length] = key;
		length++;
		return null;
	}
}
