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

import java.util.Iterator;

import de.intarsys.tools.collection.SingleObjectIterator;

/**
 * An implementation of NumberWrapper that represents a single number object.
 * 
 */
public class NumberInstance extends NumberWrapper {
	private Number n;

	/**
	 * NumberInstance constructor for double.
	 * 
	 * @param i
	 *            A primitive double to be wrapped
	 */
	public NumberInstance(double i) {
		super();
		n = Double.valueOf(i);
	}

	/**
	 * NumberInstance constructor for int.
	 * 
	 * @param i
	 *            A primitive int tobe wrapped
	 */
	public NumberInstance(int i) {
		super();
		n = Integer.valueOf(i);
	}

	/**
	 * NumberInstance constructor for {@link Number}.
	 * 
	 * @param number
	 *            A {@link Number} instance to be wrapped
	 */
	public NumberInstance(Number number) {
		super();
		n = number;
	}

	/**
	 * NumberInstance constructor for integer value formatted in a String.
	 * 
	 * @param numberValueInString
	 *            The string containing the integer value.
	 */
	public NumberInstance(String numberValueInString) {
		super();
		int zahl = Integer.parseInt(numberValueInString);
		n = Integer.valueOf(zahl);
	}

	public boolean add(Object arg0) { // NOSONAR
		if (arg0 instanceof Number) {
			n = (Number) arg0;
			return true; // NOSONAR
		} else if (arg0 instanceof NumberWrapper && !((NumberWrapper) arg0).isEmpty()) {
			n = ((NumberWrapper) arg0).getFirst();
			return true; // NOSONAR
		}
		throw new IllegalArgumentException("Only objects of type Number or NumberWrapper are supported.");
	}

	public boolean contains(Object o) {
		if (o instanceof Number) {
			return ((Number) o).doubleValue() == n.doubleValue();
		} else if (o instanceof NumberWrapper && !((NumberWrapper) o).isEmpty()) {
			return ((NumberWrapper) o).getFirst().doubleValue() == n.doubleValue();
		}
		return false;
	}

	@Override
	public double getMax() {
		return n.doubleValue();
	}

	@Override
	public double getMin() {
		return n.doubleValue();
	}

	public java.lang.Number getN() {
		return n;
	}

	@Override
	public NumberWrapper increment(int i) {
		n = Integer.valueOf(getN().intValue() + i);
		return this;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Iterator iterator() {
		return new SingleObjectIterator(getN());
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public String toString() {
		return java.text.NumberFormat.getInstance().format(getN());
	}
}
