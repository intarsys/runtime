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

import java.util.Collection;
import java.util.Iterator;

import de.intarsys.tools.collection.IntervalIterator;

/**
 * An implementation of NumberWrapper that represents an interval of numbers.
 * 
 */
public class NumberInterval extends NumberWrapper {
	public static char SEPARATOR = '-';

	private Number from;

	private Number to;

	private double step = 1;

	/**
	 * NumberInterval constructor comment.
	 */
	public NumberInterval() {
		super();
	}

	public boolean add(Object arg0) {
		if (arg0 instanceof Number) {
			Number n = (Number) arg0;
			if (n.doubleValue() < from.doubleValue()) {
				from = new Double(n.doubleValue());
			} else if (n.doubleValue() > to.doubleValue()) {
				to = new Double(n.doubleValue());
			}
			return true;
		} else if (arg0 instanceof NumberWrapper) {
			NumberWrapper wrapper = (NumberWrapper) arg0;
			if (wrapper.getMin() < this.getMin()) {
				from = new Double(wrapper.getMin());
			}
			if (wrapper.getMax() > this.getMax()) {
				to = new Double(wrapper.getMax());
			}
		}

		throw new IllegalArgumentException(
				"Only objects of type Number or NumberWrapper are supported.");
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
		if (o instanceof Number) {
			Number n = (Number) o;
			return (n.doubleValue() >= from.doubleValue())
					&& (n.doubleValue() <= to.doubleValue());
		} else if (o instanceof NumberWrapper) {
			NumberWrapper wrapper = (NumberWrapper) o;
			return (wrapper.getMin() >= this.getMin())
					&& (wrapper.getMax() <= this.getMax());
		}
		return false;
	}

	public java.lang.Number getFrom() {
		return from;
	}

	@Override
	protected double getMax() {
		return to.doubleValue();
	}

	@Override
	protected double getMin() {
		return from.doubleValue();
	}

	public java.lang.Number getTo() {
		return to;
	}

	@Override
	public NumberWrapper increment(int i) {
		from = new Integer(getFrom().intValue() + i);
		to = new Integer(getTo().intValue() + i);
		return this;
	}

	public boolean isEmpty() {
		if ((to == null) || (from == null)) {
			return true;
		}
		return from.intValue() > to.intValue();
	}

	public java.util.Iterator iterator() {
		return new IntervalIterator(from, to);
	}

	public void setFrom(java.lang.Number newFrom) {
		from = newFrom;
	}

	public void setTo(java.lang.Number newTo) {
		to = newTo;
	}

	public int size() {
		return (int) Math.round((to.doubleValue() - from.doubleValue()) / step) + 1;
	}

	public Object[] toArray() {
		Number[] numbers = new Number[size()];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = new Double(from.doubleValue() + (i * step));
		}
		return numbers;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getFrom());
		sb.append(SEPARATOR);
		sb.append(getTo());
		return sb.toString();
	}
}
