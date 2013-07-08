package de.intarsys.tools.functor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.functor.IArgs.IBinding;

/**
 * A tool class for handling "result" objects.
 * <p>
 * The result args contain information on the result requested for a service.
 * 
 * <pre>
 * result = {
 * 		return = "void" | "primitive" | "composite";
 * 		property = {
 * 			<path> = {
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
public class ResultTools {

	public static final String ARG_RESULT = "result";

	public static final String ARG_RESULT_RETURN = "result.return";

	public static final String ARG_RESULT_PROPERTY = "result.property";

	public static final String PROP_STYLE = "style";

	public static final String PROP_RETURN = "return";

	public static final String PROP_DISPLAY = "display";

	protected static void detectPropertyDescriptor(Map<String, IArgs> map,
			String property, IArgs properties) {
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

	public static IArgs getPropertyDescriptor(IArgs args, String name) {
		IArgs property = ArgTools.getArgs(args, ARG_RESULT_PROPERTY + "."
				+ name, Args.create());
		return property;
	}

	public static Map<String, IArgs> getPropertyDescriptors(IArgs args) {
		Map<String, IArgs> map = new HashMap<String, IArgs>();
		IArgs properties = ArgTools.getArgs(args, ARG_RESULT_PROPERTY,
				Args.create());
		detectPropertyDescriptor(map, null, properties);
		return map;
	}

	public static EnumStyle getPropertyStyle(IArgs propertyDescriptor,
			EnumStyle defaultValue) {
		return ArgTools.getEnumItem(propertyDescriptor, EnumStyle.META,
				PROP_STYLE, defaultValue);
	}

	public static EnumStyle getPropertyStyle(IArgs args, String name,
			EnumStyle defaultValue) {
		return ArgTools.getEnumItem(args, EnumStyle.META, ARG_RESULT_PROPERTY
				+ "." + name + ".style", defaultValue);
	}

	public static IArgs getResultDescriptor(IArgs args) {
		return ArgTools.getArgs(args, ARG_RESULT, Args.create());
	}

	public static EnumReturnMode getResultReturnMode(IArgs args) {
		return (EnumReturnMode) ArgTools.getEnumItem(args, EnumReturnMode.META,
				ARG_RESULT_RETURN);
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
		return "return".equals(name) || "display".equals(name)
				|| "style".equals(name);
	}

	public static boolean isPropertyDisplay(IArgs propertyDescriptor,
			boolean defaultValue) {
		return ArgTools.getBoolean(propertyDescriptor, PROP_DISPLAY,
				defaultValue);
	}

	public static boolean isPropertyDisplay(IArgs args, String name,
			boolean defaultValue) {
		return ArgTools.getBoolean(args, ARG_RESULT_PROPERTY + "." + name
				+ ".display", defaultValue);
	}

	public static boolean isPropertyReturn(IArgs propertyDescriptor,
			boolean defaultValue) {
		return ArgTools.getBoolean(propertyDescriptor, PROP_RETURN,
				defaultValue);
	}

	public static boolean isPropertyReturn(IArgs args, String name,
			boolean defaultValue) {
		return ArgTools.getBoolean(args, ARG_RESULT_PROPERTY + "." + name
				+ ".return", defaultValue);
	}

}
