package de.intarsys.tools.functor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.locator.ILocator;

/**
 * A tool class for handling "result" objects.
 * <p>
 * The result args contain information on the result requested for a service.
 * 
 * <pre>
 * result = {
 * 		return = "void" | "primitive" | "composite";
 * 		structure = "hierarchical" | "flat";
 * 		property = {
 * 			&lt;path&gt; = {
 * 				return = "true" | "false";
 * 				style = "literal" | "reference";
 * 				display = "true" | "false";
 * 			}
 * 			...
 * 		}
 * }
 * </pre>
 * 
 */
public final class ResultTools {

	public static final String ARG_RESULT = "result";

	public static final String ARG_RESULT_RETURN = "result.return";

	public static final String ARG_RESULT_STRUCTURE = "result.structure";

	public static final String ARG_RESULT_PROPERTY = "result.property";

	public static final String PROP_STYLE = "style";

	public static final String PROP_RETURN = "return";

	public static final String PROP_DISPLAY = "display";

	public static Object createResult(IArgs result, IArgs args) {
		Object resultObject = null;
		EnumReturnMode returnMode = ResultTools.getResultReturnMode(args);
		if (returnMode == EnumReturnMode.VOID) {
			//
		} else if (returnMode == EnumReturnMode.PRIMITIVE) {
			for (IBinding binding : result) {
				IArgs propertyDescriptor = getPropertyDescriptor(args, binding.getName());
				if (ResultTools.isPropertyReturn(propertyDescriptor, true)) {
					resultObject = encode(binding.getValue(), propertyDescriptor);
					break;
				}
			}
		} else {
			IArgs basicResult = Args.create();
			for (IBinding binding : result) {
				IArgs propertyDescriptor = getPropertyDescriptor(args, binding.getName());
				if (ResultTools.isPropertyReturn(propertyDescriptor, true)) {
					basicResult.put(binding.getName(), encode(binding.getValue(), propertyDescriptor));
				}
			}
			resultObject = basicResult;
			try {
				resultObject = ConverterRegistry.get().convert(resultObject, ArgSerialized.class);
			} catch (ConversionException e) {
				//
			}
		}
		if (resultObject instanceof IArgs) {
			EnumResultStructure resultStructure = ResultTools.getResultStructure(args);
			if (resultStructure == EnumResultStructure.FLAT) {
				resultObject = ArgTools.flatten((IArgs) resultObject);
			}
		}
		return resultObject;
	}

	protected static void detectPropertyDescriptor(Map<String, IArgs> map, String property, IArgs properties) {
		Iterator<IBinding> it = properties.bindings();
		while (it.hasNext()) {
			IBinding binding = it.next();
			String name = binding.getName();
			IArgs value = ArgTools.toArgs(binding.getValue());
			String path = property == null ? name : property + "." + name;
			if (isPropertyDescriptor(value)) {
				map.put(path, value);
			} else {
				detectPropertyDescriptor(map, path, value);
			}
		}
	}

	protected static Object encode(Object value, IArgs propertyDescriptor) {
		if (value instanceof IArgs) {
			for (IBinding binding : (IArgs) value) {
				if (ResultTools.isPropertyReturn(propertyDescriptor, true)) {
					Object encoded = encode(binding.getValue(), propertyDescriptor);
					binding.setValue(encoded);
				}
			}
		} else if (value instanceof ILocator) {
			EnumStyle style = EnumStyle.get(propertyDescriptor);
			return encodeLocator((ILocator) value, style);
		}
		return value;
	}

	protected static Object encodeLocator(ILocator locator, EnumStyle style) {
		if (style == EnumStyle.REFERENCE) {
			IArgs result = Args.create();
			result.put("locator", locator.getPath());
			// fill deprecated attribute 'path'
			result.put("path", locator.getPath());
			return result;
		}
		return locator;
	}

	public static IArgs getPropertyDescriptor(IArgs args, String name) {
		IArgs property = ArgTools.getArgs(args, ARG_RESULT_PROPERTY + "." + name, Args.create());
		return property;
	}

	public static Map<String, IArgs> getPropertyDescriptors(IArgs args) {
		Map<String, IArgs> map = new HashMap<>();
		IArgs properties = ArgTools.getArgs(args, ARG_RESULT_PROPERTY, Args.create());
		detectPropertyDescriptor(map, null, properties);
		return map;
	}

	public static EnumStyle getPropertyStyle(IArgs propertyDescriptor, EnumStyle defaultValue) {
		return ArgTools.getEnumItemStrict(propertyDescriptor, EnumStyle.META, PROP_STYLE, defaultValue);
	}

	public static EnumStyle getPropertyStyle(IArgs args, String name, EnumStyle defaultValue) {
		return ArgTools.getEnumItemStrict(args, EnumStyle.META, ARG_RESULT_PROPERTY + "." + name + ".style",
				defaultValue);
	}

	public static IArgs getResultDescriptor(IArgs args) {
		return ArgTools.getArgs(args, ARG_RESULT, Args.create());
	}

	public static EnumReturnMode getResultReturnMode(IArgs args) {
		return (EnumReturnMode) ArgTools.getEnumItemStrict(args, EnumReturnMode.META, ARG_RESULT_RETURN);
	}

	public static EnumResultStructure getResultStructure(IArgs args) {
		return (EnumResultStructure) ArgTools.getEnumItemStrict(args, EnumResultStructure.META, ARG_RESULT_STRUCTURE);
	}

	public static boolean isPropertyDescriptor(IArgs args) {
		if (args == null || args.size() == 0) {
			return true;
		}
		Iterator<IBinding> it = args.bindings();
		while (it.hasNext()) {
			IBinding binding = it.next();
			String name = binding.getName();
			if (!isPropertyDescriptorProperty(name)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isPropertyDescriptorProperty(String name) {
		return "return".equals(name) || "display".equals(name) || "style".equals(name);
	}

	public static boolean isPropertyDisplay(IArgs propertyDescriptor, boolean defaultValue) {
		return ArgTools.getBoolStrict(propertyDescriptor, PROP_DISPLAY, defaultValue);
	}

	public static boolean isPropertyDisplay(IArgs args, String name, boolean defaultValue) {
		return ArgTools.getBoolStrict(args, ARG_RESULT_PROPERTY + "." + name + ".display", defaultValue);
	}

	public static boolean isPropertyReturn(IArgs propertyDescriptor, boolean defaultValue) {
		return ArgTools.getBoolStrict(propertyDescriptor, PROP_RETURN, defaultValue);
	}

	public static boolean isPropertyReturn(IArgs args, String name, boolean defaultValue) {
		return ArgTools.getBoolStrict(args, ARG_RESULT_PROPERTY + "." + name + ".return", defaultValue);
	}

	private ResultTools() {
	}

}
