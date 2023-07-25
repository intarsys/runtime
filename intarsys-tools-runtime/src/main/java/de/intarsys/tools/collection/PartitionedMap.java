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

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A map that wraps two other maps.
 * 
 */
public class PartitionedMap implements Map {
	private class PartitionIterator implements Iterator {
		Iterator parentIterator = PartitionedMap.this.getParent().entrySet().iterator();

		Iterator subIterator = PartitionedMap.this.getChild().entrySet().iterator();

		int type;

		PartitionIterator(int type) {
			this.type = type;
		}

		@Override
		public boolean hasNext() {
			return parentIterator.hasNext() || subIterator.hasNext();
		}

		@Override
		public Object next() {
			Map.Entry e = null;
			if (parentIterator.hasNext()) {
				e = (Map.Entry) parentIterator.next();
			} else {
				e = (Map.Entry) subIterator.next();
			}
			if (e != null) {
				if (type == KEYS) {
					return e.getKey();
				}
				if (type == VALUES) {
					return e.getValue();
				}
				return e;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Partitioned Maps do not support this");
		}
	}

	// Types of Iterators
	private static final int KEYS = 0;

	private static final int VALUES = 1;

	private static final int ENTRIES = 2;

	public static Map create(Map parent, Map child) {
		if (parent == null) {
			return child;
		}
		if (child == null) {
			return parent;
		}
		return new PartitionedMap(parent, child);
	}

	private Map child;

	private Map parent;

	public PartitionedMap() {
		this(null, null);
	}

	public PartitionedMap(Map parent, Map child) {
		super();
		setChild((child == null) ? new HashMap() : child);
		setParent((parent == null) ? new HashMap() : parent);
	}

	/**
	 * Removes all mappings from this map (optional operation).
	 */
	@Override
	public void clear() {
		getChild().clear();
	}

	/**
	 * Returns {@code true} if this map contains a mapping for the specified key.
	 * 
	 * @param key key whose presence in this map is to be tested.
	 * 
	 * @return {@code true} if this map contains a mapping for the specified key.
	 */
	@Override
	public boolean containsKey(java.lang.Object key) {
		return getChild().containsKey(key) || (getParent() != null && getParent().containsKey(key));
	}

	/**
	 * Returns {@code true} if this map maps one or more keys to the specified
	 * value. More formally, returns {@code true} if and only if this map contains
	 * at least one mapping to a value {@code v} such that
	 * {@code (value==null ? v==null : value.equals(v))}. This operation will
	 * probably require time linear in the map size for most implementations of the
	 * {@code Map} interface.
	 * 
	 * @param value value whose presence in this map is to be tested.
	 * 
	 * @return {@code true} if this map maps one or more keys to the specified
	 *         value.
	 */
	@Override
	public boolean containsValue(java.lang.Object value) {
		return getChild().containsValue(value) || (getParent() != null && getParent().containsValue(value));
	}

	/**
	 * Returns a set view of the mappings contained in this map. Each element in the
	 * returned set is a {@code Map.Entry}. The set is backed by the map, so changes
	 * to the map are reflected in the set, and vice-versa. If the map is modified
	 * while an iteration over the set is in progress, the results of the iteration
	 * are undefined. The set supports element removal, which removes the
	 * corresponding mapping from the map, via the {@code Iterator.remove},
	 * {@code Set.remove}, {@code removeAll}, {@code retainAll} and {@code clear}
	 * operations. It does not support the {@code add} or {@code addAll} operations.
	 * 
	 * @return a set view of the mappings contained in this map.
	 */
	@Override
	public java.util.Set entrySet() {
		Set set = new AbstractSet() {
			@Override
			public void clear() {
				getChild().clear();
			}

			@Override
			public boolean contains(Object o) {
				// i dont claim this to be efficient....
				return getChild().entrySet().contains(o)
						|| (getParent() != null && getParent().entrySet().contains(o));
			}

			@Override
			public Iterator iterator() {
				return new PartitionIterator(ENTRIES);
			}

			@Override
			public boolean remove(Object o) {
				return getChild().entrySet().remove(o)
						|| (getParent() != null && getParent().entrySet().remove(o));
			}

			@Override
			public int size() {
				return getChild().size() + ((getParent() == null) ? 0 : getParent().size());
			}
		};

		return set;
	}

	/**
	 * Returns the value to which this map maps the specified key. Returns
	 * {@code null} if the map contains no mapping for this key. A return value of
	 * {@code null} does not <i>necessarily</i> indicate that the map contains no
	 * mapping for the key; it's also possible that the map explicitly maps the key
	 * to {@code null}. The {@code containsKey} operation may be used to distinguish
	 * these two cases.
	 * 
	 * @param key key whose associated value is to be returned.
	 * 
	 * @return the value to which this map maps the specified key, or {@code null}
	 *         if the map contains no mapping for this key.
	 * 
	 * @see #containsKey(Object)
	 */
	@Override
	public java.lang.Object get(java.lang.Object key) {
		Object result = getChild().get(key);
		if (result == null) {
			return (getParent() == null) ? null : getParent().get(key);
		}
		return result;
	}

