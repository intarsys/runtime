package de.intarsys.tools.functor.common;

import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.codeexit.CodeExit;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclaration;
import de.intarsys.tools.functor.ConstantFunctor;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationElement;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.IFunctorFactory;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.preferences.PreferenceValue;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

public class DeclarationIO {

	public static class ArgLookupFunctor implements IFunctor {
		private final String path;

		public ArgLookupFunctor(String path) {
			super();
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		@Override
		public Object perform(IFunctorCall call) throws FunctorException {
			return ArgTools.getPath(call.getArgs(), path);
		}
	}

	public static final String LANG_STRING = "String"; //$NON-NLS-1$
	public static final String LANG_JAVASCRIPT = "JavaScript"; //$NON-NLS-1$

	public static final String LANG_ARGLOOKUP = "ArgLookup"; //$NON-NLS-1$
	public static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	public static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTR_MODIFIER = "modifier"; //$NON-NLS-1$
	public static final String ATTR_MODIFIERS = "modifiers"; //$NON-NLS-1$
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	public static final String ATTR_VALUE = "value"; //$NON-NLS-1$
	public static final String ELEMENT_ARG = "arg"; //$NON-NLS-1$
	public static final String ELEMENT_DECLARATIONS = "declarations"; //$NON-NLS-1$

	public static final String MOD_SECRET = "secret"; //$NON-NLS-1$
	public static final String MOD_SYSTEMTRANSIENT = "system.transient"; //$NON-NLS-1$

	public static final String VALUE_NULL = "null"; //$NON-NLS-1$

	private static final Map<String, IFunctorFactory> FUNCTOR_FACTORIES = new HashMap<>();

	static {
		registerFunctorFactory(LANG_STRING, new IFunctorFactory() {
			@Override
			public IFunctor createFunctor(Object... object) throws ObjectCreationException {
				return new ConstantFunctor(object[0]);
			}
		});
		registerFunctorFactory(LANG_ARGLOOKUP, new IFunctorFactory() {
			@Override
			public IFunctor createFunctor(Object... object) throws ObjectCreationException {
				final String path = (String) object[0];
				if (StringTools.isEmpty(path)) {
					return null;
				}
				return new ArgLookupFunctor(path);
			}
		});
		registerFunctorFactory(LANG_JAVASCRIPT, new IFunctorFactory() {
			@Override
			public IFunctor createFunctor(Object... object) throws ObjectCreationException {
				String source = (String) object[0];
				if (StringTools.isEmpty(source)) {
					return null;
				}
				// owner is undefined
				CodeExit codeExit = new CodeExit(new Object());
				codeExit.setType(LANG_JAVASCRIPT);
				codeExit.setSource(source);
				return codeExit;
			}
		});
	}

	public static IFunctor createFunctor(String language, Object source) throws ObjectCreationException {
		if (StringTools.isEmpty(language)) {
			return null;
		}
		if (source == null) {
			return null;
		}
		IFunctorFactory factory = DeclarationIO.lookupFunctorFactory(language);
		if (factory != null) {
			return factory.createFunctor(source);
		}
		throw new ObjectCreationException("unknown language '" + language //$NON-NLS-1$
				+ "'"); //$NON-NLS-1$
	}

	public static IFunctorFactory lookupFunctorFactory(String id) {
		return FUNCTOR_FACTORIES.get(id);
	}

	public static Collection<String> lookupFunctorFactoryNames() {
		return FUNCTOR_FACTORIES.keySet();
	}

	public static void registerFunctorFactory(String id, IFunctorFactory factory) {
		FUNCTOR_FACTORIES.put(id, factory);
	}

	private String defaultLanguage = LANG_STRING;

	protected Object convertValue(Object value) {
		if (value == null) {
			// shortcut
			return null;
		} else if (value instanceof String) {
			// shortcut
			return value;
		} else {
			try {
				return ConverterRegistry.get().convert(value, PreferenceValue.class);
			} catch (ConversionException e) {
				return StringTools.safeString(value);
			}
		}
	}

