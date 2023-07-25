/*
 * Copyright (c) 2007, intarsys GmbH
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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.crypto.CryptoTools;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.digest.DigestTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.geometry.GeometryTools;
import de.intarsys.tools.locator.FileLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.reader.ReaderTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.Converter;
import de.intarsys.tools.string.ConverterException;
import de.intarsys.tools.string.StringTools;

/**
 * Tool class to ease handling of arguments.
 * 
 */
public final class ArgTools {

	public interface IBindingProcessor {
		public Object visitArgs(String path, IArgs args);

		public Object visitBinding(String path, IArgs args, IBinding binding);
	}

	public static final IFunctor<String> TO_STRING = new IFunctor<String>() {

		@Override
		public String perform(IFunctorCall call) throws FunctorException {
			Args args = (Args) call.getReceiver();
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
				IBinding binding = it.next();
				if (binding.getName() != null) {
					sb.append(binding.getName());
				} else {
					sb.append(i);
				}
				sb.append(" = ");
				sb.append(binding.getValue());
				sb.append("\n");
				i++;
			}
			return sb.toString();
		}

	};

	public static final Object UNDEFINED = new Object();

	/**
	 * Apply the given {@link Function} recursively to all leaf values
	 * (non-args) in
	 * {@code args}. {@code args} is modified to contain the results of the
	 * function.
	 * 
	 * @param args
	 * @param function
	 * @return The modified {@code args} object.
	 */
	public static IArgs applyDeep(IArgs args, Function<IBinding, Object> function) {
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.isDefined()) {
				Object value = binding.getValue();
				if (value instanceof IArgs) {
					applyDeep((IArgs) value, function);
				} else {
					Object newValue = function.apply(binding);
					if (newValue != value && newValue != UNDEFINED) {
						binding.setValue(newValue);
					}
				}
			}
		}
		return args;
	}

	protected static Object basicGet(IArgs args, int index, String name) {
		Object argsValue;
		if (name != null) {
			argsValue = args.get(name);
		} else {
			argsValue = args.get(index);
		}
		return argsValue;
	}

	protected static Object basicGet(IArgs args, String name) {
		try {
			if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
				int index = Integer.parseInt(name.trim());
				return args.get(index);
			} else {
				return args.get(name);
			}
		} catch (NumberFormatException e) {
			return args.get(name);
		}
	}

	protected static Object basicGetOrUndefined(IArgs args, String name) {
		try {
			if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
				int index = Integer.parseInt(name.trim());
				if (!args.isDefined(index)) {
					return UNDEFINED;
				}
				return args.get(index);
			} else {
				if (!args.isDefined(name)) {
					return UNDEFINED;
				}
				return args.get(name);
			}
		} catch (NumberFormatException e) {
			if (!args.isDefined(name)) {
				return UNDEFINED;
			}
			return args.get(name);
		}
	}

	protected static boolean basicIsDefined(IArgs args, int index, String name) {
		if (name != null) {
			return args.isDefined(name);
		} else {
			return args.isDefined(index);
		}
	}

	protected static void basicPut(IArgs args, int index, String name, Object otherValue) {
		if (name == null) {
			args.put(index, otherValue);
		} else {
			args.put(name, otherValue);
		}
	}

	protected static <T> T convert(Object value, Class<T> clazz) throws ConversionException {
		return ConverterRegistry.get().convert(value, clazz);
	}

	protected static <T> T convert(Object value, Class<T> clazz, Object defaultValue) {
		try {
			return convertStrict(value, clazz, defaultValue);
		} catch (ConversionException e) {
			// ignore
		}
		return (T) defaultValue;
	}

	protected static <T> T convertStrict(Object value, Class<T> clazz, Object defaultValue) throws ConversionException {
		T result = convert(value, clazz);
		if (result != null) {
			return result;
		}
		return (T) defaultValue;
	}

	public static IArgs createArgs() {
		return Args.create();
	}

	/**
	 * Expand recursively all string templates in <code>args</code> using the
	 * evaluator.
	 * <p>
	 * args is modified to contain the result of the expansion process and
	 * returned to ease call chaining.
	 * 
	 * @param args
	 * @param evaluator
	 * @return The modified args object.
	 */
	public static IArgs expandDeep(IArgs args, IStringEvaluator evaluator) {
		return applyDeep(args, binding -> StringEvaluatorTools.evaluate(evaluator, binding.getValue()));
	}

	/**
	 * Expand recursively all string templates in <code>args</code> binding for
	 * name using the evaluator.
	 * <p>
	 * args is modified to contain the result of the expansion process and
	 * returned to ease call chaining.
	 * 
	 * @param args
	 * @param evaluator
	 * @return The modified args object.
	 */
	public static IArgs expandDeep(IArgs args, String name, IStringEvaluator evaluator) {
		Object value = getPath(args, name);
		if (value instanceof IArgs) {
			expandDeep((IArgs) value, evaluator);
		} else if (value instanceof String) {
			try {
				Object newValue = evaluator.evaluate((String) value, Args.create());
				putPath(args, name, newValue);
			} catch (EvaluationException e) {
				//
			}
		} else {
			//
		}
		return args;
	}

	/**
	 * Convert the <code>args</code> to a single-level {@link IArgs}. This is
	 * done recursively, i.e. all IArgs substructures are converted, too. The
	 * result is a {@link IArgs} where the keys are path names.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure. .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * {
	 *   a -> "b"
	 *   x.i -> 12
	 *   x.j.last -> "nn"
	 * ]
	 * </pre>
	 * 
	 * 
	 * @param args
	 * @return The flat {@link IArgs} representation of the <code>args</code>
	 */
	public static IArgs flatten(IArgs args) {
		return flatten(args, null, Args.create());
	}

	public static IArgs flatten(IArgs args, String prefix, IArgs map) {
		int i = 0;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.isDefined()) {
				String key;
				if (binding.getName() != null) {
					key = binding.getName();
				} else {
					key = "" + i;
				}
				if (!StringTools.isEmpty(prefix)) {
					key = prefix + "." + key;
				}
				Object value = binding.getValue();
				if (value instanceof IArgs) {
					flatten((IArgs) value, key, map);
				} else {
					map.put(key, value);
				}
				i++;
			}
		}
		return map;
	}

	/**
	 * The argument value at <code>name</code>. If the
	 * argument value is not provided or not convertible,
	 * the value provided by the defaultSupplier is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link String}, {@link Object}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultSupplier
	 * @return The argument value at <code>name</code> as a {@link String}.
	 */
	public static <T> T get(IArgs args, String name, Class<T> clazz, Supplier<T> defaultSupplier) {
		if (args == null) {
			return defaultSupplier == null ? null : defaultSupplier.get();
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultSupplier == null ? null : defaultSupplier.get();
		}
		if (clazz.isInstance(optionValue)) {
			return (T) optionValue;
		}
		try {
			return convert(optionValue, clazz);
		} catch (ConversionException e) {
			return defaultSupplier == null ? null : defaultSupplier.get();
		}
	}

	/**
	 * The argument value at <code>name</code> as an {@link IArgs} instance. If
	 * the argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link IArgs}, {@link String}, {@link Map}
	 * and {@link List}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as an {@link IArgs}
	 *         instance.
	 */
	public static IArgs getArgs(IArgs args, String name, IArgs defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		return toArgs(optionValue);
	}

	/**
	 * The argument value at <code>name</code> as a boolean. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Boolean}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a <code>boolean</code>
	 */
	public static boolean getBool(IArgs args, String name, boolean defaultValue) {
		try {
			return getBoolStrict(args, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Synonym for getBool.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The result of getBool
	 */
	public static boolean getBoolean(IArgs args, String name, boolean defaultValue) {
		return getBool(args, name, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a boolean. If the argument
	 * value is not provided <code>defaultValue</code>is returned.
	 * 
	 * If the argument value is not compatible with "boolean", an {@link IllegalArgumentException} is raised.
	 * *
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a <code>boolean</code>
	 */
	public static boolean getBoolStrict(IArgs args, String name, boolean defaultValue) {
		Object optionValue = getPath(args, name);
		return toBoolean(name, optionValue, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a byte. If the argument value
	 * is not provided or not convertible, <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Number}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a byte.
	 */
	public static byte getByte(IArgs args, String name, byte defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}
		if (value instanceof String) {
			try {
				return (byte) Integer.parseInt(((String) value).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return convert(value, Byte.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a byte array. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a byte array.
	 */
	public static byte[] getByteArray(IArgs args, String name, byte[] defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		if (value instanceof String) {
			try {
				return Converter.asBytes((String) value);
			} catch (ConverterException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		if (value instanceof ILocator) {
			try {
				return LocatorTools.getBytes((ILocator) value);
			} catch (IOException e) {
				return defaultValue;
			}
		}
		if (value instanceof ILocatorSupport) {
			try {
				return LocatorTools.getBytes(((ILocatorSupport) value).getLocator());
			} catch (IOException e) {
				return defaultValue;
			}
		}
		return convert(value, byte[].class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a char. If the argument value
	 * is not provided or not convertible, <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Character}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a char.
	 */
	public static char getChar(IArgs args, String name, char defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof Character) {
			return ((Character) value).charValue();
		}
		if (value instanceof String) {
			String valueString = (String) value;
			if (valueString.length() > 0) {
				return valueString.charAt(0);
			}
		}
		return convert(value, Character.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a char[]. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link String}, char[]. <b>This one throws
	 * an IllegalArgumentException, if the value is not of type
	 * <code>String</code> or <code>char[]</code>.</b>
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @exception IllegalArgumentException
	 *                if value is not of type <code>String</code> or
	 *                <code>char[]</code>
	 * @return The argument value at <code>name</code> as a {@link String}.
	 */
	public static char[] getCharArray(IArgs args, String name, char[] defaultValue) throws IllegalArgumentException {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof char[]) {
			return (char[]) optionValue;
		}
		if (optionValue instanceof String) {
			return ((String) optionValue).toCharArray();
		}
		if (optionValue instanceof Secret) {
			try {
				return ((Secret) optionValue).getChars();
			} catch (GeneralSecurityException e) {
				throw new IllegalArgumentException(e);
			}
		}
		try {
			return convert(optionValue, char[].class);
		} catch (ConversionException e) {
			return defaultValue;
		}
	}

	/**
	 * The argument value at <code>name</code> as a {@link Class}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Boolean}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Class}.
	 */
	public static Class getClass(IArgs args, String name, Class defaultValue, ClassLoader classLoader) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Class) {
			return (Class) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			try {
				return ClassTools.createClass(optionString, Object.class, classLoader);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		return convert(optionValue, Class.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Class}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Class}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Class}.
	 */
	public static <T> Class<T> getClass(IArgs args, String name, Class<T> defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Class) {
			return (Class<T>) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			try {
				return ClassTools.createClass(optionString, null, null);
			} catch (ObjectCreationException e) {
				return defaultValue;
			}
		}
		return convert(optionValue, Class.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Color}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Color}.
	 */
	public static Color getColor(IArgs args, String name, Color defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Color) {
			return (Color) optionValue;
		}
		return convert(optionValue, Color.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Date}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Date}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Date}.
	 */
	public static Date getDate(IArgs args, String name, Date defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Date) {
			return (Date) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			// try UTC format
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'");
				LocalDateTime localDateTime = LocalDateTime.parse(optionString.trim(), formatter);
				return Date.from(localDateTime.atZone(java.time.ZoneId.of("UTC")).toInstant());
			} catch (DateTimeParseException e) {
				try {
					// try default format
					return DateFormat.getInstance().parse(optionString.trim());
				} catch (ParseException ex) {
					return defaultValue;
				}
			}
		}
		return convert(optionValue, Date.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as an {@link IDigest}.
	 * 
	 * @param args
	 * @param name
	 * @return The argument value at <code>name</code> as an {@link IDigest}.
	 * @throws IOException
	 */
	public static IDigest getDigest(IArgs args, String name) throws IOException {
		if (args == null) {
			return null;
		}
		Object optionValue = getPath(args, name);
		return DigestTools.createDigest(optionValue);
	}

	/**
	 * The argument value at <code>name</code> as a java enum instance. If the
	 * argument value is not provided or not valid the <code>defaultValue</code> is returned.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends Enum<T>> T getEnum(IArgs args, Class<T> meta, String name, T defaultValue) {
		try {
			return getEnumStrict(args, meta, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * The argument value at <code>name</code> as an {@link EnumItem}. If the
	 * argument value is not provided or not valid, the enumeration default value is returned.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends EnumItem> T getEnumItem(IArgs args, EnumMeta<T> meta, String name) {
		return getEnumItem(args, meta, name, meta.getDefault());
	}

	/**
	 * The argument value at <code>name</code> as a {@link EnumItem}. If the
	 * argument value is not provided or not convertible, the enumeration item
	 * with the id <code>defaultValue</code> is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link EnumItem}, {@link String}.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends EnumItem> T getEnumItem(IArgs args, EnumMeta<T> meta, String name, String defaultValue) {
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return meta.getItem(defaultValue);
		}
		if (optionValue instanceof EnumItem) {
			return (T) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			return meta.getItem(optionString.trim(), defaultValue);
		}
		return (T) convert(optionValue, meta.getEnumClazz(), meta.getItemOrDefault(defaultValue));
	}

	/**
	 * The argument value at <code>name</code> as a {@link EnumItem}. If the
	 * argument value is not provided or not convertible, the
	 * <code>defaultValue</code> is returned.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends EnumItem> T getEnumItem(IArgs args, EnumMeta<T> meta, String name, T defaultValue) {
		try {
			return getEnumItemStrict(args, meta, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * The argument value at <code>name</code> as a {@link EnumItem}. If the
	 * argument value is not provided or not convertible, the enumeration
	 * default value is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link EnumItem}, {@link String}.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends EnumItem> T getEnumItemStrict(IArgs args, EnumMeta<T> meta, String name) {
		return getEnumItemStrict(args, meta, name, meta.getDefault());
	}

	/**
	 * The argument value at <code>name</code> as a {@link EnumItem}. If the
	 * argument value is not provided or not convertible, the
	 * <code>defaultValue</code> is returned.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends EnumItem> T getEnumItemStrict(IArgs args, EnumMeta<T> meta, String name, T defaultValue) {
		Object optionValue = getPath(args, name);
		return toEnumItem(meta, name, optionValue, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a java enum instance. If the
	 * argument value is not provided the <code>defaultValue</code> is returned.
	 * 
	 * @param args
	 * @param meta
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link EnumItem}.
	 */
	public static <T extends Enum<T>> T getEnumStrict(IArgs args, Class<T> meta, String name, T defaultValue) {
		Object optionValue = getPath(args, name);
		return toEnum(meta, name, optionValue, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link File}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link File}, {@link String},
	 * {@link ILocator}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Date}.
	 */
	public static File getFile(IArgs args, String name, File defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof File) {
			return (File) value;
		}
		if (value instanceof String) {
			return new File(((String) value).trim());
		}
		if (value instanceof FileLocator) {
			return ((FileLocator) value).getFile();
		}
		if (value instanceof ILocator) {
			return new File(((ILocator) value).getPath());
		}
		return convert(value, File.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a float. If the argument value
	 * is not provided or not convertible, <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Number}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a float.
	 */
	public static float getFloat(IArgs args, String name, float defaultValue) {
		try {
			return getFloatStrict(args, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static float getFloatStrict(IArgs args, String name, float defaultValue) {
		Object value = getPath(args, name);
		return toFloat(name, value, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a int. If the argument value
	 * is not provided or not convertible, <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Number}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a int.
	 */
	public static int getInt(IArgs args, String name, int defaultValue) {
		try {
			return getIntStrict(args, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static int getIntStrict(IArgs args, String name, int defaultValue) {
		Object value = getPath(args, name);
		return toInt(name, value, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link List}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link List}.
	 */
	public static List getList(IArgs args, String name, List defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof List) {
			return (List) optionValue;
		}
		if (optionValue instanceof Collection) {
			return new ArrayList<>((Collection) optionValue);
		}
		if (optionValue instanceof String) {
			return Converter.asList((String) optionValue);
		}
		if (optionValue instanceof IArgs) {
			return toList((IArgs) optionValue);
		}
		if (optionValue instanceof Object[]) {
			return Arrays.asList((Object[]) optionValue);
		}
		return convert(optionValue, List.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link ILocator}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link ILocator}, {@link String},
	 * {@link File}
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @param factory
	 * @return The argument value at <code>name</code> as a {@link ILocator}.
	 */
	public static ILocator getLocator(IArgs args, String name, ILocator defaultValue, ILocatorFactory factory) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		try {
			return LocatorTools.createLocator(optionValue, factory, defaultValue);
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	/**
	 * The argument value at <code>name</code> as a
	 * {@link List}&lt;{@link ILocator}&gt;. If the argument value is not provided,
	 * <code>null</code> is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are {@link Collection} of {@link ILocator}, {@link String} and
	 * {@link File}.
	 * 
	 * @param args
	 * @param name
	 * @param factory
	 * @return The argument value at <code>name</code> as a {@link List}.
	 */
	@SuppressWarnings({ "java:S1168" })
	public static List<ILocator> getLocators(IArgs args, String name, ILocatorFactory factory) {
		if (args == null) {
			return null;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return null;
		}
		if (optionValue instanceof ILocator[]) {
			return Arrays.asList((ILocator[]) optionValue);
		}
		try {
			List<ILocator> locators = new ArrayList<>();
			if (optionValue instanceof Collection) {
				for (Iterator i = ((Collection) optionValue).iterator(); i.hasNext();) {
					Object candidate = i.next();
					ILocator locator = LocatorTools.createLocator(candidate, factory, null);
					if (locator != null) {
						locators.add(locator);
					}
				}
			} else if (optionValue instanceof Object[]) {
				Object[] values = (Object[]) optionValue;
				for (int i = 0; i < values.length; i++) {
					ILocator locator = LocatorTools.createLocator(values[i], factory, null);
					if (locator != null) {
						locators.add(locator);
					}
				}
			} else if (optionValue instanceof IArgs) {
				// args may be both indexed (collection of locators) and named
				// (flat
				// locator)
				Iterator<IBinding> values = ((IArgs) optionValue).bindings();
				while (values.hasNext()) {
					IBinding binding = values.next();
					if (binding.getName() == null) {
						ILocator locator = LocatorTools.createLocator(binding.getValue(), factory, null);
						if (locator != null) {
							locators.add(locator);
						}
					} else {
						break;
					}
				}
				if (locators.isEmpty()) {
					ILocator locator = LocatorTools.createLocator(optionValue, factory, null);
					if (locator != null) {
						locators.add(locator);
					}
				}
			} else {
				ILocator locator = LocatorTools.createLocator(optionValue, factory, null);
				if (locator != null) {
					locators.add(locator);
				}
			}
			return locators;
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	/**
	 * The argument value at <code>name</code> as a long. If the argument value
	 * is not provided or not convertible, <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Number}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a int.
	 */
	public static long getLong(IArgs args, String name, long defaultValue) {
		try {
			return getLongStrict(args, name, defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long getLongStrict(IArgs args, String name, long defaultValue) {
		Object value = getPath(args, name);
		return toLong(name, value, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Map}. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Map}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Map}.
	 */
	public static Map getMap(IArgs args, String name, Map defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Map) {
			return (Map) optionValue;
		}
		if (optionValue instanceof String) {
			return Converter.asMap((String) optionValue);
		}
		if (optionValue instanceof IArgs) {
			return toMap((IArgs) optionValue);
		}
		return convert(optionValue, Map.class, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Object}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Object}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Object}.
	 */
	public static Object getObject(IArgs args, String name, Object defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		return optionValue;
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend (and lazy create) the tree of {@link IArgs} objects and return
	 * the value in the leaf {@link IArgs} instance or <code>null</code>.
	 * 
	 * @param args
	 * @param path
	 * @return The argument value in args at path
	 */
	public static Object getPath(IArgs args, String path) {
		if (args == null) {
			return null;
		}
		if (StringTools.isEmpty(path) || ".".equals(path)) {
			return args;
		}
		String[] segments = path.split("\\.");
		int position = 0;
		while (position < segments.length - 1) {
			Object tempValue = basicGet(args, segments[position]);
			// we are not at a leaf yet - convert
			args = toArgs(tempValue);
			if (args == null) {
				return null;
			}
			position++;
		}
		return basicGet(args, segments[position]);
	}

	/**
	 * The argument value at <code>name</code> as a {@link Point2D}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link Point2D}, {@link String}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Point2D}.
	 */
	public static Point2D getPoint(IArgs args, String name, Point2D defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Point2D) {
			return (Point2D) optionValue;
		}
		if (optionValue instanceof String) {
			try {
				return GeometryTools.parsePoint((String) optionValue);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		return convert(optionValue, Point2D.class, defaultValue);
	}

	/**
	 * A strict version of {@link #getPoint(IArgs, String, Point2D)}
	 * 
	 * @param args
	 * @param name
	 * @param xName
	 * @param yName
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Point2D}.
	 */
	public static Point2D getPointStrict(IArgs args, String name, String xName, String yName, Point2D defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return getPointStrictXY(args, xName, yName, defaultValue);
		} else if (optionValue instanceof Point2D) {
			return (Point2D) optionValue;
		} else if (optionValue instanceof String) {
			return GeometryTools.parsePoint((String) optionValue);
		} else {
			try {
				return convertStrict(optionValue, Point2D.class, defaultValue);
			} catch (ConversionException e) {
				throw new IllegalArgumentException("'" + optionValue + "' not a valid point");
			}
		}
	}

	protected static Point2D getPointStrictXY(IArgs args, String xName, String yName, Point2D defaultValue) {
		Object optionXValue = getPath(args, xName);
		Object optionYValue = getPath(args, yName);
		if (optionXValue == null && optionYValue == null) {
			return defaultValue;
		}
		if (optionXValue == null) {
			throw new IllegalArgumentException("'" + xName + "' missing");
		}
		if (optionYValue == null) {
			throw new IllegalArgumentException("'" + yName + "' missing");
		}
		try {
			return new Point2D.Float(toFloat(xName, optionXValue, 0), toFloat(yName, optionYValue, 0));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("'" + optionXValue + "' or '" + optionYValue + "'not a valid point");
		}
	}

	/**
	 * The argument value at <code>name</code> as a Secret. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * 
	 * If the args contain plain text, the text is interpreted as a plaintext
	 * (unencoded) {@link Secret} and is simply hidden (see
	 * {@link Secret#hideTrimmed(char[])}).
	 * 
	 * Use this method if your code expects the secret to be in plaintext
	 * already.
	 * 
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, Secret, {@link String}, char[].
	 * 
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @exception IllegalArgumentException
	 * @return The argument value at <code>name</code> as a {@link Secret}.
	 */
	public static Secret getSecretHide(IArgs args, String name, Secret defaultValue) throws IllegalArgumentException {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		Secret secret = CryptoTools.createSecret(optionValue);
		if (CryptoTools.isEmpty(secret)) {
			return defaultValue;
		}
		return secret;
	}

	/**
	 * The argument value at <code>name</code> as a Secret. If the argument
	 * value is not provided or not convertible, <code>defaultValue</code>is
	 * returned.
	 * 
	 * If the args contain plain text, the text is interpreted as an encrypted
	 * (encoded) {@link Secret} and parsed (see {@link Secret#parse(String)}).
	 * 
	 * Use this method if your code expects the serialized secret to be
	 * explicitly encoded in naked secret syntax.
	 * 
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, Secret, {@link String}, char[].
	 * 
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @exception IllegalArgumentException
	 * @return The argument value at <code>name</code> as a {@link Secret}.
	 */
	public static Secret getSecretParse(IArgs args, String name, Secret defaultValue) throws IllegalArgumentException {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof char[]) {
			return Secret.parse(new String((char[]) optionValue));
		}
		if (optionValue instanceof String) {
			return Secret.parse((String) optionValue);
		}
		if (optionValue instanceof Secret) {
			return (Secret) optionValue;
		}
		return defaultValue;
	}

	/**
	 * The argument value at <code>name</code> as a {@link String}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * <p>
	 * This method performs the necessary casts and conversions. Supported input
	 * types are <code>null</code>, {@link String}, {@link Object}.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link String}.
	 */
	public static String getString(IArgs args, String name, String defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof String) {
			return (String) optionValue;
		}
		if (optionValue instanceof char[]) {
			return new String((char[]) optionValue);
		}
		if (optionValue instanceof Secret) {
			try {
				return ((Secret) optionValue).getString();
			} catch (GeneralSecurityException e) {
				throw new IllegalArgumentException(e);
			}
		}
		return convert(optionValue, String.class, String.valueOf(optionValue));
	}

	public static URL getUrlStrict(IArgs args, String name, URL defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		return toUrl(name, value, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> converted to clazz. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @param args
	 * @param name
	 * @param clazz
	 * @param defaultValue
	 * @return The argument value at <code>name</code> converted to clazz.
	 */
	public static <T> T getValue(IArgs args, String name, Class<T> clazz, Object defaultValue) {
		if (args == null) {
			return (T) defaultValue;
		}
		Object value = getPath(args, name);
		return convert(value, clazz, defaultValue);
	}

	/**
	 * Answer true if args has any defined binding.
	 * 
	 * @param args
	 * @return
	 */
	public static boolean hasDefinedBindings(IArgs args) {
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.isDefined()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend (and lazy create) the tree of {@link IArgs} objects and return
	 * true if all bindings in the path are defined.
	 * 
	 * @param args
	 * @param path
	 * @return true if all bindings are defined.
	 */
	public static boolean isDefined(IArgs args, String path) {
		if (StringTools.isEmpty(path) || ".".equals(path)) {
			return true;
		}
		String[] segments = path.split("\\.");
		int position = 0;
		String name;
		Object tempValue;
		while (position < segments.length - 1) {
			name = segments[position];
			tempValue = basicGetOrUndefined(args, name);
			args = toArgs(tempValue);
			if (args == null) {
				return false;
			}
			position++;
		}
		name = segments[position];
		tempValue = basicGetOrUndefined(args, name);
		return tempValue != UNDEFINED;
	}

	/**
	 * Answer true if args has (any) named binding.
	 * 
	 * @param args
	 * @return
	 */
	public static boolean isNamed(IArgs args) {
		for (IBinding binding : args) {
			if (binding.getName() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Create a new camel case argument name from <code>name</code> by prefixing
	 * with <code>prefix</code>.
	 * 
	 * @param prefix
	 * @param name
	 * @return The new argument name.
	 */
	public static String prefix(String prefix, String name) {
		if (prefix == null || prefix.length() == 0) {
			return name;
		}
		if (name == null) {
			return null;
		}
		return prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	/**
	 * Put all named top level entries in <code>other</code> into
	 * <code>args</code>.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param other
	 * @return The modified args object.
	 */
	public static IArgs putAll(IArgs args, IArgs other) {
		if (other == null) {
			return args;
		}
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.getName() != null) {
				args.put(binding.getName(), binding.getValue());
			}
		}
		return args;
	}

	/**
	 * Put all top level entries in <code>map</code> into <code>args</code>. The
	 * map keys are interpreted as a path expression ("." separated).
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param map
	 * @return The modified args object.
	 */
	public static IArgs putAll(IArgs args, Map map) {
		if (map == null) {
			return args;
		}
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			putPath(args, String.valueOf(entry.getKey()), entry.getValue());
		}
		return args;
	}

	/**
	 * Merge recursively all entries from <code>other</code> into args. If an
	 * {@link IBinding} in other is already contained in args, args is
	 * overwritten.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param other
	 * @return The modified args object.
	 */
	public static IArgs putAllDeep(IArgs args, IArgs other) {
		if (other == null) {
			return args;
		}
		int index = -1;
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			index++;
			putAllDeepBinding(args, index, binding);
		}
		return args;
	}

	/**
	 * Merge recursively all entries from <code>list</code> into args.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param list
	 * @return The modified args object.
	 */
	public static IArgs putAllDeep(IArgs args, List list) {
		if (list == null) {
			return args;
		}
		int i = 0;
		for (Iterator it = list.iterator(); it.hasNext(); i++) {
			Object listValue = it.next();
			Object argsValue = args.get(i);
			if (argsValue instanceof IArgs) {
				if (listValue instanceof Map) {
					putAllDeep((IArgs) argsValue, (Map) listValue);
				} else if (listValue instanceof List) {
					putAllDeep((IArgs) argsValue, (List) listValue);
				} else {
					args.put(i, listValue);
				}
			} else {
				args.put(i, toArgsValue(listValue));
			}
		}
		return args;
	}

	/**
	 * Merge recursively all entries from <code>map</code> into args.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param map
	 * @return The modified args object.
	 */
	public static IArgs putAllDeep(IArgs args, Map map) {
		if (map == null) {
			return args;
		}
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String name = String.valueOf(entry.getKey());
			Object mapValue = entry.getValue();
			Object argsValue = ArgTools.getPath(args, name);
			if (argsValue instanceof IArgs) {
				if (mapValue instanceof Map) {
					putAllDeep((IArgs) argsValue, (Map) mapValue);
				} else if (mapValue instanceof List) {
					putAllDeep((IArgs) argsValue, (List) mapValue);
				} else {
					putPath(args, name, mapValue);
				}
			} else {
				putPath(args, name, toArgsValue(mapValue));
			}
		}
		return args;
	}

	protected static void putAllDeepBinding(IArgs args, int index, IBinding binding) {
		if (!binding.isDefined()) {
			return;
		}
		String name = binding.getName();
		Object otherValue = binding.getValue();
		if ("+".equals(name)) {
			args.add(otherValue);
		} else {
			Object argsValue = basicGet(args, index, name);
			if (argsValue instanceof IArgs) {
				if (otherValue instanceof IArgs) {
					putAllDeep((IArgs) argsValue, (IArgs) otherValue);
				} else {
					basicPut(args, index, name, otherValue);
				}
			} else {
				basicPut(args, index, name, otherValue);
			}
		}
	}

	/**
	 * Put all named top level entries in <code>other</code> that are not
	 * already defined into <code>args</code>.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param other
	 * @return The modified args object.
	 */
	public static IArgs putAllIfAbsent(IArgs args, IArgs other) {
		if (other == null) {
			return args;
		}
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.getName() != null && !args.isDefined(binding.getName())) {
				args.put(binding.getName(), binding.getValue());
			}
		}
		return args;
	}

	/**
	 * Put all top level entries in <code>map</code> that are not already
	 * defined into <code>args</code>. The map keys are interpreted as a path
	 * expression ("." separated).
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param map
	 * @return The modified args object.
	 */
	public static IArgs putAllIfAbsent(IArgs args, Map map) {
		if (map == null) {
			return args;
		}
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = String.valueOf(entry.getKey());
			putPathIfAbsent(args, key, entry.getValue());
		}
		return args;
	}

	/**
	 * Merge recursively all named entries from <code>other</code> that are not
	 * already defined in args into args.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param other
	 * @return The modified args object.
	 */
	public static IArgs putAllIfAbsentDeep(IArgs args, IArgs other) {
		if (other == null) {
			return args;
		}
		int index = -1;
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			index++;
			if (binding.isDefined()) {
				String name = binding.getName();
				Object otherValue = binding.getValue();
				if (basicIsDefined(args, index, name)) {
					Object argsValue = basicGet(args, index, name);
					if (argsValue instanceof IArgs && otherValue instanceof IArgs) {
						putAllIfAbsentDeep((IArgs) argsValue, (IArgs) otherValue);
					}
				} else {
					basicPut(args, index, name, otherValue);
				}
			}
		}
		return args;
	}

	/**
	 * Add a String based definition to args. "definition" is of the form
	 * "x.y.z=b" (the key is a "." separated path).
	 * 
	 * @param args
	 * @param definition
	 */
	public static void putDefinition(IArgs args, String definition) {
		if (StringTools.isEmpty(definition)) {
			return;
		}
		Reader r = new StringReader(definition);
		try {
			while (true) {
				Map.Entry<String, String> entry = ReaderTools.readEntry(r, ';');
				if (entry == null) {
					break;
				}
				if (entry.getKey() != null) {
					putPath(args, entry.getKey(), entry.getValue());
				}
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("invalid definition '" + definition + "'");
		}
	}

	/**
	 * Shovel arguments from other to args, mapping the argument names from
	 * otherNames to argsNames.
	 * 
	 * @param args
	 * @param other
	 * @param argsNames
	 * @param otherNames
	 * 
	 * @return The modified input parameter args.
	 */
	public static IArgs putMapped(IArgs args, IArgs other, String[] argsNames, String[] otherNames) {
		if (other == null) {
			return args;
		}
		for (int i = 0; i < otherNames.length; i++) {
			putPath(args, argsNames[i], getPath(other, otherNames[i]));
		}
		return args;
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend (and lazy create) the tree of {@link IArgs} objects and set
	 * <code>value</code> in the leaf {@link IArgs} instance.
	 * 
	 * @param args
	 * @param path
	 * @param value
	 * @return The modified input parameter args.
	 */
	public static IArgs putPath(IArgs args, String path, Object value) {
		String[] segments = path.split("\\."); //$NON-NLS-1$
		int position = 0;
		String name;
		int index;
		while (position < segments.length - 1) {
			name = segments[position];
			Object tempValue;
			try {
				if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
					index = Integer.parseInt(name.trim());
					tempValue = args.get(index);
					if (!(tempValue instanceof IArgs)) {
						tempValue = toArgs(tempValue);
						if (tempValue == null) {
							tempValue = Args.create();
						}
						args.put(index, tempValue);
					}
				} else {
					tempValue = args.get(name);
					if (!(tempValue instanceof IArgs)) {
						tempValue = toArgs(tempValue);
						if (tempValue == null) {
							tempValue = Args.create();
						}
						args.put(name, tempValue);
					}
				}
			} catch (NumberFormatException e) {
				tempValue = args.get(name);
				if (!(tempValue instanceof IArgs)) {
					tempValue = toArgs(tempValue);
					if (tempValue == null) {
						tempValue = Args.create();
					}
					args.put(name, tempValue);
				}
			}
			args = (IArgs) tempValue;
			position++;
		}
		name = segments[position];
		try {
			if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
				index = Integer.parseInt(name.trim());
				args.put(index, value);
			} else {
				args.put(name, value);
			}
		} catch (NumberFormatException e) {
			args.put(name, value);
		}
		return args;
	}

	/**
	 * See {@link #putPathIfAbsent(IArgs, String, Supplier)}
	 * 
	 * @param args
	 * @param path
	 * @param value
	 * @return
	 */
	public static IArgs putPathIfAbsent(IArgs args, String path, Object value) {
		return putPathIfAbsent(args, path, () -> value);
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend the tree of {@link IArgs} objects and set <code>value</code> in
	 * the leaf {@link IArgs} instance if and only if a binding for the path is
	 * not yet present.
	 * 
	 * @param args
	 * @param path
	 * @param supplier
	 * @return The modified input parameter args.
	 */
	public static IArgs putPathIfAbsent(IArgs args, String path, Supplier<?> supplier) {
		String[] segments = path.split("\\."); //$NON-NLS-1$
		int position = 0;
		String name;
		int index;
		while (position < segments.length - 1) {
			name = segments[position];
			Object tempValue;
			try {
				index = Integer.parseInt(name.trim());
				tempValue = args.get(index);
				if (!(tempValue instanceof IArgs)) {
					tempValue = toArgs(tempValue);
					if (tempValue == null) {
						tempValue = Args.create();
					}
					args.put(index, tempValue);
				}
			} catch (NumberFormatException e) {
				tempValue = args.get(name);
				if (!(tempValue instanceof IArgs)) {
					tempValue = toArgs(tempValue);
					if (tempValue == null) {
						tempValue = Args.create();
					}
					args.put(name, tempValue);
				}
			}
			args = (IArgs) tempValue;
			position++;
		}
		name = segments[position];
		try {
			index = Integer.parseInt(name.trim());
			if (!args.isDefined(index)) {
				Object newValue = supplier.get();
				if (newValue != UNDEFINED) {
					args.put(index, newValue);
				}
			}
		} catch (NumberFormatException e) {
			if (!args.isDefined(name)) {
				Object newValue = supplier.get();
				if (newValue != UNDEFINED) {
					args.put(name, newValue);
				}
			}
		}
		return args;
	}

	/**
	 * Cast or convert any input <code>value</code> to an {@link IArgs}.
	 * 
	 * String representations will be parsed.
	 * 
	 * @param value
	 * @return The {@link IArgs} created from <code>value</code>.
	 */
	public static IArgs toArgs(Object value) {
		if (value == UNDEFINED) {
			return null;
		}
		if (value == null) {
			return null;
		}
		if (value instanceof IArgs) {
			return (IArgs) value;
		}
		if (value instanceof String) {
			value = Converter.asMap((String) value);
		}
		if (value instanceof Map) {
			return new Args((Map) value);
		}
		if (value instanceof List) {
			return new Args((List) value);
		}
		return convert(value, IArgs.class, Args.create());
	}

	/**
	 * Create a parseable representation of args.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure .
	 * 
	 * <pre>
	 * {
	 *   a = "b;b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "n\"n"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * a="b;b";x.i="12";x.j.last="n\"n"
	 * </pre>
	 * 
	 * @param args
	 * @return
	 */
	public static String toArgString(IArgs args) {
		Map<String, ?> map = ArgTools.toMapDeepFlat(args);
		StringWriter writer = new StringWriter();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			writer.append(entry.getKey());
			writer.append("=");
			if (entry.getValue() == null) {
				writer.append("");
			} else {
				String value;
				try {
					value = ConverterRegistry.get().convert(entry.getValue(), String.class);
				} catch (ConversionException e) {
					value = entry.getValue().toString();
				}
				writer.append(StringTools.quote(value));
			}
			writer.append(";");
		}
		return writer.toString();
	}

	/**
	 * Cast or convert compatible container <code>value</code> to {@link IArgs}.
	 * 
	 * Everything but {@link IArgs}, {@link Map} and {@link List} will be left
	 * alone.
	 * 
	 * @param value
	 * @return The {@link IArgs} created from <code>value</code>.
	 */
	public static Object toArgsValue(Object value) {
		if (value == UNDEFINED) {
			return null;
		}
		if (value instanceof Map) {
			return new Args((Map) value);
		}
		if (value instanceof List) {
			return new Args((List) value);
		}
		return value;
	}

	protected static boolean toBoolean(String name, Object value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		if (value instanceof String) {
			String optionString = (String) value;
			try {
				return Converter.asBooleanStrict(optionString, defaultValue);
			} catch (ConverterException e) {
				throw new IllegalArgumentException("cannot convert '" + name + "' to boolean");
			}
		}
		try {
			return convertStrict(value, Boolean.class, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to boolean");
		}
	}

	protected static <T extends Enum<T>> T toEnum(Class<T> meta, String name, Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (meta.isInstance(value)) {
			return (T) value;
		}
		if (value instanceof String) {
			if (StringTools.isEmpty((String) value)) {
				return null;
			}
			String optionString = (String) value;
			return Enum.valueOf(meta, optionString);
		}
		try {
			return convertStrict(value, meta, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to enum");
		}
	}

	protected static <T extends EnumItem> T toEnumItem(EnumMeta<T> meta, String name, Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof EnumItem) {
			return (T) value;
		}
		if (value instanceof String) {
			String optionString = (String) value;
			return meta.getItemStrict(optionString.trim(), defaultValue);
		}
		try {
			return (T) convertStrict(value, meta.getEnumClazz(), defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to enum");
		}
	}

	protected static float toFloat(String name, Object value, float defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}
		if (value instanceof String) {
			String stringValue = (String) value;
			if (stringValue.indexOf("%") != -1) { //$NON-NLS-1$
				try {
					Number result = NumberFormat.getPercentInstance().parse(stringValue.trim());
					return result.floatValue();
				} catch (Exception e) {
					throw new IllegalArgumentException("cannot convert '" + name + "' to percent");
				}
			}
			try {
				return Float.parseFloat(stringValue.trim());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cannot convert '" + name + "' to float");
			}
		}
		try {
			return convertStrict(value, Float.class, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to float");
		}
	}

	protected static int toInt(String name, Object value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt(((String) value).trim());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cannot convert '" + name + "' to int");
			}
		}
		try {
			return convertStrict(value, Integer.class, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to int");
		}
	}

	/**
	 * Convert the <code>args</code> to a corresponding java container object.
	 * This is done recursively, i.e. all IArgs substructures are converted to
	 * java containers.
	 * 
	 * If the args are named, a {@link Map} is returned, if they are indexed, a
	 * {@link List} is returned.
	 * 
	 * @param args
	 * @return The java container representation of the <code>args</code>
	 */
	public static Object toJavaDeep(IArgs args) {
		Map resultMap = new LinkedHashMap<>(args == null ? 0 : args.size());
		boolean named = false;
		if (args != null) {
			int index = -1;
			Iterator<IBinding> it = args.bindings();
			while (it.hasNext()) {
				IBinding binding = it.next();
				index++;
				named = named || binding.getName() != null;
				toJavaDeepBinding(resultMap, index, binding);
			}
		}
		if (named || resultMap.isEmpty()) {
			return resultMap;
		} else {
			return new ArrayList(resultMap.values());
		}
	}

	protected static void toJavaDeepBinding(Map resultMap, int index, IBinding binding) {
		if (!binding.isDefined()) {
			return;
		}
		Object value = binding.getValue();
		if (value instanceof IArgs) {
			value = toJavaDeep((IArgs) value);
		}
		String key = binding.getName();
		if (key == null) {
			key = "" + index;
		}
		resultMap.put(key, value);
	}

	/**
	 * Convert the <code>args</code> to a {@link List}.
	 * 
	 * @param args
	 * @return The {@link List} representation of the <code>args</code>
	 */
	public static List toList(IArgs args) {
		List result = new ArrayList();
		if (args != null) {
			Iterator<IBinding> it = args.bindings();
			while (it.hasNext()) {
				IBinding binding = it.next();
				if (binding.isDefined()) {
					result.add(binding.getValue());
				}
			}
		}
		return result;
	}

	protected static long toLong(String name, Object value, long defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		if (value instanceof String) {
			try {
				return Long.parseLong(((String) value).trim());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cannot convert '" + name + "' to long");
			}
		}
		try {
			return convertStrict(value, Long.class, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to long");
		}
	}

	/**
	 * Convert the <code>args</code> to a {@link Map}. This is done 1 level
	 * deep, i.e. the immediate children of args are now members of the map. If
	 * args contains nested args, these are left alone.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure and [] a {@link Map}
	 * .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * [
	 *   a -> "b"
	 *   x -> {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * ]
	 * </pre>
	 * 
	 * 
	 * 
	 * 
	 * @param args
	 * @return The {@link Map} representation of the <code>args</code>
	 */
	public static Map toMap(IArgs args) {
		int i = 0;
		Map result = new HashMap();
		if (args != null) {
			Iterator<IBinding> it = args.bindings();
			while (it.hasNext()) {
				IBinding binding = it.next();
				if (binding.isDefined()) {
					Object value = binding.getValue();
					String key;
					if (binding.getName() != null) {
						key = binding.getName();
					} else {
						key = "" + i;
					}
					result.put(key, value);
					i++;
				}
			}
		}
		return result;
	}

	/**
	 * Convert the <code>args</code> to a {@link Map}. This is done recursively,
	 * i.e. all IArgs substructures are converted to maps. The result is a
	 * nested map as well.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure and [] a {@link Map}
	 * .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * [
	 *   a -> "b"
	 *   x -> [
	 *     i -> 12
	 *     j -> [
	 *       last -> "nn"
	 *     ]
	 *   ]
	 * ]
	 * </pre>
	 * 
	 * 
	 * @param args
	 * @return The {@link Map} representation of the <code>args</code>
	 */
	public static Map toMapDeep(IArgs args) {
		int i = 0;
		Map result = new HashMap();
		if (args != null) {
			Iterator<IBinding> it = args.bindings();
			while (it.hasNext()) {
				IBinding binding = it.next();
				if (binding.isDefined()) {
					Object value = binding.getValue();
					if (value instanceof IArgs) {
						value = toMapDeep((IArgs) value);
					}
					String key;
					if (binding.getName() != null) {
						key = binding.getName();
					} else {
						key = "" + i;
					}
					result.put(key, value);
					i++;
				}
			}
		}
		return result;
	}

	/**
	 * Convert the <code>args</code> to a {@link Map}. This is done recursively,
	 * i.e. all IArgs substructures are converted, too. The result is a map
	 * where the keys are path names.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure and [] a {@link Map}
	 * .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * [
	 *   a -> "b"
	 *   x.i -> 12
	 *   x.j.last -> "nn"
	 * ]
	 * </pre>
	 * 
	 * 
	 * @param args
	 * @return The {@link Map} representation of the <code>args</code>
	 */
	public static Map<String, Object> toMapDeepFlat(IArgs args) {
		return toMapDeepFlat(args, null, new HashMap<>());
	}

	/**
	 * Convert the <code>args</code> to a {@link Map}. This is done recursively,
	 * i.e. all IArgs substructures are converted, too. The result is a map
	 * where the keys are path names.
	 * 
	 * Example, where {} denotes an {@link IArgs} structure and [] a {@link Map}
	 * .
	 * 
	 * <pre>
	 * {
	 *   a = "b"
	 *   x = {
	 *     i = 12
	 *     j = {
	 *       last = "nn"
	 *     }
	 *   }
	 * }
	 * </pre>
	 * 
	 * will result in
	 * 
	 * <pre>
	 * [
	 *   a -> "b"
	 *   x.i -> 12
	 *   x.j.last -> "nn"
	 * ]
	 * </pre>
	 * 
	 * 
	 * @param args
	 * @return The {@link Map} representation of the <code>args</code>
	 */
	public static Map<String, Object> toMapDeepFlat(IArgs args, String prefix, Map<String, Object> map) {
		int i = 0;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.isDefined()) {
				String key;
				if (binding.getName() != null) {
					key = binding.getName();
				} else {
					key = "" + i;
				}
				if (!StringTools.isEmpty(prefix)) {
					key = prefix + "." + key;
				}
				Object value = binding.getValue();
				if (value instanceof IArgs) {
					toMapDeepFlat((IArgs) value, key, map);
				} else {
					map.put(key, value);
				}
				i++;
			}
		}
		return map;
	}

	/**
	 * Create a printable {@link String} for <code>args</code>.
	 * 
	 * @param args
	 * @param prefix
	 * @return The printed representation of args
	 */
	public static String toPrintString(IArgs args, String prefix) {
		return new ArgsPrinter().toPrintString(args, prefix);
	}

	/**
	 * Create a printable {@link String} for <code>args</code>.
	 * 
	 * @param args
	 * @param prefix
	 * @return The printed representation of args
	 */
	public static String toPrintString(IArgs args, String prefix, String... hide) {
		ArgsPrinter printer = new ArgsPrinter();
		for (String temp : hide) {
			printer.hide(temp);
		}
		return printer.toPrintString(args, prefix);
	}

	public static URL toUrl(String name, Object value, URL defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof URL) {
			return (URL) value;
		}
		if (value instanceof String) {
			try {
				return new URL(((String) value).trim());
			} catch (MalformedURLException ex) {
				throw new IllegalArgumentException("cannot convert '" + name + "' to URL", ex);
			}
		}
		try {
			return convertStrict(value, URL.class, defaultValue);
		} catch (ConversionException e) {
			throw new IllegalArgumentException("cannot convert '" + name + "' to URL");
		}
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend the tree of {@link IArgs} objects and undefine the leave name.
	 * 
	 * @param args
	 * @param path
	 */
	public static void undefinePath(IArgs args, String path) {
		if (StringTools.isEmpty(path)) {
			return;
		}
		String name;
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex > 0) {
			name = path.substring(lastIndex + 1);
			args = toArgs(getPath(args, path.substring(0, lastIndex)));
			if (args == null) {
				return;
			}
		} else {
			name = path;
		}
		try {
			int index = Integer.parseInt(name.trim());
			args.undefine(index);
		} catch (NumberFormatException e) {
			args.undefine(name);
		}
	}

	/**
	 * For all named argument bindings perform the {@link IBindingProcessor}.
	 * This method performs a depth first enumeration and call the binding
	 * processor for each leaf element in the argument tree.
	 * 
	 * todo review: why only named?
	 * 
	 * @param prefix
	 * @param args
	 * @param processor
	 */
	public static void visitNamedBindings(String prefix, IArgs args, IBindingProcessor processor) {
		processor.visitArgs(prefix, args);
		int index = 0;
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			String name = binding.getName();
			if (name == null) {
				// change by TPI: process with index as name
				name = String.valueOf(index);
			}
			Object value = binding.getValue();
			if (value instanceof IArgs) {
				visitNamedBindings(StringTools.pathAppend(prefix, ".", name), (IArgs) value, processor);
			} else {
				processor.visitBinding(prefix, args, binding);
			}
			index++;
		}
	}

	private ArgTools() {
	}
}
