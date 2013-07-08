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
package de.intarsys.tools.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implement an Iterator over a virtual collection of numbers defined by an
 * interval.
 * 
 * <p>
 * The iterator returns every number with (from &gt.= number) and (number &lt.=
 * to) starting with <code>from</code>. The current number is incremented by
 * <code>step</code> after every element access.
 * </p>
 */
public class IntervalIterator implements Iterator {
	//
	private double current;

	private double from;

	private double step = 1;

	private double to;

	/**
	 * IntervalIterator constructor .
	 * 
	 * @param from
	 *            The first number to be returned by the iterator.
	 * @param to
	 *            The number defining an upper limit to the numbers in the
	 *            collection.
	 */
	public IntervalIterator(Number from, Number to) {
		super();
		this.from = from.doubleValue();
		this.to = to.doubleValue();
		current = this.from;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (current > to) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public java.lang.Object next() {
		Number activeElement;
		if (!hasNext()) {
			throw new NoSuchElementException("no more elements");
		}
		activeElement = new Double(current);
		current = current + step;
		return activeElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException(
				"IntervalIterator not modifiable");
	}
}
