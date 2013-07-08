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

	public Object marshall(Object value) throws IOException {
		if (value instanceof IArgs) {
			return marshallArgs((IArgs) value);
		} else if (value instanceof Map) {
			return marshallMap((Map) value);
		} else if (value instanceof Object[]) {
			return marshallArray((Object[]) value);
		} else if (value instanceof Collection) {
			return marshallCollection((Collection) value);
		} else if (value instanceof Iterator) {
			return marshallIterator((Iterator) value);
		}
		// todo should use more explicit conversion
		try {
			value = ConverterRegistry.get().convert(value, IPCObject.class);
		} catch (ConversionException e) {
			throw new IOException(e);
		}
		return value;
	}

	protected Object marshallArgs(IArgs args) throws IOException {
		IArgs tempCopy = Args.create();
		boolean touched = false;
		int i = 0;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			String name = binding.getName();
			Object value = binding.getValue();
			Object newValue = marshall(value);
			tempCopy.add(newValue).setName(name);
			if (newValue != value) {
				touched = true;
			}
			i++;
		}
		return touched ? tempCopy : args;
	}

	protected Object marshallArray(Object[] array) throws IOException {
		Object[] tempCopy = new Object[array.length];
		boolean touched = false;
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			Object newValue = marshall(value);
			tempCopy[i] = newValue;
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : array;
	}

	protected Object marshallCollection(Collection collection)
			throws IOException {
		List<Object> tempCopy = new ArrayList<Object>(collection.size());
		boolean touched = false;
		for (Object value : collection) {
			Object newValue = marshall(value);
			tempCopy.add(newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : collection;
	}

	protected Object marshallIterator(Iterator iterator) {
		final IIPCScope deferredScope = getScope();
		return new ConversionIterator(iterator) {
			@Override
			protected Object createTargetObject(Object sourceObject) {
				IIPCScope currentScope = IPCScope.lookup();
				try {
					IPCScope.set(deferredScope);
					return marshall(sourceObject);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					IPCScope.set(currentScope);
				}
			}
		};
	}

	protected Object marshallMap(Map map) throws IOException {
		Map tempCopy = new HashMap(map.size());
		boolean touched = false;
		int i = 0;
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			Object newValue = marshall(value);
			tempCopy.put(key, newValue);
			if (newValue != value) {
				touched = true;
			}
			i++;
		}
		return touched ? tempCopy : map;
	}

	/**
	 * Unmarshall a value from transport layer.
	 * <p>
	 * ATTENTION: Currently this method must unmarshall its {@link IArgs}
	 * in-place!
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public Object unmarshall(Object value) throws IOException {
		if (value instanceof IArgs) {
			return unmarshallArgs((IArgs) value);
		} else if (value instanceof Map) {
			return unmarshallMap((Map) value);
		} else if (value instanceof Object[]) {
			return unmarshallArray((Object[]) value);
		} else if (value instanceof Collection) {
			return unmarshallCollection((Collection) value);
		} else if (value instanceof Iterator) {
			return unmarshallIterator((Iterator) value);
		} else if (value instanceof String) {
			return unmarshallString((String) value);
		}
		return value;
	}

	protected Object unmarshallArgs(IArgs args) throws IOException {
		int i = 0;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			Object value = binding.getValue();
			Object newValue = unmarshall(value);
			binding.setValue(newValue);
			i++;
		}
		return args;
	}

	protected Object unmarshallArray(Object[] array) throws IOException {
		Object[] tempCopy = new Object[array.length];
		boolean touched = false;
		for (int i = 0; i < array.length; i++) {
			Object value = array[i];
			Object newValue = unmarshall(value);
			tempCopy[i] = newValue;
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : array;
	}

	protected Object unmarshallCollection(Collection collection)
			throws IOException {
		List<Object> tempCopy = new ArrayList<Object>(collection.size());
		boolean touched = false;
		for (Object value : collection) {
			Object newValue = unmarshall(value);
			tempCopy.add(newValue);
			if (newValue != value) {
				touched = true;
			}
		}
		return touched ? tempCopy : collection;
	}

	protected Object unmarshallIterator(Iterator iterator) {
		return new ConversionIterator(iterator) {
			@Override
			protected Object createTargetObject(Object sourceObject) {
				try {
					return unmarshall(sourceObject);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	protected Object unmarshallMap(Map map) throws IOException {
		Map tempCopy = new HashMap(map.size());
		boolean touched = false;
		int i = 0;
		for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			Object newValue = unmarshall(value);
			tempCopy.put(key, newValue);
			if (newValue != value) {
				touched = true;
			}
			i++;
		}
		return touched ? tempCopy : map;
	}

	protected Object unmarshallString(String value) throws IOException {
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
