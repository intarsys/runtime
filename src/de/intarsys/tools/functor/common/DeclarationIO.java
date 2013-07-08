package de.intarsys.tools.functor.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.codeexit.CodeExit;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.crypto.CryptoEnvironment;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclaration;
import de.intarsys.tools.functor.ConstantFunctor;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgumentDeclaration;
import de.intarsys.tools.functor.IDeclarationBlock;
import de.intarsys.tools.functor.IDeclarationElement;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorFactory;
import de.intarsys.tools.infoset.ElementTools;
import de.intarsys.tools.infoset.IElement;
import de.intarsys.tools.preferences.PreferenceValue;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.string.StringTools;

public class DeclarationIO {

	public static final String LANG_STRING = "String"; //$NON-NLS-1$
	public static final String MOD_SECRET = "secret"; //$NON-NLS-1$
	public static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$
	public static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTR_MODIFIER = "modifier"; //$NON-NLS-1$
	public static final String ATTR_MODIFIERS = "modifiers"; //$NON-NLS-1$
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	public static final String ATTR_VALUE = "value"; //$NON-NLS-1$
	public static final String ELEMENT_ARG = "arg"; //$NON-NLS-1$
	public static final String ELEMENT_DECLARATIONS = "declarations"; //$NON-NLS-1$
	public static final String MOD_SYSTEMTRANSIENT = "system.transient"; //$NON-NLS-1$
	public static final String VALUE_NULL = "null"; //$NON-NLS-1$
	public static final String LANG_JAVASCRIPT = "JavaScript"; //$NON-NLS-1$

	private static final Map<String, IFunctorFactory> functorFactories = new HashMap<String, IFunctorFactory>();

