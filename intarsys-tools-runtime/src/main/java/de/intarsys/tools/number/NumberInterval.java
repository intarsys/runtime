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
package de.intarsys.tools.number;

import de.intarsys.tools.collection.IntervalIterator;

/**
 * An implementation of NumberWrapper that represents an interval of numbers.
 * 
 */
public class NumberInterval extends NumberWrapper {

	public static final char SEPARATOR = '-';

	private Number from;

	private Number to;

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
				from = Double.valueOf(n.doubleValue());
			} else if (n.doubleValue() > to.doubleValue()) {
				to = Double.valueOf(n.doubleValue());
			}
			return true;
		} else if (arg0 instanceof NumberWrapper) {
			NumberWrapper wrapper = (NumberWrapper) arg0;
			if (wrapper.getMin() < this.getMin()) {
				from = Double.valueOf(wrapper.getMin());
			}
			if (wrapper.getMax() > this.getMax()) {
				to = Double.valueOf(wrapper.getMax());
			}
		}

		throw new IllegalArgumentException("Only objects of type Number or NumberWrapper are supported.");
	}

	public java.lang.Number getFrom() {
		return from;
	}

	@Override
	public double getMax() {
		return to.doubleValue();
	}

	@Override
	public double getMin() {
		return from.doubleValue();
	}

	public java.lang.Number getTo() {
		return to;
	}

	@Override
	public NumberWrapper increment(int i) {
		from = Integer.valueOf(getFrom().intValue() + i);
		to = Integer.valueOf(getTo().intValue() + i);
		return this;
	}

	@Override
	public boolean isEmpty() {
		if ((to == null) || (from == null)) {
			return true;
		}
		return from.intValue() > to.intValue();
	}

	@Override
	public java.util.Iterator iterator() {
		return new IntervalIterator(from, to);
	}

	public void setFrom(java.lang.Number newFrom) {
		from = newFrom;
	}

	public void setTo(java.lang.Number newTo) {
		to = newTo;
	}

	@Override
	public int size() {
		return (int) Math.round((to.doubleValue() - from.doubleValue())) + 1;
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