	public void deserializeArgumentDeclaration(IDeclarationBlock declarationBlock, IElement element, boolean secret)
			throws ObjectCreationException {
		String name = element.attributeValue(ATTR_NAME, null);
		if (name == null) {
			throw new ObjectCreationException("'name' attribute missing");
		}
		String modifiers = element.attributeValue(ATTR_MODIFIERS, null);
		if (StringTools.isEmpty(modifiers)) {
			// fallback to deprecated name
			modifiers = element.attributeValue(ATTR_MODIFIER, null);
		}
		String[] segments;
		if (".".equals(name)) {
			segments = new String[] { "." };
		} else {
			segments = name.split("\\.");
		}
		IDeclarationBlock currentBlock = declarationBlock;
		IDeclarationElement declaration = null;
		for (int i = 0; i < segments.length; i++) {
			String segment = segments[i];
			declaration = currentBlock.getDeclarationElement(segment);
			if (!(declaration instanceof ArgumentDeclaration)) {
				declaration = new ArgumentDeclaration(currentBlock.getDeclarationContext(), segment, null);
				currentBlock.addDeclarationElement(declaration);
			}
			currentBlock = ((ArgumentDeclaration) declaration).getDeclarationBlock();
		}
		ArgumentDeclaration argumentDeclaration = (ArgumentDeclaration) declaration;
		// overwrite existing modifiers!
		argumentDeclaration.setModifierString(modifiers); // NOSONAR - not nullable
		secret = secret || declaration.hasModifier(MOD_SECRET);
		if (element.elementIterator(ELEMENT_ARG).hasNext()) {
			argumentDeclaration.setDefaultValueUndefined();
			// only child elements available and allowed
			deserializeDeclarationElements(argumentDeclaration.getDeclarationBlock(), element, secret);
		} else {
			Class<?> type = Object.class;
			String typeName = element.attributeValue(ATTR_TYPE, null);
			if (typeName != null) {
				type = ClassTools.createClass(typeName, null, null);
			}
			argumentDeclaration.setType(type);
			IFunctor functor = deserializeDefaultFunctor(element, secret);
			argumentDeclaration.setDefaultFunctor(functor);
		}
	}

	public void deserializeDeclarationBlock(IDeclarationBlock declarationBlock, IElement element)
			throws ObjectCreationException {
		if (element == null) {
			return;
		}
		IElement declarationsElement = element.element(ELEMENT_DECLARATIONS);
		if (declarationsElement != null) {
			deserializeDeclarationElements(declarationBlock, declarationsElement, false);
		}
	}

	public void deserializeDeclarationElements(IDeclarationBlock declarationBlock, IElement declarationsElement,
			boolean secret) throws ObjectCreationException {
		Iterator<IElement> it = declarationsElement.elementIterator(ELEMENT_ARG);
		while (it.hasNext()) {
			IElement argumentElement = it.next();
			deserializeArgumentDeclaration(declarationBlock, argumentElement, secret);
		}
	}

