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
package de.intarsys.tools.functor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.intarsys.tools.collection.ArrayIterator;

/**
 * A concrete generic implementation for {@link IArgs}.
 * 
 */
public class Args implements IArgs {

	static class Binding implements IBinding {
		protected String name;
		protected Object value;

		protected Binding(String name) {
			super();
			this.name = name;
			this.value = UNDEFINED;
		}

		protected Binding(String name, Object value) {
			super();
			this.name = name;
			this.value = value;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Object getValue() {
			return value == UNDEFINED ? null : value;
		}

		@Override
		public boolean isDefined() {
			return value != UNDEFINED;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public void setValue(Object pValue) {
			value = pValue;
		}

		@Override
		public String toString() {
			return "" + name + ":" + value;
		}
	}

	static final private Binding[] EMPTY = new Binding[0];

	private static final Object UNDEFINED = new Object();

	public static Args create() {
		return new Args();
	}

	public static Args createIndexed(Object... values) {
		return new Args(values);
	}

	public static Args createNamed(String key, Object value) {
		Map map = new HashMap();
		map.put(key, value);
		return new Args(map);
	}

	public static Args createNamed(String key1, Object value1, String key2,
			Object value2) {
		Map map = new HashMap();
		map.put(key1, value1);
		map.put(key2, value2);
		return new Args(map);
	}

	private Binding[] entries = EMPTY;

	private int ptr = 0;

	public Args() {
		super();
	}

	public Args(Args pArgs) {
		super();
		if (pArgs == null) {
			return;
		}
		ptr = pArgs.ptr;
		if (ptr > 0) {
			entries = new Binding[ptr];
			for (int i = 0; i < ptr; i++) {
				Binding tempEntry = pArgs.entries[i];
				Object value = tempEntry.value;
				if (value instanceof IArgs) {
					entries[i] = new Binding(tempEntry.name,
							((IArgs) value).copy());
				} else {
					entries[i] = new Binding(tempEntry.name, value);
				}
			}
		}
	}

	public Args(List values) {
		super();
		ptr = values.size();
		entries = new Binding[ptr];
		for (int i = 0; i < ptr; i++) {
			entries[i] = new Binding(null, values.get(i));
		}
	}

	public Args(Map values) {
		super();
		ptr = values.size();
		entries = new Binding[ptr];
		int i = 0;
		for (Iterator it = values.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = (String) entry.getKey();
			Object value = entry.getValue();
			entries[i++] = new Binding(name, value);
		}
	}

	public Args(Object... values) {
		super();
		if (values != null) {
			// its common for values to be null, for example in java reflection
			// code -> support it
			ptr = values.length;
			entries = new Binding[ptr];
			for (int i = 0; i < ptr; i++) {
				entries[i] = new Binding(null, values[i]);
			}
		}
	}

	@Override
	public IBinding add(Object object) {
		ensureCapacity(ptr);
		Binding tempBinding = new Binding(null, object);
		entries[ptr++] = tempBinding;
		return tempBinding;
	}

	@Override
	public Iterator<IBinding> bindings() {
		return new ArrayIterator(entries, ptr);
	}

	@Override
	public void clear() {
		for (int i = 0; i < ptr; i++) {
			entries[i] = null;
		}
		ptr = 0;
	}

	@Override
	public IArgs copy() {
		return new Args(this);
	}

	@Override
	public IBinding declare(String name) {
		Binding tempBinding;
		// first check all bindings if already available
		for (int i = 0; i < ptr; i++) {
			tempBinding = entries[i];
			if (name.equals(tempBinding.name)) {
				return tempBinding;
			}
		}
		// try to bind get the unnamed binding
		for (int i = 0; i < ptr; i++) {
			tempBinding = entries[i];
			if (tempBinding.name == null) {
				tempBinding.name = name;
				return tempBinding;
			}
		}
		// add new entry
		ensureCapacity(ptr);
		tempBinding = new Binding(name);
		entries[ptr++] = tempBinding;
		return tempBinding;
	}

	protected void ensureCapacity(int min) {
		if (min >= entries.length) {
			Binding[] newEntries = new Binding[min + 4];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			entries = newEntries;
		}
	}

	@Override
	public Object get(int index) {
		if (index < 0 || index >= ptr) {
			return null;
		}
		if (entries[index].isDefined()) {
			return entries[index].value;
		} else {
			return null;
		}
	}

	@Override
	public Object get(int index, Object defaultValue) {
		if (index < 0 || index >= ptr) {
			return defaultValue;
		}
		if (entries[index].isDefined()) {
			return entries[index].value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public Object get(String name) {
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name) && entry.isDefined()) {
				return entry.value;
			}
		}
		return null;
	}

	@Override
	public Object get(String name, Object defaultValue) {
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name) && entry.isDefined()) {
				return entry.value;
			}
		}
		return defaultValue;
	}

	@Override
	public boolean isDefined(int index) {
		if (index < 0 || index >= ptr) {
			return false;
		}
		return entries[index].isDefined();
	}

	@Override
	public boolean isDefined(String name) {
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name)) {
				return entry.isDefined();
			}
		}
		return false;
	}

	public boolean isIndexed() {
		return true;
	}

	public boolean isNamed() {
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (entry.name != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Set names() {
		Set result = new HashSet();
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (entry.name != null) {
				result.add(entry.name);
			}
		}
		return result;
	}

	@Override
	public IBinding put(int index, Object value) {
		Binding tempBinding;
		if (index >= ptr) {
			ensureCapacity(index);
			for (int i = ptr; i < index; i++) {
				entries[i] = new Binding(null);
			}
			tempBinding = new Binding(null, value);
			entries[index] = tempBinding;
			ptr = index + 1;
		} else {
			tempBinding = entries[index];
			entries[index].setValue(value);
		}
		return tempBinding;
	}

	@Override
	public IBinding put(String name, Object value) {
		Binding tempBinding;
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name)) {
				tempBinding = entry;
				entry.setValue(value);
				return tempBinding;
			}
		}
		ensureCapacity(ptr);
		tempBinding = new Binding(name, value);
		entries[ptr++] = tempBinding;
		return tempBinding;
	}

	@Override
	public int size() {
		return ptr;
	}

	@Override
	public String toString() {
		return ArgTools.toString(this, "");
	}

	@Override
	public void undefine(int index) {
		if (index < ptr) {
			entries[index].setValue(UNDEFINED);
		}
	}

	@Override
	public void undefine(String name) {
		Binding tempBinding;
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name)) {
				tempBinding = entry;
				entry.setValue(UNDEFINED);
			}
		}
	}

}
