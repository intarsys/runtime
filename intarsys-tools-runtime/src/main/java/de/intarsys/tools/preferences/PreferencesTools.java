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
package de.intarsys.tools.preferences;

import java.awt.Rectangle;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.ConfigurationException;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.crypto.CryptoTools;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.factory.InstanceSpec;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgumentDeclaration;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationElement;
import de.intarsys.tools.functor.IDeclarationSupport;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.infoset.IElementConfigurable;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.Converter;
import de.intarsys.tools.string.ConverterException;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for some common tasks when dealing with {@link IPreferences}.
 * 
 */
public class PreferencesTools {

	public abstract static class Install implements IElementConfigurable {

		private IElement element;

		@Override
		public void configure(IElement element) throws ConfigurationException {
			this.element = element;
		}

		public IElement getElement() {
			return element;
		}

	}

	public static class InstallMain extends Install {

		@PostConstruct
		public void install() {
			IPreferences preferences = PreferencesFactory.get().getMain();
			setPreferences(preferences, getElement());
		}
	}

	public static class InstallNode extends Install {

		private String name;

		public String getName() {
			return name;
		}

		@PostConstruct
		public void install() {
			IPreferences root = PreferencesFactory.get().getRoot();
			IPreferences preferences = root.node(getName());
			if (preferences != null) {
				setPreferences(preferences, getElement());
			}
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class InstallRoot extends Install {

		@PostConstruct
		public void install() {
			IPreferences preferences = PreferencesFactory.get().getRoot();
			setPreferences(preferences, getElement());
		}
	}

	public static final String ELEMENT_SEPARATOR = ";"; //$NON-NLS-1$

	public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$

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
		} else if (value instanceof List) {
			return new Args((List) value);
		} else if (value instanceof Object[]) {
			return Args.createIndexed((Object[]) value);
		} else {
			try {
				return ConverterRegistry.get().convert(value, PreferenceValue.class);
			} catch (ConversionException e) {
				return StringTools.safeString(value);
			}
		}
	}

	protected static String fitLength(String name, int max) {
		int length = name.length();
		if (length <= max) {
			return name;
		}
		StringBuilder sb = new StringBuilder();
		char[] chars = new char[length];
		name.getChars(0, length, chars, 0);
		boolean start = true;
		for (int i = 0; i < length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c) || c == '.' || c == '_') {
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
		if (length <= max) {
			return name;
		}
		return name.substring(length - max, length);
	}

	public static <T extends EnumItem> T getEnumItem(IPreferences preferences, EnumMeta<T> meta, String name) {
		if (preferences == null) {
			return meta.getDefault();
		}
		String optionValue = preferences.get(name);
		return meta.getItemOrDefault(optionValue);
	}

	public static <T extends EnumItem> T getEnumItem(IPreferences preferences, EnumMeta<T> meta, String name,
			String defaultValue) {
		if (preferences == null) {
			return meta.getItemOrDefault(defaultValue);
		}
		String optionValue = preferences.get(name, defaultValue);
		return meta.getItem(optionValue);
	}

