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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Wraps an enumeration into an Iterator API.
 * 
 */
public class EnumerationIterator<T> implements Iterator<T> {
	private Enumeration enumeration;

	/**
	 * Create an Iterator over an enumeration.
	 * 
	 * @param e
	 *            The enumeration to be iterated.
	 */
	public EnumerationIterator(Enumeration e) {
		super();
		setEnumeration(e);
	}

	protected java.util.Enumeration getEnumeration() {
		return enumeration;
	}

	/**
	 * Return <code>true</code> if underlying enumeration still has elements.
	 * 
	 * @return <code>true</code> if underlying enumeration still has elements.
	 */
	@Override
	public boolean hasNext() {
		if (getEnumeration() == null) {
			return false;
		}
		return getEnumeration().hasMoreElements();
	}

	/**
	 * Return the next element from the underlying enumeration.
	 * 
	 * @return the next element from the underlying enumeration.
	 * 
	 * @throws NoSuchElementException
	 */
	@Override
	public T next() {
		if (getEnumeration() == null) {
			throw new NoSuchElementException("enumeration not available");
		}
		return (T) getEnumeration().nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void setEnumeration(java.util.Enumeration newEnumeration) {
		enumeration = newEnumeration;
	}
}
