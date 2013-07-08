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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.digest.DigestTools;
import de.intarsys.tools.digest.IDigest;
import de.intarsys.tools.encoding.Base64;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.locator.FileLocator;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorFactory;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.string.Converter;
import de.intarsys.tools.string.StringTools;

/**
 * Tool class to ease handling of arguments.
 * 
 */
public class ArgTools {

	public interface IBindingProcessor {
		public Object visitArgs(String path, IArgs args);

		public Object visitBinding(String path, IArgs args, IBinding binding);
	}

	static private int nesting = 0;

	public static final IFunctor<String> toString = new IFunctor<String>() {

		@Override
		public String perform(IFunctorCall call)
				throws FunctorInvocationException {
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

	private static Set visited;

	protected static <T> T convert(Object value, Class<T> clazz)
			throws ConversionException {
		return ConverterRegistry.get().convert(value, clazz);
	}

	protected static <T> T convert(Object value, Class<T> clazz,
			Object defaultValue) {
		try {
			T result = convert(value, clazz);
			if (result != null) {
				return result;
			}
		} catch (ConversionException e) {
			// ignore
		}
		return (T) defaultValue;
	}

	static public IArgs createArgs() {
		return Args.create();
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
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Boolean) {
			return ((Boolean) optionValue).booleanValue();
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			return Converter.asBoolean(optionString, defaultValue);
		}
		return convert(optionValue, Boolean.class, defaultValue);
	}

	/**
	 * Synonym for getBool.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The result of getBool
	 */
	public static boolean getBoolean(IArgs args, String name,
			boolean defaultValue) {
		return getBool(args, name, defaultValue);
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
				return Byte.parseByte(((String) value).trim());
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
	public static byte[] getByteArray(IArgs args, String name,
			byte[] defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof byte[]) {
			return (byte[]) value;
		}
		if (value instanceof String) {
			return Base64.decode((String) value);
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
				return LocatorTools.getBytes(((ILocatorSupport) value)
						.getLocator());
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
	 * types are <code>null</code>, {@link String}, char[]. <b>Unlike the other
	 * conversion methods, this one throws an IllegalArgumentException, if the
	 * value is not of type <code>String</code> or <code>char[]</code>.</b>
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @exception IllegalArgumentException
	 *                if value is not of type <code>String</code> or
	 *                <code>char[]</code>
	 * @return The argument value at <code>name</code> as a {@link String}.
	 */
	public static char[] getCharArray(IArgs args, String name,
			char[] defaultValue) throws IllegalArgumentException {
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
	public static Class getClass(IArgs args, String name, Class defaultValue,
			ClassLoader classLoader) {
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
				return ClassTools.createClass(optionString, Object.class,
						classLoader);
			} catch (Exception e) {
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
		if (optionValue instanceof String) {
			try {
				return Color.decode(((String) optionValue).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
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
			try {
				return DateFormat.getInstance().parse(optionString.trim());
			} catch (ParseException e) {
				return defaultValue;
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
	public static <T extends EnumItem> T getEnumItem(IArgs args,
			EnumMeta<T> meta, String name) {
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
	public static <T extends EnumItem> T getEnumItem(IArgs args,
			EnumMeta<T> meta, String name, String defaultValue) {
		if (args == null) {
			return meta.getItemOrDefault(defaultValue);
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return meta.getItemOrDefault(defaultValue);
		}
		if (optionValue instanceof EnumItem) {
			return (T) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			return meta.getItemOrDefault(optionString.trim());
		}
		return (T) convert(optionValue, meta.getEnumClazz(),
				meta.getItemOrDefault(defaultValue));
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
	public static <T extends EnumItem> T getEnumItem(IArgs args,
			EnumMeta<T> meta, String name, T defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof EnumItem) {
			return (T) optionValue;
		}
		if (optionValue instanceof String) {
			String optionString = (String) optionValue;
			return meta.getItemOrDefault(optionString.trim());
		}
		return (T) convert(optionValue, meta.getEnumClazz(), defaultValue);
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
			return new File(((ILocator) value).getFullName());
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
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}
		if (value instanceof String) {
			String stringValue = (String) value;
			if (stringValue.indexOf("%") != -1) { //$NON-NLS-1$
				try {
					Number result = NumberFormat.getPercentInstance().parse(
							stringValue.trim());
					return result.floatValue();
				} catch (ParseException e) {
					// todo log warning
					return defaultValue;
				}
			}
			try {
				return Float.parseFloat(stringValue.trim());
			} catch (NumberFormatException e) {
				// todo log warning
				return defaultValue;
			}
		}
		return convert(value, Float.class, defaultValue);
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
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt(((String) value).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return convert(value, Integer.class, defaultValue);
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
	public static ILocator getLocator(IArgs args, String name,
			ILocator defaultValue, ILocatorFactory factory) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		return LocatorTools.createLocator(optionValue, factory, defaultValue);
	}

	/**
	 * The argument value at <code>name</code> as a List<ILocator>. If the
	 * argument value is not provided, <code>null</code>is returned.
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
	public static List<ILocator> getLocators(IArgs args, String name,
			ILocatorFactory factory) {
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
		List<ILocator> locators = new ArrayList<ILocator>();
		if (optionValue instanceof Collection) {
			for (Iterator i = ((Collection) optionValue).iterator(); i
					.hasNext();) {
				Object candidate = i.next();
				ILocator locator = LocatorTools.createLocator(candidate,
						factory, null);
				if (locator != null) {
					locators.add(locator);
				}
			}
		} else if (optionValue instanceof Object[]) {
			Object[] values = (Object[]) optionValue;
			for (int i = 0; i < values.length; i++) {
				ILocator locator = LocatorTools.createLocator(values[i],
						factory, null);
				if (locator != null) {
					locators.add(locator);
				}
			}
		} else if (optionValue instanceof IArgs) {
			// args may be both indexed (collection of locators) and named (flat
			// locator)
			Iterator<IBinding> values = ((IArgs) optionValue).bindings();
			while (values.hasNext()) {
				IBinding binding = values.next();
				if (binding.getName() == null) {
					ILocator locator = LocatorTools.createLocator(
							binding.getValue(), factory, null);
					if (locator != null) {
						locators.add(locator);
					}
				} else {
					break;
				}
			}
			if (locators.isEmpty()) {
				ILocator locator = LocatorTools.createLocator(optionValue,
						factory, null);
				if (locator != null) {
					locators.add(locator);
				}
			}
		} else {
			ILocator locator = LocatorTools.createLocator(optionValue, factory,
					null);
			if (locator != null) {
				locators.add(locator);
			}
		}
		return locators;
	}

	/**
	 * The argument value at <code>name</code> as a {@link Level}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Level}.
	 */
	public static Level getLogLevel(IArgs args, String name, Level defaultValue) {
		if (args == null) {
			return defaultValue;
		}
		Object optionValue = getPath(args, name);
		if (optionValue == null) {
			return defaultValue;
		}
		if (optionValue instanceof Level) {
			return (Level) optionValue;
		}
		if (optionValue instanceof String) {
			try {
				return Level.parse(((String) optionValue).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return convert(optionValue, Level.class, defaultValue);
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
		if (args == null) {
			return defaultValue;
		}
		Object value = getPath(args, name);
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		if (value instanceof String) {
			try {
				return Long.parseLong(((String) value).trim());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return convert(value, Long.class, defaultValue);
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
	static public Object getPath(IArgs args, String path) {
		String[] segments = path.split("\\.");
		int position = 0;
		String name;
		int index;
		while (position < segments.length - 1) {
			name = segments[position];
			Object tempValue;
			try {
				index = Integer.parseInt(name.trim());
				tempValue = args.get(index);
			} catch (NumberFormatException e) {
				tempValue = args.get(name);
			}
			args = toArgs(tempValue);
			if (args == null) {
				return null;
			}
			position++;
		}
		name = segments[position];
		try {
			index = Integer.parseInt(name.trim());
			return args.get(index);
		} catch (NumberFormatException e) {
			return args.get(name);
		}
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
			String optionString = (String) optionValue;
			String[] coords = optionString.trim().split("[x*@]"); //$NON-NLS-1$
			if (coords == null || coords.length != 2) {
				return defaultValue;
			}
			try {
				float x = Float.parseFloat(coords[0]);
				float y = Float.parseFloat(coords[1]);
				return new Point2D.Float(x, y);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return convert(optionValue, Point2D.class, defaultValue);
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
		return convert(optionValue, String.class, String.valueOf(optionValue));
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
	public static <T> T getValue(IArgs args, String name, Class<T> clazz,
			Object defaultValue) {
		if (args == null) {
			return (T) defaultValue;
		}
		Object value = getPath(args, name);
		return convert(value, clazz, defaultValue);
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
	static public boolean isDefined(IArgs args, String path) {
		String[] segments = path.split("\\.");
		int position = 0;
		String name;
		int index;
		while (position < segments.length - 1) {
			name = segments[position];
			Object tempValue;
			try {
				index = Integer.parseInt(name.trim());
				if (!args.isDefined(index)) {
					return false;
				}
				tempValue = args.get(index);
			} catch (NumberFormatException e) {
				if (!args.isDefined(name)) {
					return false;
				}
				tempValue = args.get(name);
			}
			args = toArgs(tempValue);
			if (args == null) {
				return false;
			}
			position++;
		}
		name = segments[position];
		try {
			index = Integer.parseInt(name.trim());
			return args.isDefined(index);
		} catch (NumberFormatException e) {
			return args.isDefined(name);
		}
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
		return prefix + Character.toUpperCase(name.charAt(0))
				+ name.substring(1);
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
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			putPath(args, String.valueOf(entry.getKey()), entry.getValue());
		}
		return args;
	}

	/**
	 * Merge recursively all named entries from <code>other</code> into args.
	 * <p>
	 * args is modified to contain the result of the merge process and returned
	 * to ease call chaining.
	 * 
	 * @param args
	 * @param other
	 * @return The modified args object.
	 */
	public static IArgs putAllDeep(IArgs args, IArgs other) {
		int index = -1;
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			index++;
			Object argsValue;
			if (binding.getName() != null) {
				argsValue = args.get(binding.getName());
			} else {
				argsValue = args.get(index);
			}
			Object otherValue = binding.getValue();
			if (argsValue instanceof IArgs && otherValue instanceof IArgs) {
				putAllDeep((IArgs) argsValue, (IArgs) otherValue);
			} else {
				args.put(binding.getName(), binding.getValue());
			}
		}
		return args;
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
		for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = String.valueOf(entry.getKey());
			if (!args.isDefined(key)) {
				putPath(args, key, entry.getValue());
			}
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
		for (Iterator<IBinding> it = other.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			if (binding.getName() != null) {
				Object argsValue = args.get(binding.getName());
				Object otherValue = binding.getValue();
				if (argsValue instanceof IArgs && otherValue instanceof IArgs) {
					putAllIfAbsentDeep((IArgs) argsValue, (IArgs) otherValue);
				} else {
					if (!args.isDefined(binding.getName())) {
						args.put(binding.getName(), binding.getValue());
					}
				}
			}
		}
		return args;
	}

	/**
	 * Add a String based definition to args. "definition" is of the form
	 * "x.y.z=b" or "x.y.z:b" (the key is a "." separated path).
	 * 
	 * @param args
	 * @param definition
	 */
	static public void putDefinition(IArgs args, String definition) {
		if (definition == null) {
			return;
		}
		String[] split = definition.split("[\\:\\=]", 2);
		if (split.length > 1) {
			putPath(args, split[0], split[1]);
		} else {
			putPath(args, split[0], true);
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
	public static IArgs putMapped(IArgs args, IArgs other, String[] argsNames,
			String[] otherNames) {
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
	static public IArgs putPath(IArgs args, String path, Object value) {
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
			args.put(index, value);
		} catch (NumberFormatException e) {
			args.put(name, value);
		}
		return args;
	}

	/**
	 * Cast or convert <code>value</code> to an {@link IArgs}.
	 * 
	 * @param value
	 * @return The {@link IArgs} created from <code>value</code>.
	 */
	public static IArgs toArgs(Object value) {
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
			IArgs args = Args.create();
			putAll(args, (Map) value);
			return args;
		}
		if (value instanceof List) {
			return new Args((List) value);
		}
		return convert(value, IArgs.class, Args.create());
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
	static public Map toMapDeep(IArgs args) {
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
		return toMapDeepFlat(args, null, new HashMap<String, Object>());
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
	static public Map<String, Object> toMapDeepFlat(IArgs args, String prefix,
			Map<String, Object> map) {
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
	synchronized public static String toString(IArgs args, String prefix) {
		if (visited == null) {
			visited = new HashSet();
			nesting = 0;
		}
		if (visited.contains(args)) {
			return "...recursive...";
		}
		if (nesting == 5) {
			return "...nested to deep...";
		}
		visited.add(args);
		nesting++;
		try {
			StringBuilder sb = new StringBuilder();
			int i = 0;
			for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
				IBinding binding = it.next();
				if (!binding.isDefined()) {
					toStringUndefined(prefix, sb, binding.getName());
				} else if (binding.getName() != null) {
					Object value = binding.getValue();
					if (value instanceof IArgs) {
						toStringArgs(prefix, sb, binding.getName(),
								(IArgs) value);
					} else {
						toStringPlain(prefix, sb, binding.getName(), value);
					}
				} else {
					Object value = binding.getValue();
					if (value instanceof IArgs) {
						toStringArgs(prefix, sb, "" + i, (IArgs) value);
					} else {
						toStringPlain(prefix, sb, "" + i, value);
					}
				}
				i++;
			}
			return sb.toString();
		} finally {
			nesting--;
			if (nesting == 0) {
				visited = null;
			}
		}
	}

	protected static void toStringArgs(String prefix, StringBuilder sb,
			String name, IArgs value) {
		for (int i = 1; i < nesting; i++) {
			sb.append("   ");
		}
		sb.append(name);
		sb.append(" = ");
		sb.append("{");
		sb.append("\n");
		sb.append(toString(value, prefix));
		sb.append("\n");
		for (int i = 1; i < nesting; i++) {
			sb.append("   ");
		}
		sb.append("}");
		sb.append("\n");
	}

	protected static void toStringPlain(String prefix, StringBuilder sb,
			String name, Object value) {
		for (int i = 1; i < nesting; i++) {
			sb.append("   ");
		}
		sb.append(name);
		sb.append(" = ");
		sb.append(StringTools.safeString(value));
		sb.append("\n");
	}

	protected static void toStringUndefined(String prefix, StringBuilder sb,
			String name) {
		for (int i = 1; i < nesting; i++) {
			sb.append("   ");
		}
		sb.append(name);
		sb.append(" = UNDEFINED");
		sb.append("\n");
	}

	/**
	 * Interpret <code>path</code> as a "." separated sequence of arg names,
	 * descend the tree of {@link IArgs} objects and undefine the leave name.
	 * 
	 * @param args
	 * @param path
	 */
	static public void undefinePath(IArgs args, String path) {
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
	 * @param prefix
	 * @param args
	 * @param processor
	 */
	static public void visitNamedBindings(String prefix, IArgs args,
			IBindingProcessor processor) {
		processor.visitArgs(prefix, args);
		for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
			IBinding binding = it.next();
			String name = binding.getName();
			if (name == null) {
				continue;
			}
			Object value = binding.getValue();
			if (value instanceof IArgs) {
				visitNamedBindings(StringTools.pathAppend(prefix, ".", name),
						(IArgs) value, processor);
			} else {
				processor.visitBinding(prefix, args, binding);
			}
		}
	}
}