	public static String getLarge(IPreferences preferences, String name, String defaultValue) {
		if (preferences == null) {
			return defaultValue;
		}
		IPreferences childNode = preferences.node(name);
		int i = 0;
		String subKey = "part" + i++; //$NON-NLS-1$
		String subValue = childNode.get(subKey, null);
		if (subValue == null) {
			return defaultValue;
		}
		StringBuilder sb = new StringBuilder();
		while ((subValue != null) && (subValue.length() == Preferences.MAX_VALUE_LENGTH)) {
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

	/**
	 * Get the preference name as a {@link Secret}.
	 * 
	 * Be aware that the preference value *is expected* to be in naked secret syntax (e.g. plain#Zm9v).
	 * 
	 * @param preferences
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static Secret getSecret(IPreferences preferences, String name, Secret defaultValue) {
		String tempValue = preferences.get(name);
		if (StringTools.isEmpty(tempValue)) {
			return defaultValue;
		}
		return Secret.parse(tempValue);
	}

	public static void importPreferences(IPreferences root, IPreferences source) throws BackingStoreException {
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
	 * Add all argument bindings stored in source preferences to args.
	 * 
	 * @param source
	 * @param args
	 */
	public static IArgs mergeArgs(IPreferences source, IArgs args) {
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
		return args;
	}

	/**
	 * Migration tool method - move property "name" from oldNode to newNode if not already present.
	 * 
	 * @param newNode
	 * @param oldNode
	 * @param name
	 */
	public static void moveIfAbsent(IPreferences newNode, IPreferences oldNode, String name) {
		String newValue = newNode.get(name);
		if (StringTools.isEmpty(newValue)) {
			String oldValue = oldNode.get(name);
			newNode.put(name, oldValue);
			oldNode.remove(name);
		}
	}

	/**
	 * Write all bindings in pValue to the preferences.
	 * 
	 * @param preferences
	 * @param name
	 * @param pValue
	 * @param argDecl
	 */
	protected static void putArgArgs(IPreferences preferences, String name, IArgs pValue,
			IArgumentDeclaration argDecl) {
		IPreferences childNode = preferences.node(name);
		IDeclarationBlock argDeclBlock = null;
		if (argDecl instanceof IDeclarationBlock) {
			argDeclBlock = (IDeclarationBlock) argDecl;
		} else if (argDecl instanceof IDeclarationSupport) {
			argDeclBlock = ((IDeclarationSupport) argDecl).getDeclarationBlock();
		}
		IDeclarationElement[] childDeclarations = null;
		if (argDeclBlock != null) {
			childDeclarations = argDeclBlock.getDeclarationElements();
		}
		if (childDeclarations != null) {
			if (childDeclarations.length > 0) {
				putArgsDeclared(childNode, pValue, childDeclarations);
			} else if (argDecl.getType() == Object.class || argDecl.getType() == IArgs.class) {
				putArgsAll(childNode, pValue);
			}
		} else {
			putArgsAll(childNode, pValue);
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
	public static void putArgs(IPreferences preferences, IArgs args, IDeclarationBlock block) {
		putArgsDeclared(preferences, args, block.getDeclarationElements());
	}

	/**
	 * Save all argument bindings in the preferences node.
	 * <p>
	 * This implementation does not remove any binding from preferences. If this
	 * is required, you should clear the preferences beforehand.
	 * 
	 * @param preferences
	 * @param args
	 */
	public static void putArgsAll(IPreferences preferences, IArgs args) {
		// we do not need any special handling for empty args
		Set<String> names = args.names();
		if (names.isEmpty()) {
			for (int i = 0; i < args.size(); i++) {
				Object value = args.get(i);
				putArgValue(preferences, String.valueOf(i), value, null);
			}
		} else {
			for (String argName : names) {
				if (args.isDefined(argName)) {
					Object value = args.get(argName);
					putArgValue(preferences, argName, value, null);
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
	public static void putArgsDeclared(IPreferences preferences, IArgs args, IDeclarationElement[] declarations) {
		for (int i = 0; i < declarations.length; i++) {
			IDeclarationElement decl = declarations[i];
			if (decl instanceof IArgumentDeclaration) {
				IArgumentDeclaration argDecl = (IArgumentDeclaration) decl;
				String argName = argDecl.getName();
				if (args.isDefined(argName)) {
					Object argValue = args.get(argName);
					putArgValue(preferences, argName, argValue, argDecl);
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
	 */
	protected static void putArgString(IPreferences preferences, String name, Object pValue) {
		String tempValue = StringTools.safeString(pValue);
		preferences.put(name, tempValue);
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
	 */
	public static void putArgValue(IPreferences preferences, String name, Object pValue, IArgumentDeclaration argDecl) {
		boolean modTransient = argDecl != null && argDecl.hasModifier(IDeclarationElement.MOD_TRANSIENT);
		if (modTransient) {
			return;
		}
		pValue = convertValue(pValue);
		if (pValue == null) {
			putArgNull(preferences, name);
		} else if (pValue instanceof IArgs) {
			putArgArgs(preferences, name, (IArgs) pValue, argDecl);
		} else if (pValue instanceof InstanceSpec) {
			putArgArgs(preferences, name, ((InstanceSpec) pValue).toArgs(), argDecl);
		} else {
			putArgString(preferences, name, pValue);
		}
	}

	public static void putEnumItem(IPreferences preferences, String name, EnumItem item) {
		preferences.put(name, item.getId());
	}

	public static void putLarge(IPreferences preferences, String name, String longValue) {
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
			if ((subValue != null) && (subValue.length() == Preferences.MAX_VALUE_LENGTH)) {
				subKey = "part" + i++; //$NON-NLS-1$ // NOSONAR
				subValue = ""; //$NON-NLS-1$
				childNode.put(subKey, subValue);
			}
		} catch (Exception e) {
			//
		}
	}

	public static void putSecret(IPreferences preferences, String name, Secret value) {
		if (CryptoTools.isEmpty(value)) {
			preferences.remove(name);
		} else {
			String tempValue = value.getEncoded();
			preferences.put(name, tempValue);
		}
	}

	public static void setPreferences(IPreferences preferences, IElement element) {
		Iterator<IElement> key = element.elementIterator("node");
		while (key.hasNext()) {
			IElement childElement = key.next();
			String name = childElement.attributeValue("name", null);
			IPreferences childPreferences = preferences.node(name);
			setPreferences(childPreferences, childElement);
		}
		Iterator<IElement> propertyElements = element.elementIterator("property");
		while (propertyElements.hasNext()) {
			IElement propertyElement = propertyElements.next();
			String name = propertyElement.attributeValue("name", null);
			String value = propertyElement.attributeValue("value", null);
			preferences.put(name, value);
		}
	}

	/**
	 * Create a "canonical" key name.
	 * 
	 * This method ensures that the name does not have more than MAX_KEY_LENGTH
	 * by discarding the leftmost characters.
	 * 
	 * @param name
	 * @return
	 */
	public static String toKeyName(String name) {
		return fitLength(name, IPreferences.MAX_KEY_LENGTH);
	}

	/**
	 * Create a "canonical" node name.
	 * 
	 * This method ensures that the name does not have more than MAX_NODE_LENGTH
	 * by discarding the leftmost characters.
	 * 
	 * @param name
	 * @return
	 */
	public static String toNodeName(String name) {
		return fitLength(name, IPreferences.MAX_NODE_LENGTH);
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
				rectDef = rectDef.replace("%", "");
				try {
					rectValues[i] = (int) ((float) Converter.asInteger(rectDef) * (float) ranges[i] / 100f);
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
		return new Rectangle(rectValues[0], rectValues[1], rectValues[2], rectValues[3]);
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
			Object value = (entry.getValue() == null) ? "" : String.valueOf(entry.getValue());
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
			return null; // NOSONAR
		}
		StringTokenizer tk = new StringTokenizer(value, ELEMENT_SEPARATOR, false);
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
