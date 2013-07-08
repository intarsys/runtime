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
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterator to enumerate sequentially the elements in a hierarchical
 * collection.
 * 
 */
public class NestedIterator implements Iterator {

	//
	private Iterator innerIterator = null;

	private Object outerCurrent;

	//
	private Iterator outerIterator = null;

	private boolean tryInner = true;

	public NestedIterator(Iterator i) {
		super();
		setOuterIterator(i);
	}

	public NestedIterator(List l) {
		super();
		setOuterIterator(l.iterator());
	}

	protected void createInnerIterator() {
		setInnerIterator(null);
		if (getOuterCurrent() != null) {
			if (getOuterCurrent() instanceof List) {
				setInnerIterator(new NestedIterator(((List) getOuterCurrent())
						.iterator()));
			}
			if (getOuterCurrent() instanceof Iterable) {
				setInnerIterator(new NestedIterator(
						((Iterable) getOuterCurrent()).iterator()));
			}
			if (getOuterCurrent() instanceof Iterator) {
				setInnerIterator(new NestedIterator(
						(Iterator) getOuterCurrent()));
			}
		}
	}

	protected java.util.Iterator getInnerIterator() {
		return innerIterator;
	}

	protected java.lang.Object getOuterCurrent() {
		return outerCurrent;
	}

	protected java.util.Iterator getOuterIterator() {
		return outerIterator;
	}

	public boolean hasNext() {
		if (isTryInner()) {
			if (!getOuterIterator().hasNext()) {
				return false;
			}
			setTryInner(false);
			setOuterCurrent(getOuterIterator().next());
			createInnerIterator();
		}
		if (getInnerIterator() == null) {
			// no nesting
			return true;
		} else {
			if (getInnerIterator().hasNext()) {
				// running on inner
				return true;
			} else {
				// inner has run out, reset to outer
				setTryInner(true);
				return hasNext();
			}
		}
	}

	private boolean isTryInner() {
		return tryInner;
	}

	public java.lang.Object next() {
		if (!hasNext()) {
			throw new NoSuchElementException("no more elements");
		}
		if (getInnerIterator() == null) {
			setTryInner(true);
			return getOuterCurrent();
		} else {
			return getInnerIterator().next();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException("iterator not modifiable");
	}

	private void setInnerIterator(java.util.Iterator newInnerIterator) {
		innerIterator = newInnerIterator;
	}

	private void setOuterCurrent(java.lang.Object newOuterCurrent) {
		outerCurrent = newOuterCurrent;
	}

	private void setOuterIterator(java.util.Iterator newOuterIterator) {
		outerIterator = newOuterIterator;
	}

	private void setTryInner(boolean newTryInner) {
		tryInner = newTryInner;
	}
}
