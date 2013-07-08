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
import java.util.NoSuchElementException;

/**
 * An enumeration of a single object.
 * 
 */
public class SingleObjectEnumeration<T> implements Enumeration<T> {

	private T singleObject;

	private boolean hasNext = true;

	/**
	 * EnumerationIterator constructor comment.
	 * 
	 * @param single
	 * 
	 */
	public SingleObjectEnumeration(T single) {
		super();
		setSingleObject(single);
	}

	private T getSingleObject() {
		return singleObject;
	}

	public boolean hasMoreElements() {
		return hasNext;
	}

	public T nextElement() {
		if (hasMoreElements()) {
			hasNext = false;
			return getSingleObject();
		}
		throw new NoSuchElementException();
	}

	/**
	 * Removes from the underlying collection the last element returned by the
	 * iterator (optional operation). This method can be called only once per
	 * call to <tt>next</tt>. The behavior of an iterator is unspecified if
	 * the underlying collection is modified while the iteration is in progress
	 * in any way other than by calling this method.
	 * 
	 * @exception UnsupportedOperationException
	 *                if the <tt>remove</tt> operation is not supported by
	 *                this Iterator.
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void setSingleObject(T newSingleObject) {
		singleObject = newSingleObject;
	}
}
