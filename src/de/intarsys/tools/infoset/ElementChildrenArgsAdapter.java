/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.infoset;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

/**
 * Adapt an {@link IElement} children collection to {@link IArgs}.
 * 
 * This is always an indexed enumeration of all child {@link IElement} instances
 * with the same name within an {@link IElement} container.
 * 
 */
public class ElementChildrenArgsAdapter implements IArgs {

	protected class Binding implements IBinding {

		private IElement element;

		public Binding(IElement element) {
			super();
			this.element = element;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public Object getValue() {
			return new ElementArgsAdapter(element);
		}

		@Override
		public boolean isDefined() {
			return false;
		}

		@Override
		public void setName(String name) {
		}

		@Override
		public void setValue(Object value) {
			throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
		}

	}

	final private IElement element;

	final private String name;

	public ElementChildrenArgsAdapter(IElement element, String name) {
		super();
		this.element = element;
		this.name = name;
	}

	public IBinding add(Object object) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	@Override
	public Iterator<IBinding> bindings() {
		return new Iterator<IBinding>() {
			private Iterator<IElement> it = element.elementIterator(name);

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public IBinding next() {
				return new Binding(it.next());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("can't remove");
			}
		};
	}

	public void clear() {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	@Override
	public IArgs copy() {
		return new ElementChildrenArgsAdapter(element, name);
	}

	@Override
	public IBinding declare(final String name) {
		return new Binding(null);
	}

	public Object get(int index) {
		if (index < 0) {
			return null;
		}
		Iterator<IElement> it = element.elementIterator(name);
		int i = -1;
		IElement element = null;
		while (it.hasNext() && i < index) {
			i++;
			element = it.next();
		}
		if (i == index) {
			return new ElementArgsAdapter(element);
		}
		return null;
	}

	public Object get(int index, Object defaultValue) {
		if (index < 0) {
			return defaultValue;
		}
		Iterator<IElement> it = element.elementIterator(name);
		int i = -1;
		IElement element = null;
		while (it.hasNext() && i < index) {
			i++;
			element = it.next();
		}
		if (i == index) {
			return new ElementArgsAdapter(element);
		}
		return defaultValue;
	}

	public Object get(String childName) {
		IElement child = element.element(name);
		if (child == null) {
			return null;
		}
		return new ElementArgsAdapter(child).get(childName);
	}

	public Object get(String childName, Object defaultValue) {
		IElement child = element.element(name);
		if (child == null) {
			return defaultValue;
		}
		return new ElementArgsAdapter(child).get(childName, defaultValue);
	}

	public boolean isDefined(int index) {
		if (index < 0) {
			return false;
		}
		Iterator<IElement> it = element.elementIterator(name);
		int i = -1;
		IElement element = null;
		while (it.hasNext() && i < index) {
			i++;
			element = it.next();
		}
		if (i == index) {
			return true;
		}
		return false;
	}

	public boolean isDefined(String name) {
		// not supported
		return false;
	}

	public Set names() {
		// not supported
		return Collections.EMPTY_SET;
	}

	public IBinding put(int index, Object value) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	public IBinding put(String name, Object value) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	public int size() {
		Iterator<IElement> it = element.elementIterator(name);
		int i = 0;
		while (it.hasNext()) {
			i++;
			it.next();
		}
		return i;
	}

	@Override
	public String toString() {
		return ArgTools.toString(this, ""); //$NON-NLS-1$
	}

	public void undefine(int index) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	public void undefine(String name) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

}
