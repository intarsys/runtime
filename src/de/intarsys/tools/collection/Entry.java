package de.intarsys.tools.collection;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V> {

	private K key;

	private V value;

	public Entry(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}

}
