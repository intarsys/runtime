package de.intarsys.tools.ipc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.collection.ConversionIterator;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;

public class IPCMarshaller {

	public IPCMarshaller() {
		super();
	}

	protected IIPCScope getScope() {
		return IPCScope.get();
	}

	public Object marshal(Object value) throws IOException {
		if (value instanceof IArgs) {
			return marshalArgs((IArgs) value);
		} else if (value instanceof Map) {
			return marshalMap((Map) value);
		} else if (value instanceof Object[]) {
			return marshalArray((Object[]) value);
		} else if (value instanceof Collection) {
			return marshalCollection((Collection) value);
		} else if (value instanceof Iterator) {
			return marshalIterator((Iterator) value);
		}
		// todo should use more explicit conversion
		try {
			value = ConverterRegistry.get().convert(value, IPCObject.class);
		} catch (ConversionException e) {
			throw new IOException(e);
		}
		return value;
	}

	protected Object marshalArgs(IArgs args) throws IOException {
		IArgs tempCopy = Args.create();
		boolean touched = false;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			String name = binding.getName();
			Object value = binding.getValue();
			Object newValue = marshal(value);
			tempCopy.add(newValue).setName(name);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : args;
	}

	protected Object marshalArray(Object[] array) throws IOException {
		Object[] tempCopy = new Object[array.length];
		boolean touched = false;
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			Object newValue = marshal(value);
			tempCopy[i] = newValue;
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : array;
	}

	protected Object marshalCollection(Collection collection) throws IOException {
		List<Object> tempCopy = new ArrayList<>(collection.size());
		boolean touched = false;
		for (Object value : collection) {
			Object newValue = marshal(value);
			tempCopy.add(newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : collection;
	}

	protected Object marshalIterator(Iterator iterator) {
		final IIPCScope deferredScope = getScope();
		return new ConversionIterator(iterator) {

			@Override
			protected Object createTargetObject(Object sourceObject) {
				IIPCScope currentScope = IPCScope.lookup();
				try {
					IPCScope.set(deferredScope);
					return marshal(sourceObject);
				} catch (IOException e) {
					throw ExceptionTools.wrap(e);
				} finally {
					IPCScope.set(currentScope);
				}
			}
		};
	}

	/**
	 * @deprecated use {@link #marshal(value) marshal}
	 */
	@Deprecated
	public Object marshall(Object value) throws IOException {
		return marshal(value);
	}

	protected Object marshalMap(Map map) throws IOException {
		Map tempCopy = new HashMap(map.size());
		boolean touched = false;
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			Object newValue = marshal(value);
			tempCopy.put(key, newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : map;
	}

	/**
	 * Unmarshal a value from transport layer.
	 * <p>
	 * ATTENTION: Currently this method must unmarshal its {@link IArgs}
	 * in-place!
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public Object unmarshal(Object value) throws IOException {
		if (value instanceof IArgs) {
			return unmarshalArgs((IArgs) value);
		} else if (value instanceof Map) {
			return unmarshalMap((Map) value);
		} else if (value instanceof Object[]) {
			return unmarshalArray((Object[]) value);
		} else if (value instanceof Collection) {
			return unmarshalCollection((Collection) value);
		} else if (value instanceof Iterator) {
			return unmarshalIterator((Iterator) value);
		} else if (value instanceof String) {
			return unmarshalString((String) value);
		}
		return value;
	}

	protected Object unmarshalArgs(IArgs args) throws IOException {
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			Object value = binding.getValue();
			Object newValue = unmarshal(value);
			binding.setValue(newValue);
		}
		return args;
	}

	protected Object unmarshalArray(Object[] array) throws IOException {
		Object[] tempCopy = new Object[array.length];
		boolean touched = false;
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			Object newValue = unmarshal(value);
			tempCopy[i] = newValue;
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : array;
	}

	protected Object unmarshalCollection(Collection collection) throws IOException {
		List<Object> tempCopy = new ArrayList<>(collection.size());
		boolean touched = false;
		for (Object value : collection) {
			Object newValue = unmarshal(value);
			tempCopy.add(newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : collection;
	}

	protected Object unmarshalIterator(Iterator iterator) {
		return new ConversionIterator(iterator) {

			@Override
			protected Object createTargetObject(Object sourceObject) {
				try {
					return unmarshal(sourceObject);
				} catch (IOException e) {
					throw ExceptionTools.wrap(e);
				}
			}
		};
	}

	/**
	 * @deprecated use {@link #unmarshal(value) unmarshal}
	 */
	@Deprecated
	public Object unmarshall(Object value) throws IOException {
		return unmarshal(value);
	}

	protected Object unmarshalMap(Map map) throws IOException {
		Map tempCopy = new HashMap(map.size());
		boolean touched = false;
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			Object newValue = unmarshal(value);
			tempCopy.put(key, newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : map;
	}

	protected Object unmarshalString(String value) throws IOException {
		String tempString = value;
		if (tempString.startsWith("ipc://")) {
			try {
				IPCHandle handle = getScope().importHandle(tempString);
				if (handle.isResolved()) {
					return handle.getObject();
				}
				return handle;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		return value;
	}
}
