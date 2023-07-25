/*
 * Copyright (c) 2007, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.intarsys.tools.collection.ArrayIterator;
import de.intarsys.tools.factory.IFactory;
import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.lang.LangTools;

/**
 * A concrete generic implementation for {@link IArgs}.
 * 
 * ATTENTION: This implementation supports a "legacy feature" with regard to
 * {@link InstanceSpec} style declarations. Initial style for declaring
 * instances was
 * 
 * <pre>
 *  args = {
 *  	instance : "myInstanceFactoryId",
 *  	instanceArgs : {
 *  		gnu : "gnat",
 *  		foo : "bar"
 *  	}
 *  }
 * </pre>
 * 
 * Now
 * 
 * <pre>
 *  args = {
 *  	instance : {
 *  		factory: "myInstanceFactoryId",
 *  		args : {
 *  			gnu : "gnat",
 *  			foo : "bar"
 *  		}
 *  	}
 *  }
 * </pre>
 * 
 * is considered more appropriate. To support legacy style arguments, we must
 * canonicalize arguments on the lowest common denominator, which is this
 * implementation. This leads to the reserved argument pattern "xyzArgs", which
 * is always transformed to the canonical {@link InstanceSpec} style.
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
			this.value = toArgValue(value);
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
			value = toArgValue(pValue);
		}

		@Override
		public String toString() {
			return "" + name + ":" + value;
		}
	}

	private static final Binding[] EMPTY = new Binding[0];

	private static final Object UNDEFINED = new Object();

	public static Args create() {
		return new Args();
	}

	public static Args createIndexed(Object... values) {
		return new Args(values);
	}

	public static Args createNamed(Object... keyValue) {
		Args args = new Args();
		if (keyValue.length % 2 != 0) {
			throw new IllegalArgumentException("even number of arguments required");
		}
		int i = 0;
		while (i < keyValue.length) {
			String key = String.valueOf(keyValue[i++]);
			Object value = null;
			if (i < keyValue.length) {
				value = keyValue[i++];
			}
			ArgTools.putPath(args, key, value);
		}
		return args;
	}

	protected static IArgs instanceSpecToArgs(Object spec) {
		if (spec instanceof InstanceSpec) {
			IArgs args = ((InstanceSpec<?>) spec).toArgs();
			return args;
		}
		if (spec instanceof IArgs) {
			IArgs args = (IArgs) spec;
			return args;
		}
		IArgs args = Args.create();
		if (spec instanceof String || spec instanceof Class || spec instanceof IFactory) {
			args.put(InstanceSpec.ARG_FACTORY, spec);
		}
		return args;
	}

	protected static boolean isInstanceSpec(Object oldValue) {
		if (oldValue instanceof InstanceSpec) {
			return true;
		}
		if (oldValue instanceof IArgs) {
			IArgs tempArgs = (IArgs) oldValue;
			if (tempArgs.get(ARG_ARGS) != null) {
				return true;
			}
			if (tempArgs.get(InstanceSpec.ARG_FACTORY) != null) {
				return true;
			}
		}
		return false;
	}

	protected static Object toArgValue(Object value) {
		if (value instanceof InstanceSpec) {
			return ((InstanceSpec<?>) value).toArgs();
		}
		return value;
	}

	private Binding[] entries = EMPTY;

	private int ptr;

	public Args() {
		super();
	}

	protected Args(Args pArgs) {
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
					entries[i] = new Binding(tempEntry.name, ((IArgs) value).copy());
				} else {
					entries[i] = new Binding(tempEntry.name, value);
				}
			}
		}
	}

	public Args(List<Object> values) {
		super();
		ArgTools.putAllDeep(this, values);
	}

	public Args(Map<String, Object> values) {
		super();
		ArgTools.putAllDeep(this, values);
	}

	public Args(Object... values) {
		super();
		if (values != null) {
			// its common for values to be null, for example in java reflection
			// code -> support it
			for (int i = 0; i < values.length; i++) {
				add(values[i]);
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
		return new ArrayIterator<>(entries, ptr);
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
		if (name.endsWith("Args") && name.length() > 4) {
			IArgs spec = getAsInstanceSpecArgs(name);
			return spec.declare(ARG_ARGS);
		} else {
			Binding tempBinding = getBinding(name);
			if (tempBinding != null) {
				return tempBinding;
			}
			// try to bind the first unnamed binding
			for (int i = 0; i < ptr; i++) {
				tempBinding = entries[i];
				if (tempBinding.name == null) {
					tempBinding.name = name;
					return tempBinding;
				}
			}
			// add new entry
			return put(name, UNDEFINED);
		}
	}

	protected void ensureCapacity(int min) {
		if (min >= entries.length) {
			Binding[] newEntries = new Binding[min + 4];
			System.arraycopy(entries, 0, newEntries, 0, entries.length);
			entries = newEntries;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IArgs) {
			IArgs other = (IArgs) obj;
			int size = size();
			if (other.size() != size) {
				return false;
			}
			for (int index = 0; index < size; index++) {
				IBinding binding = entries[index];
				Object value = binding.getValue();
				Object otherValue;
				String name = binding.getName();
				if (name == null) {
					otherValue = other.get(index);
				} else {
					otherValue = other.get(name);
				}
				if (!LangTools.equals(value, otherValue)) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public Object get(int index) {
		if (index < 0 || index >= ptr) {
			return null;
		}
		return entries[index].getValue();
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
		if (name.endsWith("Args") && name.length() > 4) {
			// restructure InstanceSpec type
			String tempName = name.substring(0, name.length() - 4);
			Binding entry = getBinding(tempName);
			if (entry != null) {
				Object tempValue = entry.getValue();
				if (tempValue instanceof IArgs) {
					IArgs args = (IArgs) tempValue;
					return args.get(ARG_ARGS);
				}
			}
			return null;
		} else {
			Binding entry = getBinding(name);
			return entry == null ? null : entry.getValue();
		}
	}

	@Override
	public Object get(String name, Object defaultValue) {
		Object result = get(name);
		return result == null ? defaultValue : result;
	}

	protected IArgs getAsInstanceSpecArgs(String name) {
		// restructure InstanceSpec type
		String tempName = name.substring(0, name.length() - 4);
		Binding oldBinding = getBinding(tempName);
		Object oldValue = oldBinding == null ? null : oldBinding.getValue();
		IArgs spec = instanceSpecToArgs(oldValue);
		put(tempName, spec);
		return spec;
	}

	protected Binding getBinding(String name) {
		for (int i = 0; i < ptr; i++) {
			Binding entry = entries[i];
			if (name.equals(entry.name)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		StringBuilder sb = new StringBuilder();
		for (IBinding binding : this) {
			if (binding.getName() != null) {
				sb.append(binding.getName().hashCode());
			}
			sb.append('@');
			if (binding.getValue() != null) {
				sb.append(binding.getValue().hashCode());
			}
			sb.append('#');
		}
		return sb.toString().hashCode();
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
		if (name.endsWith("Args") && name.length() > 4) {
			// restructure InstanceSpec type
			String tempName = name.substring(0, name.length() - 4);
			Binding entry = getBinding(tempName);
			if (entry != null && entry.isDefined()) {
				Object tempValue = entry.getValue();
				if (tempValue instanceof IArgs) {
					IArgs args = (IArgs) tempValue;
					return args.isDefined(ARG_ARGS);
				}
			}
			return false;
		} else {
			Binding binding = getBinding(name);
			return binding != null && binding.isDefined();
		}
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public boolean isIndexed() {
		return true;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public boolean isNamed() {
		return ArgTools.isNamed(this);
	}

	@Override
	public Iterator<IBinding> iterator() {
		return bindings();
	}

	@Override
	public Set<String> names() {
		Set<String> result = new HashSet<>();
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
		if (name.endsWith("Args") && name.length() > 4) {
			IArgs spec = getAsInstanceSpecArgs(name);
			return spec.put(ARG_ARGS, value);
		} else {
			Binding binding = getBinding(name);
			if (binding != null) {
				Object oldValue = binding.getValue();
				// if we set old style xxArgs first, we will end up here
				// merge InstanceSpec type
				if (isInstanceSpec(oldValue) && value instanceof String) {
					IArgs spec = instanceSpecToArgs(oldValue);
					binding.setValue(spec);
					return spec.put(InstanceSpec.ARG_FACTORY, value);
				} else {
					binding.setValue(value);
					return binding;
				}
			}
			ensureCapacity(ptr);
			binding = new Binding(name, value);
			entries[ptr++] = binding;
			return binding;
		}
	}

	@Override
	public int size() {
		return ptr;
	}

	@Override
	public String toString() {
		return ArgTools.toPrintString(this, "");
	}

	@Override
	public void undefine(int index) {
		if (index < ptr) {
			entries[index].setValue(UNDEFINED);
		}
	}

	@Override
	public void undefine(String name) {
		if (name.endsWith("Args") && name.length() > 4) {
			IArgs spec = getAsInstanceSpecArgs(name);
			spec.undefine(ARG_ARGS);
		} else {
			Binding binding = getBinding(name);
			if (binding != null) {
				binding.setValue(UNDEFINED);
			}
		}
	}
}
