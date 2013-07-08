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
package de.intarsys.tools.enumeration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The meta data for an enumeration implementation. This is a completely generic
 * implementation - you should only subclass "EnumItem" to define a new
 * enumeration.
 * 
 */
public class EnumMeta<T extends EnumItem> {

	final private Class enumClazz;

	final private List<T> items = new ArrayList<T>();

	private T enumDefault = null;

	/**
	 * 
	 */
	public EnumMeta(Class enumClazz) {
		super();
		this.enumClazz = enumClazz;
	}

	protected void addItem(T item) {
		items.add(item);
		if (items.size() == 1) {
			item.setDefault();
		}
	}

	public T getDefault() {
		return enumDefault;
	}

	public Class getEnumClazz() {
		return enumClazz;
	}

	public T getItem(String id) {
		if (id == null) {
			return null;
		}
		for (T element : items) {
			if (element.getId().equals(id)) {
				return element;
			}
		}
		return null;
	}

	public T getItemOrDefault(String id) {
		T item = getItem(id);
		if (item == null) {
			item = getDefault();
		}
		return item;
	}

	public T[] getItems() {
		return items.toArray((T[]) Array.newInstance(enumClazz, items.size()));
	}

	public T getMax() {
		T max = null;
		for (T element : items) {
			if ((max == null) || (element.getWeight() > max.getWeight())) {
				max = element;
			}
		}
		return max;
	}

	public T getMin() {
		T min = null;
		for (T element : items) {
			if ((min == null) || (element.getWeight() < min.getWeight())) {
				min = element;
			}
		}
		return min;
	}

	protected void setDefault(T item) {
		enumDefault = item;
	}

	/**
	 * Sort the items in ascending label order.
	 */
	public void sort() {
		sort(new Comparator<EnumItem>() {
			public int compare(EnumItem o1, EnumItem o2) {
				return o1.getLabel().compareToIgnoreCase(o2.getLabel());
			};
		});
	}

	/**
	 * Sort the items using the given Comparator.
	 * 
	 * @param c
	 */
	public void sort(Comparator<EnumItem> c) {
		Collections.sort(items, c);
	}
}