	static {
		registerFunctorFactory(LANG_STRING, new IFunctorFactory() {
			@Override
			public IFunctor createFunctor(Object... object)
					throws ObjectCreationException {
				return new ConstantFunctor(object[0]);
			}
		});
		registerFunctorFactory(LANG_JAVASCRIPT, new IFunctorFactory() {
			@Override
			public IFunctor createFunctor(Object... object)
					throws ObjectCreationException {
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

	static public IFunctorFactory lookupFunctorFactory(String id) {
		return functorFactories.get(id);
	}

	static public void registerFunctorFactory(String id, IFunctorFactory factory) {
		functorFactories.put(id, factory);
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
				return ConverterRegistry.get().convert(value,
						PreferenceValue.class);
			} catch (ConversionException e) {
				return StringTools.safeString(value);
			}
		}
	}

	protected IFunctor createDefaultFunctor(Object value, String language)
			throws ObjectCreationException {
		IFunctorFactory factory = lookupFunctorFactory(language);
		if (factory != null) {
			return factory.createFunctor(value);
		} else {
			throw new ObjectCreationException("unknown language '" + language //$NON-NLS-1$
					+ "'"); //$NON-NLS-1$
		}
	}

	public void deserializeArgumentDeclaration(
			IDeclarationBlock declarationBlock, IElement element, boolean secret)
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
		String[] segments = name.split("\\.");
		IDeclarationBlock currentBlock = declarationBlock;
		IDeclarationElement declaration = null;
		for (int i = 0; i < segments.length; i++) {
			String segment = segments[i];
			declaration = currentBlock.getDeclarationElement(segment);
			if (!(declaration instanceof ArgumentDeclaration)) {
				declaration = new ArgumentDeclaration(
						currentBlock.getDeclarationContext(), segment, null);
				currentBlock.addDeclarationElement(declaration);
			}
			currentBlock = ((ArgumentDeclaration) declaration)
					.getDeclarationBlock();
		}
		ArgumentDeclaration argumentDeclaration = (ArgumentDeclaration) declaration;
		// overwrite existing modifiers!
		argumentDeclaration.setModifierString(modifiers);
		secret = secret || declaration.hasModifier(MOD_SECRET);
		if (element.elementIterator(ELEMENT_ARG).hasNext()) {
			argumentDeclaration.setDefaultValueUndefined();
			// only child elements available and allowed
			deserializeDeclarationElements(
					argumentDeclaration.getDeclarationBlock(), element, secret);
		} else {
			Class<?> type = Object.class;
			String typeName = element.attributeValue(ATTR_TYPE, null);
			if (typeName != null) {
				type = ClassTools.createClass(typeName, Object.class, null);
			}
			argumentDeclaration.setType(type);
			IFunctor functor = deserializeDefaultFunctor(argumentDeclaration,
					element, secret);
			argumentDeclaration.setDefaultFunctor(functor);
		}
	}

	public void deserializeDeclarationBlock(IDeclarationBlock declarationBlock,
			IElement element) throws ObjectCreationException {
		if (element == null) {
			return;
		}
		IElement declarationsElement = element.element(ELEMENT_DECLARATIONS);
		if (declarationsElement != null) {
			deserializeDeclarationElements(declarationBlock,
					declarationsElement, false);
		}
	}

	public void deserializeDeclarationElements(
			IDeclarationBlock declarationBlock, IElement declarationsElement,
			boolean secret) throws ObjectCreationException {
		Iterator<IElement> it = declarationsElement
				.elementIterator(ELEMENT_ARG);
		while (it.hasNext()) {
			IElement argumentElement = it.next();
			deserializeArgumentDeclaration(declarationBlock, argumentElement,
					secret);
		}
	}

	public IFunctor deserializeDefaultFunctor(IArgumentDeclaration declaration,
			IElement element, boolean secret) throws ObjectCreationException {
		Object value = element.attributeValue(ATTR_VALUE, null);
		if (secret) {
			try {
				value = CryptoEnvironment.get().decryptStringEncoded(
						(String) value);
			} catch (IOException e) {
				throw new ObjectCreationException(e);
			}
		}
		String language = element.attributeValue(ATTR_LANGUAGE,
				getDefaultLanguage());
		if (value != null) {
			return createDefaultFunctor(value, language);
		} else if (element.attributeValue(ATTR_CLASS, null) != null) {
			value = ElementTools
					.createObject(element, Object.class, null);
			return createDefaultFunctor(value, language);
		} else if (element.hasElements()) {
			value = ElementTools.createPropertyValue(null, element,
					Object.class, null);
			return createDefaultFunctor(value, language);
		} else {
			return null;
		}
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	protected void serializeArgs(IArgs args, IElement element, boolean secret) {
		if (args.size() == 0) {
			element.setAttributeValue(ATTR_CLASS, Args.class.getName());
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

	public void serializeArgumentDeclaration(ArgumentDeclaration declaration,
			IElement element, boolean secret) {
		element.setAttributeValue(ATTR_NAME, declaration.getName());
		if (declaration.getType() != Object.class) {
			element.setAttributeValue(ATTR_TYPE, declaration.getType()
					.getName());
		}
		if (!StringTools.isEmpty(declaration.getModifierString())) {
			element.setAttributeValue(ATTR_MODIFIERS,
					declaration.getModifierString());
		}
		if (declaration.isDefaultDefined()) {
			secret = secret || declaration.hasModifier(MOD_SECRET);
			IFunctor functor = declaration.getDefaultFunctor();
			if (functor instanceof ConstantFunctor) {
				ConstantFunctor constantFunctor = (ConstantFunctor) functor;
				Object value = constantFunctor.getConstant();
				serializeValue(value, element, LANG_STRING, secret);
			} else if (functor instanceof CodeExit) {
				CodeExit codeExit = (CodeExit) functor;
				Object value = codeExit.getSource();
				serializeValue(value, element, codeExit.getType(), secret);
			} else {
				try {
					Object value = declaration.getDefaultValue(Args.create());
					serializeValue(value, element, LANG_STRING, secret);
				} catch (FunctorInvocationException e) {
					//
				}
			}
		} else {
			IDeclarationBlock argDeclarationBlock = declaration
					.getDeclarationBlock();
			if (argDeclarationBlock != null) {
				serializeDeclarationElements(
						argDeclarationBlock.getDeclarationElements(), element,
						secret);
			}
		}
	}

	public void serializeDeclarationBlock(IDeclarationBlock declarationBlock,
			IElement element) {
		if (declarationBlock == null || declarationBlock.size() == 0) {
			return;
		}
		IElement declarationsElement = element.newElement("declarations"); //$NON-NLS-1$
		IDeclarationElement[] elements = declarationBlock
				.getDeclarationElements();
		serializeDeclarationElements(elements, declarationsElement, false);
	}

	public void serializeDeclarationElement(IDeclarationElement declaration,
			IElement element, boolean secret) {
		if (declaration.hasModifier(MOD_SYSTEMTRANSIENT)) {
			return;
		}
		if (declaration instanceof ArgumentDeclaration) {
			ArgumentDeclaration argDeclaration = (ArgumentDeclaration) declaration;
			IElement argElement = element.newElement(ELEMENT_ARG);
			serializeArgumentDeclaration(argDeclaration, argElement, secret);
		}
	}

	public void serializeDeclarationElements(IDeclarationElement[] elements,
			IElement declarationsElement, boolean secret) {
		for (int i = 0; i < elements.length; i++) {
			IDeclarationElement element = elements[i];
			serializeDeclarationElement(element, declarationsElement, secret);
		}
	}

	protected void serializeValue(Object value, IElement element,
			String language, boolean secret) {
		if (value instanceof Secret) {
			if (!secret) {
				element.setAttributeValue(ATTR_MODIFIERS, MOD_SECRET);
			}
			secret = true;
			value = ((Secret) value).getValue();
		}
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
				try {
					element.setAttributeValue(ATTR_VALUE, CryptoEnvironment
							.get().encryptStringEncoded(value.toString()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				element.setAttributeValue(ATTR_VALUE, value.toString());
			}
		}
	}

	public void setDefaultLanguage(String defaultlanguage) {
		this.defaultLanguage = defaultlanguage;
	}

}
