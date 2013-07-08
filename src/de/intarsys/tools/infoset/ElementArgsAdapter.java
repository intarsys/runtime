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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.intarsys.tools.collection.ConversionIterator;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

/**
 * Adapt an {@link IElement} to {@link IArgs}.
 * 
 * Every {@link IAttribute} is mapped to an argument binding. Every child
 * {@link IElement} is treated as a (possible) collection and as such is mapped
 * to a binding from the element name to an indexed {@link IArgs}. The
 * collection size is one if only a single element exists.
 * 
 * The presence of both an {@link IAttribute} and an {@link IElement} with the
 * same name is undefined.
 * 
 */
public class ElementArgsAdapter implements IArgs {

	protected class Binding implements IBinding {
		private String name;

		public Binding(String name) {
			super();
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getValue() {
			return ElementArgsAdapter.this.get(name);
		}

		@Override
		public boolean isDefined() {
			return ElementArgsAdapter.this.isDefined(name);
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public void setValue(Object value) {
			ElementArgsAdapter.this.put(name, value);
		}

	}

	private final IElement element;

	public ElementArgsAdapter(IElement element) {
		super();
		this.element = element;
	}

	public IBinding add(Object object) {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	@Override
	public Iterator<IBinding> bindings() {
		final Set<String> names = names();
		return new ConversionIterator<String, IArgs.IBinding>(names.iterator()) {
			@Override
			protected IBinding createTargetObject(String sourceObject) {
				return new Binding(sourceObject);
			}
		};
	}

	public void clear() {
		throw new UnsupportedOperationException("can't write"); //$NON-NLS-1$
	}

	@Override
	public IArgs copy() {
		return new ElementArgsAdapter(element);
	}

	@Override
	public IBinding declare(final String name) {
		return new Binding(name);
	}

	public Object get(int index) {
		// not supported
		return null;
	}

	public Object get(int index, Object defaultValue) {
		return defaultValue;
	}

	public Object get(String name) {
		IAttribute result = element.attribute(name);
		if (result == null) {
			if (element.hasElements(name)) {
				return new ElementChildrenArgsAdapter(element, name);
			}
			return null;
		}
		return result.getValue();
	}

	public Object get(String name, Object defaultValue) {
		IAttribute result = element.attribute(name);
		if (result == null) {
			if (element.hasElements(name)) {
				return new ElementChildrenArgsAdapter(element, name);
			} else {
				return defaultValue;
			}
		}
		return result.getValue();
	}

	@Override
	public boolean isDefined(int index) {
		// not supported
		return false;
	}

	public boolean isDefined(String name) {
		return element.hasElements(name) || element.hasAttribute(name);
	}

	public Set<String> names() {
		final Set<String> names = new HashSet<>();
		Iterator<IElement> elements = element.elementIterator();
		while (elements.hasNext()) {
			IElement element = elements.next();
			names.add(element.getName());
		}
		Iterator<String> attributes = element.attributeNames();
		while (attributes.hasNext()) {
			String name = attributes.next();
			names.add(name);
		}
		return names;
	}

	public IBinding put(int index, Object value) {
		throw new UnsupportedOperationException("can't write to ArgsAdapter"); //$NON-NLS-1$
	}

	public IBinding put(String name, Object value) {
		throw new UnsupportedOperationException("can't write to ArgsAdapter"); //$NON-NLS-1$
	}

	public int size() {
		return names().size();
	}

	@Override
	public String toString() {
		return ArgTools.toString(this, ""); //$NON-NLS-1$
	}

	public void undefine(int index) {
		throw new UnsupportedOperationException("can't write to ArgsAdapter"); //$NON-NLS-1$
	}

	public void undefine(String name) {
		throw new UnsupportedOperationException("can't write to ArgsAdapter"); //$NON-NLS-1$
	}

}