	public IFunctor deserializeDefaultFunctor(IElement element, boolean secret)
			throws ObjectCreationException {
		Object value = element.attributeValue(ATTR_VALUE, null);
		if (secret) {
			try {
				Secret tempSecret = Secret.parse((String) value);
				value = tempSecret.getString();
			} catch (GeneralSecurityException e) {
				throw new ObjectCreationException(e);
			}
		}
		String language = element.attributeValue(ATTR_LANGUAGE, getDefaultLanguage());
		if (value != null) {
			return DeclarationIO.createFunctor(language, value);
		} else if (element.attributeValue(ATTR_CLASS, null) != null) {
			value = ElementTools.createObject(element, Object.class, null, Args.create());
			return DeclarationIO.createFunctor(language, value);
		} else if (element.hasElements()) {
			value = ElementTools.createPropertyValue(null, element, Object.class, null);
			return DeclarationIO.createFunctor(language, value);
		} else {
			return null;
		}
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	protected void serializeArgs(IArgs args, IElement element, boolean secret) {
		if (args.size() == 0) {
			element.setAttributeValue(ATTR_TYPE, Args.class.getName());
		} else {
			for (Iterator<String> it = args.names().iterator(); it.hasNext();) {
				String name = it.next();
				IElement argElement = element.newElement(ELEMENT_ARG);
				argElement.setAttributeValue(ATTR_NAME, name);
				if (args.isDefined(name)) {
					Object value = args.get(name);
					serializeValue(value, argElement, LANG_STRING, secret);
				}
			}
		}
	}

	public void serializeArgumentDeclaration(ArgumentDeclaration declaration, IElement element, boolean secret) {
		element.setAttributeValue(ATTR_NAME, declaration.getName());
		if (declaration.getType() != Object.class) {
			element.setAttributeValue(ATTR_TYPE, declaration.getType().getName());
		}
		if (!StringTools.isEmpty(declaration.getModifierString())) {
			element.setAttributeValue(ATTR_MODIFIERS, declaration.getModifierString());
		}
		if (declaration.isDefaultDefined()) {
			secret = secret || declaration.hasModifier(MOD_SECRET);
			IFunctor functor = declaration.getDefaultFunctor();
			if (functor instanceof ConstantFunctor) {
				ConstantFunctor constantFunctor = (ConstantFunctor) functor;
				Object value = constantFunctor.getConstant();
				serializeValue(value, element, LANG_STRING, secret);
			} else if (functor instanceof ArgLookupFunctor) {
				ArgLookupFunctor constantFunctor = (ArgLookupFunctor) functor;
				Object value = constantFunctor.getPath();
				serializeValue(value, element, LANG_ARGLOOKUP, secret);
			} else if (functor instanceof CodeExit) {
				CodeExit codeExit = (CodeExit) functor;
				Object value = codeExit.getSource();
				serializeValue(value, element, codeExit.getType(), secret);
			} else {
				try {
					Object value = declaration.getDefaultValue(Args.create());
					serializeValue(value, element, LANG_STRING, secret);
				} catch (FunctorException e) {
					//
				}
			}
		} else {
			IDeclarationBlock argDeclarationBlock = declaration.getDeclarationBlock();
			if (argDeclarationBlock != null) {
				serializeDeclarationElements(argDeclarationBlock.getDeclarationElements(), element, secret);
			}
		}
	}

	public void serializeDeclarationBlock(IDeclarationBlock declarationBlock, IElement element) {
		if (declarationBlock == null || declarationBlock.size() == 0) {
			return;
		}
		IElement declarationsElement = element.newElement(ELEMENT_DECLARATIONS);
		IDeclarationElement[] elements = declarationBlock.getDeclarationElements();
		serializeDeclarationElements(elements, declarationsElement, false);
	}

	public void serializeDeclarationElement(IDeclarationElement declaration, IElement element, boolean secret) {
		if (declaration.hasModifier(MOD_SYSTEMTRANSIENT)) {
			return;
		}
		if (declaration instanceof ArgumentDeclaration) {
			ArgumentDeclaration argDeclaration = (ArgumentDeclaration) declaration;
			IElement argElement = element.newElement(ELEMENT_ARG);
			serializeArgumentDeclaration(argDeclaration, argElement, secret);
		}
	}

	public void serializeDeclarationElements(IDeclarationElement[] elements, IElement declarationsElement,
			boolean secret) {
		for (int i = 0; i < elements.length; i++) {
			IDeclarationElement element = elements[i];
			serializeDeclarationElement(element, declarationsElement, secret);
		}
	}

	protected void serializeValue(Object value, IElement element, String language, boolean secret) {
		value = convertValue(value);
		if (value == null) {
			element.newElement(VALUE_NULL);
		} else if (value instanceof IArgs) {
			serializeArgs((IArgs) value, element, secret);
		} else {
			if (!language.equals(getDefaultLanguage())) {
				element.setAttributeValue(ATTR_LANGUAGE, language);
			}
			if (secret) {
				Secret tempSecret;
				if (value instanceof Secret) {
					tempSecret = (Secret) value;
				} else {
					char[] tempValue = StringTools.safeString(value).toCharArray();
					tempSecret = Secret.hideTrimmed(tempValue);
				}
				String tempEncoded = tempSecret.getEncoded();
				element.setAttributeValue(ATTR_VALUE, tempEncoded);
			} else {
				element.setAttributeValue(ATTR_VALUE, value.toString());
			}
		}
	}

	public void setDefaultLanguage(String defaultlanguage) {
		this.defaultLanguage = defaultlanguage;
	}

}