	protected Map getChild() {
		return child;
	}

	public java.util.Map getParent() {
		return parent;
	}

	/**
	 * Returns {@code true} if this map contains no key-value mappings.
	 * 
	 * @return {@code true} if this map contains no key-value mappings.
	 */
	@Override
	public boolean isEmpty() {
		return getChild().isEmpty() && (getParent() == null || getParent().isEmpty());
	}

	/**
	 * Returns a set view of the keys contained in this map. The set is backed by
	 * the map, so changes to the map are reflected in the set, and vice-versa. If
	 * the map is modified while an iteration over the set is in progress, the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * {@code Iterator.remove}, {@code Set.remove},
	 * {@code removeAll}{@code retainAll}, and {@code clear} operations. It does not
	 * support the add or {@code addAll} operations.
	 * 
	 * @return a set view of the keys contained in this map.
	 */
	@Override
	public java.util.Set keySet() {
		Set set = new AbstractSet() {
			@Override
			public void clear() {
				getChild().clear();
			}

			@Override
			public boolean contains(Object o) {
				// i dont claim this is efficient....
				return (getParent() != null && getParent().containsKey(o)) || getChild().containsKey(o);
			}

			@Override
			public Iterator iterator() {
				return new PartitionIterator(KEYS);
			}

			@Override
			public boolean remove(Object o) {
				return (getChild().remove(o) != null)
						|| (getParent() != null && (getParent().remove(o) != null));
			}

			@Override
			public int size() {
				return getChild().size() + ((getParent() == null) ? 0 : getParent().size());
			}
		};

		return set;
	}

	/**
	 * Associates the specified value with the specified key in this map (optional
	 * operation). If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 * 
	 * @param key   key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * 
	 * @return previous value associated with specified key, or {@code null} if
	 *         there was no mapping for key. A {@code null} return can also indicate
	 *         that the map previously associated {@code null} with the specified
	 *         key, if the implementation supports {@code null} values.
	 */
	@Override
	public java.lang.Object put(java.lang.Object key, java.lang.Object value) {
		return getChild().put(key, value);
	}

	/**
	 * Copies all of the mappings from the specified map to this map (optional
	 * operation). These mappings will replace any mappings that this map had
	 * for any of the keys currently in the specified map.
	 * 
	 * @param t
	 *            Mappings to be stored in this map.
	 */
	@Override
	public void putAll(java.util.Map t) {
		Iterator i = t.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry e = (Map.Entry) i.next();
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Removes the mapping for this key from this map if present (optional
	 * operation).
	 * 
	 * @param key key whose mapping is to be removed from the map.
	 * 
	 * @return previous value associated with specified key, or {@code null} if
	 *         there was no mapping for key. A {@code null} return can also indicate
	 *         that the map previously associated {@code null} with the specified
	 *         key, if the implementation supports {@code null} values.
	 */
	@Override
	public java.lang.Object remove(java.lang.Object key) {
		if ((getParent() != null) && getParent().containsKey(key)) {
			return getParent().remove(key);
		}
		return getChild().remove(key);
	}

	private void setChild(java.util.Map newSubMap) {
		child = newSubMap;
	}

	public void setParent(java.util.Map newParent) {
		parent = newParent;
	}

	/**
	 * Returns the number of key-value mappings in this map. If the map contains
	 * more than {@code Integer.MAX_VALUE} elements, returns
	 * {@code Integer.MAX_VALUE}.
	 * 
	 * @return the number of key-value mappings in this map.
	 */
	@Override
	public int size() {
		return ((getParent() == null) ? 0 : getParent().size()) + getChild().size();
	}

	@Override
	public String toString() {
		return ((getParent() == null) ? "" : (getParent().toString() + System.getProperty("line.separator")))
				+ getChild().toString();
	}

	/**
	 * Returns a collection view of the values contained in this map. The collection
	 * is backed by the map, so changes to the map are reflected in the collection,
	 * and vice-versa. If the map is modified while an iteration over the collection
	 * is in progress, the results of the iteration are undefined. The collection
	 * supports element removal, which removes the corresponding mapping from the
	 * map, via the {@code Iterator.remove}, {@code Collection.remove},
	 * {@code removeAll}, {@code retainAll} and {@code clear} operations. It does
	 * not support the add or {@code addAll} operations.
	 * 
	 * @return a collection view of the values contained in this map.
	 */
	@Override
	public java.util.Collection values() {
		Collection c = new AbstractCollection() {
			@Override
			public void clear() {
				getChild().clear();
			}

			@Override
			public boolean contains(Object o) {
				// i dont claim this to be efficient....
				return (getParent() != null && getParent().containsValue(o)) || getChild().containsValue(o);
			}

			@Override
			public Iterator iterator() {
				return new PartitionIterator(VALUES);
			}

			@Override
			public int size() {
				return getChild().size() + ((getParent() == null) ? 0 : getParent().size());
			}
		};

		return c;
	}
}
