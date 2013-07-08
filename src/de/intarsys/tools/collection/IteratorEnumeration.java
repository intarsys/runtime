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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps an iterator into an enumeration API.
 * 
 */
public class IteratorEnumeration<T> implements Enumeration<T> {
	private Iterator<T> iterator;

	/**
	 * IteratorEnumeration constructor.
	 * 
	 * @param i
	 *            The Iterator to be enumerated
	 */
	public IteratorEnumeration(Iterator<T> i) {
		super();
		setIterator(i);
	}

	protected Iterator<T> getIterator() {
		return iterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements() {
		if (getIterator() == null) {
			return false;
		}
		return getIterator().hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#nextElement()
	 */
	public T nextElement() {
		if (getIterator() == null) {
			throw new NoSuchElementException("iterator not available");
		}
		return getIterator().next();
	}

	private void setIterator(Iterator<T> newIterator) {
		iterator = newIterator;
	}
}
