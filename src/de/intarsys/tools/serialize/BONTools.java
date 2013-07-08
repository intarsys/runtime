package de.intarsys.tools.serialize;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.string.StringTools;

public class BONTools {

	public static Object deserialize(byte[] value) throws IOException {
		BONSerializationFactory factory = new BONSerializationFactory();
		StreamSerializationContext context = new StreamSerializationContext(
				value);
		IDeserializer deserializer = factory.createDeserializer(context);
		return deserializer.deserialize();
	}

	public static Object deserialize(String value) throws IOException {
		BONSerializationFactory factory = new BONSerializationFactory();
		StreamSerializationContext context = new StreamSerializationContext(
				value);
		IDeserializer deserializer = factory.createDeserializer(context);
		return deserializer.deserialize();
	}

	public static void flatten(Object object, String prefix,
			Map<String, String> map) throws IOException {
		if (object instanceof Map) {
			Iterator it = ((Map) object).entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String key = String.valueOf(entry.getKey());
				Object value = entry.getValue();
				String nestedPrefix;
				if (StringTools.isEmpty(prefix)) {
					nestedPrefix = key;
				} else {
					nestedPrefix = prefix + "." + key;
				}
				flatten(value, nestedPrefix, map);
			}
		} else if (object instanceof Object[]) {
			int i = 0;
			Iterator it = Arrays.asList((Object[]) object).iterator();
			while (it.hasNext()) {
				Object value = it.next();
				String nestedPrefix;
				if (StringTools.isEmpty(prefix)) {
					nestedPrefix = "" + i;
				} else {
					nestedPrefix = prefix + "." + i;
				}
				flatten(value, nestedPrefix, map);
			}
		} else if (object instanceof List) {
			int i = 0;
			Iterator it = ((List) object).iterator();
			while (it.hasNext()) {
				Object value = it.next();
				String nestedPrefix;
				if (StringTools.isEmpty(prefix)) {
					nestedPrefix = "" + i;
				} else {
					nestedPrefix = prefix + "." + i;
				}
				flatten(value, nestedPrefix, map);
			}
		} else if (object instanceof IArgs) {
			IArgs args = (IArgs) object;
			int i = 0;
			for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
				IBinding binding = it.next();
				Object value = binding.getValue();
				String nestedPrefix;
				String name = binding.getName() == null ? "" + i : binding
						.getName();
				if (StringTools.isEmpty(prefix)) {
					nestedPrefix = name;
				} else {
					nestedPrefix = prefix + "." + name;
				}
				flatten(value, nestedPrefix, map);
				i++;
			}
		} else {
			String serialized = serializeString(object);
			map.put(prefix, serialized);
		}
	}

	static public boolean isSupported(Object value) {
		if (value == null) {
			return true;
		} else if (value instanceof Number) {
			return true;
		} else if (value instanceof Boolean) {
			return true;
		} else if (value instanceof String) {
			return true;
		} else if (value instanceof byte[]) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] serializeBytes(Object value) throws IOException {
		BONSerializationFactory factory = new BONSerializationFactory();
		StreamSerializationContext context = new StreamSerializationContext();
		ISerializer serializer = factory.createSerializer(context);
		serializer.serialize(value);
		return context.getBytes();
	}

	public static String serializeString(Object value) throws IOException {
		BONSerializationFactory factory = new BONSerializationFactory();
		StreamSerializationContext context = new StreamSerializationContext();
		ISerializer serializer = factory.createSerializer(context);
		serializer.serialize(value);
		return new String(context.getBytes(), "UTF-8");
	}

}
