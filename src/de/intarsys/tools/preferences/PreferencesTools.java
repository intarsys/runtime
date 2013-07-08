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
package de.intarsys.tools.preferences;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.crypto.CryptoEnvironment;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgumentDeclaration;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationElement;
import de.intarsys.tools.functor.IDeclarationSupport;
import de.intarsys.tools.logging.LogTools;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.Converter;
import de.intarsys.tools.string.ConverterException;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for some common tasks when dealing with {@link IPreferences}.
 * 
 */
public class PreferencesTools {

	public static final String ELEMENT_SEPARATOR = ";"; //$NON-NLS-1$

	public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

	final private static Logger Log = LogTools
			.getLogger(PreferencesTools.class);

	/**
	 * <p>
	 * The value is converted using the {@link IConverter} framework with the
	 * dummy target type {@link PreferenceValue} (which should result in a
	 * {@link String} finally ).
	 * 
	 * @param value
	 * @return
	 */
	protected static Object convertValue(Object value) {
		if (value == null) {
			// shortcut
			return null;
		} else if (value instanceof String) {
			// shortcut
			return value;
		} else {
			try {
				return ConverterRegistry.get().convert(value,
						PreferenceValue.class);
			} catch (ConversionException e) {
				return StringTools.safeString(value);
			}
		}
	}

	static public String fitKey(String name) {
		int length = name.length();
		if (length <= Preferences.MAX_KEY_LENGTH) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[length];
		name.getChars(0, length, chars, 0);
		boolean start = true;
		for (int i = 0; i < length; i++) {
			char c = chars[i];
			if (c == '.' || c == '/' || c == '\\' || c == '_') {
				start = true;
				continue;
			}
			if (Character.isUpperCase(c) || c == '.') {
				start = true;
				sb.append(c);
				continue;
			}
			if (CharacterTools.isVowel(c)) {
				if (start) {
					start = false;
					sb.append(c);
				}
				continue;
			}
			sb.append(c);
		}
		name = sb.toString();
		length = name.length();
		if (length <= Preferences.MAX_KEY_LENGTH) {
			return name;
		}
		return name.substring(length - Preferences.MAX_KEY_LENGTH, length);
	}

	public static <T extends EnumItem> T getEnumItem(IPreferences preferences,
			EnumMeta<T> meta, String name) {
		if (preferences == null) {
			return meta.getDefault();
		}
		String optionValue = preferences.get(name);
		return meta.getItemOrDefault(optionValue);
	}

	public static <T extends EnumItem> T getEnumItem(IPreferences preferences,
			EnumMeta<T> meta, String name, String defaultValue) {
		if (preferences == null) {
			return meta.getItemOrDefault(defaultValue);
		}
		String optionValue = preferences.get(name, defaultValue);
		return meta.getItem(optionValue);
	}

	public static String getLarge(IPreferences preferences, String name,
			String defaultValue) {
		if (preferences == null) {
			return null;
		}
		IPreferences childNode = preferences.node(name);
		int i = 0;
		String subKey = "part" + i++; //$NON-NLS-1$
		String subValue = childNode.get(subKey, null);
		if (subValue == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		while ((subValue != null)
				&& (subValue.length() == Preferences.MAX_VALUE_LENGTH)) {
			sb.append(subValue);
			subKey = "part" + i++; //$NON-NLS-1$
			subValue = childNode.get(subKey, null);
		}

		// read terminator
		if (subValue != null) {
			sb.append(subValue);
		}
		return sb.toString();
	}

	public static String getSecret(IPreferences preferences, String name,
			String defaultValue) {
		if (CryptoEnvironment.get() == null) {
			Log.log(Level.WARNING,
					"CryptoEnvironment not available, can't read preference '"
							+ name + "'");
			return defaultValue;
		}
		try {
			String tempValue = preferences.get(name);
			if (StringTools.isEmpty(tempValue)) {
				return defaultValue;
			}
			return CryptoEnvironment.get().decryptStringEncoded(tempValue);
		} catch (IOException e) {
			Log.log(Level.WARNING,
					"CryptoEnvironment can't decrypt preference '" + name + "'");
			return defaultValue;
		}
	}

	public static void importPreferences(IPreferences root, IPreferences source)
			throws BackingStoreException {
		String[] childrenNames = source.childrenNames();
		for (int i = 0; i < childrenNames.length; i++) {
			String childName = childrenNames[i];
			IPreferences rootChild = root.node(childName);
			IPreferences sourceChild = source.node(childName);
			importPreferences(rootChild, sourceChild);
		}
		String[] keys = source.keys();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			root.put(key, source.get(key));
		}
	}

