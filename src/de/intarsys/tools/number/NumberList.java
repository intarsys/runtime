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
package de.intarsys.tools.number;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.intarsys.tools.collection.NestedIterator;

/**
 * An implementation of NumberWrapper that represents an enumeration of single
 * number objects.
 * 
 */
public class NumberList extends NumberWrapper {

	public static char SEPARATOR = ';';

	public static char SEPARATOR_ALT = ',';

	private List list = new ArrayList();

	/**
	 * NumberList constructor comment.
	 */
	public NumberList() {
		super();
	}

	public boolean add(Object arg0) {
		if (arg0 instanceof NumberWrapper) {
			getList().add(arg0);
			return true;
		} else if (arg0 instanceof Number) {
			getList().add(new NumberInstance((Number) arg0));
			return true;
		}
		return false;
	}

	public boolean addAll(Collection arg0) {
		Iterator iter = arg0.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!add(element)) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(Object o) {
		Iterator iter = getList().iterator();
		while (iter.hasNext()) {
			NumberWrapper element = (NumberWrapper) iter.next();
			if (element.contains(o)) {
				return true;
			}
		}
		return false;
	}

	public java.util.List getList() {
		return list;
	}

	@Override
	protected double getMax() {
		double max = Double.MIN_VALUE;
		Iterator iter = getList().iterator();
		while (iter.hasNext()) {
			NumberWrapper wrapper = (NumberWrapper) iter.next();
			if (wrapper.getMax() < max) {
				max = wrapper.getMax();
			}
		}
		return max;
	}

	@Override
	protected double getMin() {
		double min = Double.MAX_VALUE;
		Iterator iter = getList().iterator();
		while (iter.hasNext()) {
			NumberWrapper wrapper = (NumberWrapper) iter.next();
			if (wrapper.getMin() < min) {
				min = wrapper.getMin();
			}
		}
		return min;
	}

	@Override
	public NumberWrapper increment(int i) {
		for (Iterator it = getList().iterator(); it.hasNext();) {
			NumberWrapper wrapper = (NumberWrapper) it.next();
			wrapper.increment(i);
		}
		return this;
	}

	public boolean isEmpty() {
		if (getList().isEmpty()) {
			return true;
		}
		Iterator iter = getList().iterator();
		while (iter.hasNext()) {
			NumberWrapper element = (NumberWrapper) iter.next();
			if (!element.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public java.util.Iterator iterator() {
		return new NestedIterator(getList().iterator());
	}

	public void setList(java.util.List newList) {
		list = newList;
	}

	public int size() {
		int size = 0;
		Iterator iter = getList().iterator();
		while (iter.hasNext()) {
			NumberWrapper element = (NumberWrapper) iter.next();
			size = size + element.size();
		}
		return size;
	}

	public Object[] toArray() {
		Object[] numbers = new Object[size()];
		Iterator iter = getList().iterator();
		int i = 0;
		while (iter.hasNext()) {
			NumberWrapper element = (NumberWrapper) iter.next();
			Object[] elementArray = element.toArray();
			for (int j = 0; j < elementArray.length; j++) {
				numbers[i] = elementArray[j];
				i++;
			}
		}

		return numbers;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator i = getList().iterator(); i.hasNext();) {
			sb.append(i.next().toString());
			if (i.hasNext()) {
				sb.append(SEPARATOR);
			}
		}
		return sb.toString();
	}
}
