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
import java.util.Set;

/**
 * An abstract class for the implementation of objects that may occur in the
 * definition of a number string.
 * 
 */
public abstract class NumberWrapper implements Iterable, Set {
	/**
	 * NumberWrapper constructor comment.
	 */
	public NumberWrapper() {
		super();
	}

	public void clear() {
		throw new UnsupportedOperationException(
				"Clear operation not supported!");
	}

	public boolean containsAll(Collection arg0) {
		Iterator iter = arg0.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (!contains(element)) {
				return false;
			}
		}
		return true;
	}

	protected Number getFirst() {
		if (!isEmpty()) {
			return (Number) iterator().next();
		}
		return null;
	}

	protected abstract double getMax();

	protected abstract double getMin();

	abstract public NumberWrapper increment(int i);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		throw new UnsupportedOperationException(
				"Remove operation not supported!");
	}

	public boolean removeAll(Collection arg0) {
		throw new UnsupportedOperationException(
				"RemoveAll operation not supported!");
	}

	public boolean retainAll(Collection arg0) {
		throw new UnsupportedOperationException(
				"RetainAll operation not supported!");
	}

	public Object[] toArray(Object[] arg0) {
		return toArray();
	}

}