	/**
	 * Add all argument bindings stored in source to args.
	 * 
	 * @param source
	 * @param args
	 */
	static public void mergeArgs(IPreferences source, IArgs args) {
		try {
			String[] childrenNames = source.childrenNames();
			for (int i = 0; i < childrenNames.length; i++) {
				String childName = childrenNames[i];
				IPreferences sourceChild = source.node(childName);
				if (!args.isDefined(childName)) {
					if (sourceChild == null) {
						args.put(childName, null);
					} else {
						IArgs tempArgs = Args.create();
						args.put(childName, tempArgs);
						mergeArgs(sourceChild, tempArgs);
					}
				}
			}
			String[] keys = source.keys();
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				if (!args.isDefined(key)) {
					args.put(key, source.get(key));
				}
			}
		} catch (BackingStoreException e) {
			//
		}
	}

	/**
	 * Write all bindings in pValue to the preferences.
	 * 
	 * @param preferences
	 * @param name
	 * @param pValue
	 * @param argDecl
	 * @param secret
	 */
	protected static void putArgArgs(IPreferences preferences, String name,
			IArgs pValue, IArgumentDeclaration argDecl, boolean secret) {
		IPreferences childNode = preferences.node(name);
		IDeclarationBlock argDeclBlock = null;
		if (argDecl instanceof IDeclarationBlock) {
			argDeclBlock = (IDeclarationBlock) argDecl;
		} else if (argDecl instanceof IDeclarationSupport) {
			argDeclBlock = ((IDeclarationSupport) argDecl)
					.getDeclarationBlock();
		}
		IDeclarationElement[] childDeclarations = null;
		if (argDeclBlock != null) {
			childDeclarations = argDeclBlock.getDeclarationElements();
		}
		if (childDeclarations != null) {
			putArgsDeclared(childNode, pValue, childDeclarations, secret);
		} else {
			putArgsAll(childNode, pValue, secret);
		}
	}

	/**
	 * Store the "null" value in the preferences for argument "name".
	 * 
	 * @param preferences
	 * @param name
	 */
	protected static void putArgNull(IPreferences preferences, String name) {
		// this is not really a null preference...
		// preferences.remove(name);
	}

	/**
	 * Serialize the declared argument bindings to a preferences node.
	 * <p>
	 * This implementation does not remove any binding from preferences. If this
	 * is required, you should clear the preferences beforehand.
	 * 
	 * @param preferences
	 * @param args
	 * @param block
	 */
	static public void putArgs(IPreferences preferences, IArgs args,
			IDeclarationBlock block) {
		putArgsDeclared(preferences, args, block.getDeclarationElements(),
				false);
	}

	/**
	 * Save all argument bindings in the preferences node.
	 * <p>
	 * This implementation does not remove any binding from preferences. If this
	 * is required, you should clear the preferences beforehand.
	 * 
	 * @param preferences
	 * @param args
	 * @param secret
	 */
	static public void putArgsAll(IPreferences preferences, IArgs args,
			boolean secret) {
		// we do not need any special handling for empty args
		Set<String> names = args.names();
		if (names.isEmpty()) {
			for (int i = 0; i < args.size(); i++) {
				Object value = args.get(i);
				putArgValue(preferences, String.valueOf(i), value, null, secret);
			}
		} else {
			for (String argName : names) {
				if (args.isDefined(argName)) {
					Object value = args.get(argName);
					putArgValue(preferences, argName, value, null, secret);
				}
			}
		}
	}

	/**
	 * Serialize the declared argument bindings to a preferences node.
	 * <p>
	 * This implementation does not remove any binding from preferences. If this
	 * is required, you should clear the preferences beforehand.
	 * 
	 * @param preferences
	 * @param args
	 * @param declarations
	 */
	static public void putArgsDeclared(IPreferences preferences, IArgs args,
			IDeclarationElement[] declarations, boolean secret) {
		for (int i = 0; i < declarations.length; i++) {
			IDeclarationElement decl = declarations[i];
			if (decl instanceof IArgumentDeclaration) {
				IArgumentDeclaration argDecl = (IArgumentDeclaration) decl;
				secret = secret || argDecl.hasModifier("secret");
				String argName = argDecl.getName();
				if (args.isDefined(argName)) {
					Object argValue = args.get(argName);
					putArgValue(preferences, argName, argValue, argDecl, secret);
				}
			}
		}
	}

	/**
	 * Save the string valued argument "name"
	 * 
	 * @param preferences
	 * @param name
	 * @param pValue
	 * @param secret
	 */
	protected static void putArgString(IPreferences preferences, String name,
			Object pValue, boolean secret) {
		if (secret) {
			try {
				pValue = CryptoEnvironment.get().encryptStringEncoded(
						pValue.toString());
			} catch (IOException e) {
				pValue = "";
			}
		}
		preferences.put(name, StringTools.safeString(pValue));
	}

	/**
	 * Serialize a name/value pair in a preferences node.
	 * <p>
	 * If value is itself an {@link IArgs} structure, argument serialization is
	 * recursed.
	 * 
	 * @param preferences
	 * @param name
	 * @param pValue
	 * @param argDecl
	 * @param secret
	 */
	static public void putArgValue(IPreferences preferences, String name,
			Object pValue, IArgumentDeclaration argDecl, boolean secret) {
		boolean modTransient = argDecl != null
				&& argDecl.hasModifier(IDeclarationElement.MOD_TRANSIENT);
		if (modTransient) {
			return;
		}
		if (pValue instanceof Secret) {
			if (!secret) {
				// create preferences structure suitable for secret bindings
			}
			secret = true;
			pValue = ((Secret) pValue).getValue();
		}
		// we currently do not support secret arguments in preferences
		if (secret) {
			return;
		}
		pValue = convertValue(pValue);
		if (pValue == null) {
			putArgNull(preferences, name);
		} else if (pValue instanceof IArgs) {
			putArgArgs(preferences, name, (IArgs) pValue, argDecl, secret);
		} else {
			putArgString(preferences, name, pValue, secret);
		}
	}

	public static void putEnumItem(IPreferences preferences, String name,
			EnumItem item) {
		preferences.put(name, item.getId());
	}

	public static void putLarge(IPreferences preferences, String name,
			String longValue) {
		try {
			preferences.remove(name);
			IPreferences childNode = preferences.node(name);
			childNode.clear();
			StringReader reader = new StringReader(longValue);
			char[] buffer = new char[Preferences.MAX_VALUE_LENGTH];
			int i = 0;
			String subKey;
			String subValue = null;
			int length = reader.read(buffer);
			while (length != -1) {
				subKey = "part" + i++; //$NON-NLS-1$
				subValue = new String(buffer, 0, length);
				childNode.put(subKey, subValue);
				length = reader.read(buffer);
			}

			// write terminator
			if ((subValue != null)
					&& (subValue.length() == Preferences.MAX_VALUE_LENGTH)) {
				subKey = "part" + i++; //$NON-NLS-1$
				subValue = ""; //$NON-NLS-1$
				childNode.put(subKey, subValue);
			}
		} catch (Exception e) {
			//
		}
	}

	public static void putSecret(IPreferences preferences, String name,
			String value) {
		if (CryptoEnvironment.get() == null) {
			Log.log(Level.WARNING,
					"CryptoEnvironment not available, can't write preference '"
							+ name + "'");
			return;
		}
		try {
			String tempValue = CryptoEnvironment.get().encryptStringEncoded(
					value);
			preferences.put(name, tempValue);
		} catch (IOException e) {
			Log.log(Level.WARNING,
					"CryptoEnvironment can't encrypt preference '" + name + "'");
		}
	}

	public static Rectangle toRect(String value) {
		if (value == null) {
			return null;
		}
		int[] rectDef = Converter.asIntArray(value);
		if ((rectDef == null) || (rectDef.length < 4)) {
			return null;
		}
		return new Rectangle(rectDef[0], rectDef[1], rectDef[2], rectDef[3]);
	}

	public static Rectangle toRect(String value, int[] ranges) {
		if (value == null) {
			return null;
		}
		String[] rectDefs = Converter.asStringArray(value);
		if ((rectDefs == null) || (rectDefs.length != 4)) {
			return null;
		}
		int[] rectValues = new int[4];
		for (int i = 0; i < rectDefs.length; i++) {
			String rectDef = rectDefs[i];
			if (rectDef.indexOf("%") >= 0) {
				rectDef = rectDef.replaceAll("%", "");
				try {
					rectValues[i] = (int) ((float) Converter.asInteger(rectDef)
							* (float) ranges[i] / 100f);
				} catch (ConverterException e) {
					rectValues[i] = 0;
				}
			} else {
				try {
					rectValues[i] = Converter.asInteger(rectDef);
				} catch (ConverterException e) {
					rectValues[i] = 0;
				}
			}
		}
		return new Rectangle(rectValues[0], rectValues[1], rectValues[2],
				rectValues[3]);
	}

	public static String toString(float[] value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			if (i != 0) {
				sb.append(ELEMENT_SEPARATOR);
			}
			sb.append(value[i]);
		}
		return sb.toString();
	}

	public static String toString(int[] value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			if (i != 0) {
				sb.append(ELEMENT_SEPARATOR);
			}
			sb.append(value[i]);
		}
		return sb.toString();
	}

	public static String toString(List value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator it = value.iterator(); it.hasNext();) {
			Object element = it.next();
			sb.append(String.valueOf(element));
			if (it.hasNext()) {
				sb.append(ELEMENT_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public static String toString(Map map) {
		if (map == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			sb.append(String.valueOf(entry.getKey()));
			sb.append(KEY_VALUE_SEPARATOR);
			Object value = (entry.getValue() == null) ? "" : String
					.valueOf(entry.getValue());
			sb.append(value);
			if (i.hasNext()) {
				sb.append(ELEMENT_SEPARATOR);
			}
		}
		return sb.toString();
	}

	public static String toString(Rectangle rect) {
		if (rect == null) {
			return ""; //$NON-NLS-1$
		}
		int[] rectDef = new int[] { rect.x, rect.y, rect.width, rect.height };
		return PreferencesTools.toString(rectDef);
	}

	public static String toString(String[] value) {
		if (value == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < value.length; i++) {
			if (i != 0) {
				sb.append(ELEMENT_SEPARATOR);
			}
			sb.append(value[i]);
		}
		return sb.toString();
	}

	public static String[] toStringArray(String value) {
		if (value == null) {
			return null;
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR,
				false);
		String[] result = new String[tk.countTokens()];
		int i = 0;
		while (tk.hasMoreTokens()) {
			String token = tk.nextToken();
			result[i] = token.trim();
			i++;
		}
		return result;
	}

	/**
	 * 
	 */
	private PreferencesTools() {
		// tool class
	}
}
