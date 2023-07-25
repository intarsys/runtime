/*
 * Copyright (c) 2012, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.infoset;

import static de.intarsys.tools.infoset.PACKAGE.Log;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.bean.BeanContainer;
import de.intarsys.tools.codeexit.CodeExit;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.crypto.CryptoTools;
import de.intarsys.tools.crypto.Secret;
import de.intarsys.tools.crypto.SecretResolver;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.Mode;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.factory.CommonFactory;
import de.intarsys.tools.factory.FactoryTools;
import de.intarsys.tools.factory.IFactory;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclarator;
import de.intarsys.tools.functor.DeclarationBlock;
import de.intarsys.tools.functor.DeclarationException;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.FunctorFieldHandler;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.lang.LangTools;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.IFieldHandler;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.Converter;
import de.intarsys.tools.string.StringTools;

public class ElementTools {

	static class ElementProxyInvocationHandler implements InvocationHandler, IAttributeSupport {

		/*
		 * this a a singleton (one handler per proxy) - so we can afford a
		 * single attributes map.
		 */
		private AttributeMap attributes;

		private Map<String, IFunctor<?>> functors = new HashMap<>();

		public ElementProxyInvocationHandler(IElement element, ClassLoader loader) throws ObjectCreationException {
			createFunctors(element, loader);
		}

		protected void createFunctors(IElement element, ClassLoader loader)
				throws ObjectCreationException {
			IElement implementation = element.element(ELEMENT_IMPLEMENTATION);
			if (implementation != null) {
				for (Iterator<IElement> it = implementation.elementIterator(ELEMENT_METHOD); it.hasNext();) {
					IElement methodElement = it.next();
					String name = methodElement.attributeValue(ATTR_NAME, null);
					IFunctor<?> functor = ElementTools.createFunctor(this, methodElement, null, loader);
					functors.put(name, functor);
				}
			}
			IFunctor<?> tempFunctor;
			tempFunctor = new IFunctor<Object>() {

				@Override
				public Object perform(IFunctorCall call) throws FunctorException {
					return getAttribute(call.getArgs().get(0));
				}
			};
			functors.put("getAttribute", tempFunctor); //$NON-NLS-1$
			tempFunctor = new IFunctor<Object>() {

				@Override
				public Object perform(IFunctorCall call) throws FunctorException {
					return setAttribute(call.getArgs().get(0), call.getArgs().get(1));
				}
			};
			functors.put("setAttribute", tempFunctor); //$NON-NLS-1$
			tempFunctor = new IFunctor<Object>() {

				@Override
				public Object perform(IFunctorCall call) throws FunctorException {
					return removeAttribute(call.getArgs().get(0));
				}
			};
			functors.put("removeAttribute", tempFunctor); //$NON-NLS-1$
		}

		@Override
		public Object getAttribute(Object key) {
			if (attributes == null) {
				return null;
			}
			return attributes.get(key);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String name = method.getName();
			IFunctor<?> functor = functors.get(name);
			if (functor == null) {
				return null;
			}
			Args functorArgs = new Args(args);
			FunctorCall call = new FunctorCall(proxy, functorArgs);
			return functor.perform(call);
		}

		@Override
		public Object removeAttribute(Object key) {
			if (attributes == null) {
				return null;
			}
			return attributes.remove(key);
		}

		@Override
		public Object setAttribute(Object key, Object o) {
			if (attributes == null) {
				attributes = new AttributeMap();
			}
			return attributes.put(key, o);
		}
	}

	public static final String PROPERTY_REMOVE = "remove"; //$NON-NLS-1$

	public static final String PROPERTY_INSERT = "insert"; //$NON-NLS-1$

	public static final String ELEMENT_SET = "set"; //$NON-NLS-1$

	public static final String PROPERTY_SET = "set"; //$NON-NLS-1$

	public static final String ELEMENT_GET = "get"; //$NON-NLS-1$

	public static final String ATTR_REF = "ref"; //$NON-NLS-1$

	public static final String ELEMENT_ACCESSOR = "accessor"; //$NON-NLS-1$

	public static final String ELEMENT_NULL = "null"; //$NON-NLS-1$

	public static final String ELEMENT_ARGS = "args"; //$NON-NLS-1$

	public static final String ELEMENT_VALUE = "value"; //$NON-NLS-1$

	public static final String ELEMENT_OBJECT = "object"; //$NON-NLS-1$

	public static final String ATTR_TYPE = "type"; //$NON-NLS-1$

	public static final String ELEMENT_PERFORM = "perform"; //$NON-NLS-1$

	public static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	public static final String ATTR_FACTORY = "factory"; //$NON-NLS-1$

	private static final Pattern PATTERN_SPLIT = Pattern.compile("\\."); //$NON-NLS-1$

	public static final String ATTR_NAME = "name"; //$NON-NLS-1$

	public static final String ELEMENT_IMPLEMENTATION = "implementation"; //$NON-NLS-1$

	public static final String ELEMENT_METHOD = "method"; //$NON-NLS-1$

	public static void addDeclPropertyArray(IElement element, String propertyName, String elementName) {
		element.addDeclaration(new ArrayDeclaration(propertyName, elementName));
	}

	protected static InvocationHandler basicCreateInvocationHandler(IElement element, ClassLoader loader)
			throws ObjectCreationException {
		return new ElementProxyInvocationHandler(element, loader);
	}

	protected static Object basicCreateProxy(IElement element, Class<?>[] clazzes, ClassLoader loader)
			throws ObjectCreationException {
		// always implement IAttributeSupport
		Class<?>[] extendedClasses = new Class<?>[clazzes.length + 1];
		System.arraycopy(clazzes, 0, extendedClasses, 0, clazzes.length);
		extendedClasses[extendedClasses.length - 1] = IAttributeSupport.class;
		InvocationHandler handler = basicCreateInvocationHandler(element, loader);
		return Proxy.newProxyInstance(loader, extendedClasses, handler);
	}

	protected static Class<?> basicCreateProxyClass(Class<?>[] clazzes, ClassLoader loader)
			throws ObjectCreationException {
		Class<?>[] extendedClasses = new Class[clazzes.length + 1];
		System.arraycopy(clazzes, 0, extendedClasses, 0, clazzes.length);
		extendedClasses[extendedClasses.length - 1] = IAttributeSupport.class;
		return Proxy.getProxyClass(loader, extendedClasses); // NOSONAR class is required here
	}

	public static void copy(IElement to, IElement from) {
		copyAttributes(to, from);
		copyElements(to, from);
		copyText(to, from);
	}

	public static void copyAttributes(IElement to, IElement from) {
		if (to == null || from == null) {
			return;
		}
		for (Iterator<String> iter = from.attributeNames(); iter.hasNext();) {
			String name = iter.next();
			to.setAttributeTemplate(name, from.attributeTemplate(name));
		}
	}

	public static void copyElements(IElement to, IElement from) {
		if (to == null || from == null) {
			return;
		}
		for (Iterator<IElement> iter = from.elementIterator(); iter.hasNext();) {
			IElement child = iter.next();
			IElement newTo = to.newElement(child.getName());
			copy(newTo, child);
		}
	}

	public static void copyText(IElement to, IElement from) {
		if (to == null || from == null) {
			return;
		}
		to.setText(from.getText());
	}

	public static <T> Class<T> createClass(IElement element, String classAttribute, Class<T> expectedClass,
			Object context) throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		String className = element.attributeValue(classAttribute, null);
		if (StringTools.isEmpty(className)) {
			return null;
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		String[] classNames = className.split("\\;"); //$NON-NLS-1$
		Class<?>[] clazzes = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			String tempName = classNames[i].trim();
			try {
				clazzes[i] = Class.forName(tempName, true, classLoader);
			} catch (ClassNotFoundException e) {
				throw new ObjectCreationException("class '" + className //$NON-NLS-1$
						+ "' not found", e); //$NON-NLS-1$
			}
		}
		Class<T> clazz;
		if (clazzes.length > 1) {
			clazz = (Class<T>) basicCreateProxyClass(clazzes, classLoader);
		} else {
			clazz = (Class<T>) clazzes[0];
		}
		return clazz;
	}

	protected static IFieldHandler createFieldHandler(IElement element, Object owner, Object context)
			throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		if (element.attributeValue(ATTR_CLASS, null) != null || element.attributeValue(ATTR_FACTORY, null) != null) {
			return ElementTools.createObject(element, IFieldHandler.class, context, Args.create());
		} else {
			FunctorFieldHandler tempAccessor = new FunctorFieldHandler();
			IElement getElement = element.element(ELEMENT_GET);
			if (getElement != null) {
				tempAccessor.setGetter(createFunctor(owner, getElement, null, context));
			}
			IElement setElement = element.element(ELEMENT_SET);
			if (setElement != null) {
				tempAccessor.setSetter(createFunctor(owner, setElement, null, context));
			}
			tempAccessor.setName(element.attributeValue(ATTR_NAME, "unknown"));
			return tempAccessor;
		}
	}

	/**
	 * This is a tool method to create an {@link IFunctor}. We use this because
	 * of a small deviation from the standard XML syntax. The functor can be
	 * either created "normally" or via an embedded "perform" element.
	 */
	public static IFunctor<?> createFunctor(Object owner, IElement element, String role, Object context)
			throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		IFunctor<?> functor = null;
		IElement codeExitElement = element.element(ELEMENT_PERFORM);
		if (codeExitElement != null) {
			functor = CodeExit.createFromElement(codeExitElement);
			((CodeExit<?>) functor).setOwner(owner);
			((CodeExit<?>) functor).setClassLoader(getClassLoader(context, CodeExit.class));
		} else {
			functor = ElementTools.createObject(element, role, IFunctor.class, context, Args.create());
		}
		return functor;
	}

	public static <T> T createObject(IElement element, Class<T> expectedClass, Object context, IArgs initProperties)
			throws ObjectCreationException {
		return createObject(element, (String) null, expectedClass, context, initProperties);
	}

	/**
	 * Create an object of type expectedClass as described in element. The object can stem from one the
	 * following sources:
	 *
	 * <dl>
	 * <dt>ref
	 * <dd>A reference to a registered object in a bean container
	 *
	 * <dt>class
	 * <dd>A new instance of the designated class
	 *
	 * <dt>factory
	 * <dd>A new instance created by the designated {@link IFactory}
	 * </dl>
	 *
	 * The implementation class is accessed via classLoader.
	 *
	 * @param element
	 *            The {@link IElement} holding the definition for the target object
	 * @param role
	 *            The role name for the attribute defining the target type (e.g."class")
	 * @param expectedClass
	 *            The expected type for the target.
	 * @param context
	 *            The context for the target instantiation.
	 * @param initProperties
	 *            Values to initialize the new object's fields
	 *
	 * @return the newly created object
	 * @throws ObjectCreationException
	 *             if the object creation fails
	 */
	public static <T> T createObject(IElement element, String role, Class<T> expectedClass, Object context,
			IArgs initProperties) throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		String attributeName;
		String target;
		if (role == null) {
			role = StringTools.EMPTY;
		}
		if (role.length() > 0) {
			attributeName = role;
			target = element.attributeValue(attributeName, null);
			if (target != null) {
				return createObjectFromClass(element, target, expectedClass, context, initProperties);
			}
		}
		attributeName = role + ATTR_REF;
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromContainer(element, target, expectedClass, context);
		}
		attributeName = role + ATTR_CLASS;
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromClass(element, target, expectedClass, context, initProperties);
		}
		attributeName = role + ATTR_FACTORY;
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromFactory(element, target, expectedClass, context);
		}
		Iterator<IElement> it = element.elementIterator();
		if (it.hasNext()) {
			return createObjectChild(null, it.next(), expectedClass, context);
		}
		throw new ObjectCreationException("can't create object (no 'ref', 'class' or 'factory'"); //$NON-NLS-1$
	}

	public static <T> T createObjectChild(Object owner, IElement element, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		String name = element.getName();
		if (ELEMENT_OBJECT.equals(name)) {
			return createObject(element, expectedClass, context, Args.create());
		} else if (ELEMENT_VALUE.equals(name)) {
			Object value;
			value = element.getText();
			String typeName = element.attributeValue(ATTR_TYPE, null);
			return (T) ObjectTools.convert(value, typeName, getClassLoader(context, expectedClass));
		} else if (ELEMENT_ARGS.equals(name)) {
			DeclarationBlock block = new DeclarationBlock(owner);
			new DeclarationIO().deserializeDeclarationElements(block, element, false);
			Args value = Args.create();
			try {
				new ArgumentDeclarator().apply(block, value);
			} catch (DeclarationException e) {
				throw new ObjectCreationException(e);
			}
			String typeName = element.attributeValue(ATTR_TYPE, null);
			return (T) ObjectTools.convert(value, typeName, getClassLoader(context, expectedClass));
		} else if (ELEMENT_NULL.equals(name)) {
			return null;
		} else if (ELEMENT_PERFORM.equals(name)) {
			CodeExit<?> functor = CodeExit.createFromElement(element);
			functor.setOwner(owner);
			functor.setClassLoader(getClassLoader(context, expectedClass));
			try {
				return (T) functor.perform(FunctorCall.noargs(owner));
			} catch (FunctorException e) {
				throw new ObjectCreationException(e);
			}
		} else if (ELEMENT_ACCESSOR.equals(name)) {
			return (T) createFieldHandler(element, owner, context);
		} else {
			throw new ObjectCreationException("unknown value element '" + name //$NON-NLS-1$
					+ "'"); //$NON-NLS-1$
		}
	}

	protected static <T> T createObjectFromClass(IElement element, String className, Class<T> expectedClass,
			Object context, IArgs initProperties) throws ObjectCreationException {
		if (className == null) {
			throw new ObjectCreationException("class name missing"); //$NON-NLS-1$
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		String[] classNames = className.split("\\;"); //$NON-NLS-1$
		Class<?>[] clazzes = new Class[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			String tempName = classNames[i].trim();
			try {
				clazzes[i] = Class.forName(tempName, false, classLoader);
			} catch (ClassNotFoundException e) {
				throw new ObjectCreationException("class '" + className //$NON-NLS-1$
						+ "' not found", e); //$NON-NLS-1$
			}
		}
		Object object;
		if (clazzes.length > 1 || clazzes[0].isInterface()) {
			object = basicCreateProxy(element, clazzes, classLoader);
		} else {
			object = ObjectTools.createObject(clazzes[0], expectedClass);
		}
		try {
			if (object instanceof IContextSupport) {
				// context = classLoader?
				((IContextSupport) object).setContext(context);
			}
			if (object instanceof IClassLoaderAccess) {
				((IClassLoaderAccess) object).setClassLoader(classLoader);
			}
			if (object instanceof IElementConfigurable) {
				((IElementConfigurable) object).configure(element);
			}
			setProperties(object, element, classLoader);
			ObjectTools.setProperties(object, initProperties);
			ObjectTools.initObject(object);
		} catch (ObjectCreationException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
		return (T) object;
	}

	/**
	 * Lookup an object in the current bean container
	 */
	protected static <T> T createObjectFromContainer(IElement element, String refName, Class<T> expectedClass,
			Object context) throws ObjectCreationException {
		T object = BeanContainer.get().lookupBean(refName, expectedClass);
		// reconfigure object...
		try {
			if (object instanceof IElementConfigurable) {
				((IElementConfigurable) object).configure(element);
			}
			setProperties(object, element, context);
		} catch (ObjectCreationException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
		return object;
	}

	protected static <T> T createObjectFromFactory(IElement element, String factoryName, Class<T> expectedClass,
			Object context) throws ObjectCreationException {
		if (factoryName == null) {
			throw new ObjectCreationException("factory name missing"); //$NON-NLS-1$
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		IFactory<T> factory = (IFactory<T>) FactoryTools.lookupFactoryFuzzy(factoryName, classLoader);
		if (factory == null) {
			throw new ObjectCreationException("factory '" + factoryName + "' missing"); //$NON-NLS-1$
		}
		IArgs args = Args.create();
		args.put(CommonFactory.ARG_CONTEXT, context);
		args.put(CommonFactory.ARG_CONFIGURATION, element);
		// the IFactory is required to make container specific initialization on
		// its own! This will ensure consistent instance creation when using the
		// factory directly.
		return factory.createInstance(args);
	}

	public static <T> T createPropertyValue(Object owner, IElement element, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		IElement valueElement = null;
		Iterator<IElement> itElement = element.elementIterator();
		if (itElement.hasNext()) {
			valueElement = itElement.next();
			if (itElement.hasNext()) {
				throw new ObjectCreationException("too many children");
			}
			return createObjectChild(owner, valueElement, expectedClass, context);
		} else {
			String ref = element.attributeValue(ATTR_REF, null);
			if (ref != null) {
				return BeanContainer.get().lookupBean(ref, expectedClass);
			} else {
				String value = element.attributeValue(ELEMENT_VALUE, null);
				String typeName = element.attributeValue(ATTR_TYPE, null);
				return (T) ObjectTools.convert(value, typeName, getClassLoader(context, null));
			}
		}
	}

	public static boolean getBool(IElement element, String attributeName, boolean defaultValue) {
		return getBoolean(element, attributeName, defaultValue);
	}

	public static boolean getBoolean(IElement element, String attributeName, boolean defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value != null) {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
				//
			}
		}
		return defaultValue;
	}

	public static Boolean getBooleanObject(IElement element, String attributeName, Boolean defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (!StringTools.isEmpty(value)) {
			try {
				return Boolean.valueOf(value);
			} catch (Exception e) {
				//
			}
		}
		return defaultValue;
	}

	public static char[] getCharArray(IElement element, String attributeName, char[] defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value == null) {
			return defaultValue;
		}
		return value.toCharArray();
	}

	protected static <T> ClassLoader getClassLoader(Object context, Class<T> expectedClass) {
		ClassLoader classLoader = null;
		if (context instanceof ClassLoader) {
			classLoader = (ClassLoader) context;
		} else if (context instanceof IClassLoaderSupport) {
			classLoader = ((IClassLoaderSupport) context).getClassLoader();
		}
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		if (classLoader == null && expectedClass != null) {
			// as good as any
			classLoader = expectedClass.getClassLoader();
		}
		return classLoader;
	}

	/**
	 * The argument value at <code>name</code> as a {@link Color}. If the
	 * argument value is not provided or not convertible,
	 * <code>defaultValue</code>is returned.
	 * 
	 * @return The argument value at <code>name</code> as a {@link Color}.
	 */
	public static Color getColor(IElement element, String name, Color defaultValue) {
		if (element == null) {
			return defaultValue;
		}
		String optionValue = element.attributeValue(name, null);
		if (optionValue == null) {
			return defaultValue;
		}
		try {
			return Color.decode(optionValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static double getDouble(IElement element, String attributeName, double defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value != null) {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				//
			}
		}
		return defaultValue;
	}

	public static IElement getElement(IElement element, String elementName) {
		if (element == null) {
			return null;
		}
		return element.element(elementName);
	}

	public static <T extends EnumItem> T getEnumItem(IElement element, String attributeName, EnumMeta<T> meta) {
		String id = getString(element, attributeName, null);
		return meta.getItemOrDefault(id);
	}

	public static <T extends EnumItem> T getEnumItem(IElement element, String attributeName, EnumMeta<T> meta,
			T defaultValue) {
		String id = getString(element, attributeName, null);
		T result = null;
		if (id != null) {
			result = meta.getItem(id);
		}
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	public static float getFloat(IElement element, String attributeName, float defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value != null) {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
				//
			}
		}
		return defaultValue;
	}

	public static int getInt(IElement element, String attributeName, int defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		return Converter.asInteger(value, defaultValue);
	}

	public static long getLong(IElement element, String attributeName, long defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				//
			}
		}
		return defaultValue;
	}

	public static boolean getPathBoolean(IElement element, String path, boolean defaultValue) {
		IElement nextElement = element;
		String[] split = PATTERN_SPLIT.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getBoolean(nextElement, split[i], defaultValue);
	}

	public static double getPathDouble(IElement element, String path, double defaultValue) {
		IElement nextElement = element;
		String[] split = PATTERN_SPLIT.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getDouble(nextElement, split[i], defaultValue);
	}

	public static IElement getPathElement(IElement element, String path) {
		String[] split = PATTERN_SPLIT.split(path, 0);
		return getPathElement(element, split);
	}

	public static IElement getPathElement(IElement element, String... segments) {
		int count = segments.length - 1;
		int i = 0;
		IElement nextElement = element;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(segments[i]);
			i++;
		}
		return getElement(nextElement, segments[i]);
	}

	public static float getPathFloat(IElement element, String path, float defaultValue) {
		IElement nextElement = element;
		String[] split = PATTERN_SPLIT.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getFloat(nextElement, split[i], defaultValue);
	}

	public static int getPathInt(IElement element, String path, int defaultValue) {
		IElement nextElement = element;
		String[] split = PATTERN_SPLIT.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getInt(nextElement, split[i], defaultValue);
	}

	public static String getPathString(IElement element, String path, String defaultValue) {
		IElement nextElement = element;
		String[] split = PATTERN_SPLIT.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getString(nextElement, split[i], defaultValue);
	}

	/**
	 * Deserialize a {@link Secret} from element.
	 *
	 * <p>
	 * If the element contains plain text, the text is interpreted as a
	 * plaintext (unencoded) {@link Secret} and is simply hidden (see
	 * {@link Secret#hideTrimmed(char[])}).
	 *
	 * <p>
	 * Use this method if your code expects the secret to be in plaintext
	 * already. Encrypting/decrypting the serialized secret can be achieved via
	 * {@link #putSecretTemplate(IElement, String, Secret)} in this case.
	 */
	public static Secret getSecretHide(IElement element, String name, Secret defaultValue) {
		Object value = null;
		if (element != null) {
			value = element.attributeData(name, null);
		}
		try {
			Secret secret = CryptoTools.createSecret(value);
			if (CryptoTools.isEmpty(secret)) {
				return defaultValue;
			}
			return secret;
		} catch (Exception e) {
			Log.warn("element property '{}' value of type {} not supported", name, value);
			return defaultValue;
		}
	}

	/**
	 * Deserialize a {@link Secret} from element.
	 *
	 * <p>
	 * If the element contains plain text, the text is interpreted as an
	 * encrypted (encoded) {@link Secret} and parsed (see
	 * {@link Secret#parse(String)}).
	 *
	 * <p>
	 * Use this method if your code expects the serialized secret to be
	 * explicitly encoded.
	 */
	public static Secret getSecretParse(IElement element, String name, Secret defaultValue) {
		Object value = null;
		if (element != null) {
			value = element.attributeData(name, null);
		}
		if (LangTools.isEmpty(value)) {
			return defaultValue;
		}
		if (value instanceof Secret) {
			return (Secret) value;
		}
		if (value instanceof String) {
			// content is a plain encoded Secret
			return Secret.parse((String) value);
		}
		Log.warn("element property '{}' value of type {} not supported", name, value);
		return defaultValue;
	}

	/**
	 * Deserialize a {@link Secret} from element.
	 *
	 * <p>
	 * If the element contains plain text, the text is interpreted as a template
	 * yielding a {@link Secret} and parsed (see
	 * {@link SecretResolver#evaluate(String, IArgs)}).
	 *
	 * <p>
	 * Use this method if your code expects the serialized secret to be
	 * template-encoded.
	 */
	public static Secret getSecretTemplate(IElement element, String name, Secret defaultValue) {
		Object value = null;
		if (element != null) {
			value = element.attributeData(name, null);
		}
		if (LangTools.isEmpty(value)) {
			return defaultValue;
		}
		if (value instanceof String) {
			try {
				value = TemplateEvaluator.get(Mode.TRUSTED).evaluate((String) value, Args.create());
			} catch (EvaluationException e) {
				Log.warn(e.getMessage(), e);
			}
		}
		if (value instanceof Secret) {
			return (Secret) value;
		}
		Log.warn("element property '{}' value of type {} not supported", name, value);
		return defaultValue;
	}

	public static String getString(IElement element, String attributeName, String defaultValue) {
		if (element != null) {
			return element.attributeValue(attributeName, defaultValue);
		}
		return defaultValue;
	}

	public static IElement parseElement(String value) throws IOException {
		StringReader reader = new StringReader(value);
		return ElementFactory.get().parse(reader).getRootElement();
	}

	public static void putBoolean(IElement element, String name, Boolean value) {
		String tempValue = (value == null) ? null : value.toString();
		element.setAttributeValue(name, tempValue);
	}

	public static void putNumber(IElement element, String name, Number value) {
		String tempValue = (value == null) ? null : value.toString();
		element.setAttributeValue(name, tempValue);
	}

	/**
	 * Serialize the secret value to element template.
	 * <p>
	 * The element will contain the encoded secret as a template value of the form
	 * ${secret.&lt;encoding&gt;} and will be automatically recovered as a
	 * {@link Secret} if the correct {@link SecretResolver} is in place.
	 */
	public static void putSecretTemplate(IElement element, String name, Secret value) {
		String tempValue = (value == null || value.isEmpty()) ? null : "${secret." + value.getEncoded() + "}";
		element.setAttributeTemplate(name, tempValue);
	}

	/**
	 * Serialize the secret value to element value.
	 *
	 * <p>
	 * The element will contain the encoded secret as plain value and must be
	 * reconstructed with {@link Secret#parse(String)} or
	 * {@link #getSecretParse(IElement, String, Secret)}.
	 */
	public static void putSecretValue(IElement element, String name, Secret value) {
		String tempValue = (value == null || value.isEmpty()) ? null : value.getEncoded();
		element.setAttributeValue(name, tempValue);
	}

	public static void serializeArray(IElement container, String propertyName, String elementName,
			Collection<?> collection) throws ElementSerializationException {
		ElementTools.addDeclPropertyArray(container, propertyName, elementName);
		for (Object object : collection) {
			serializeObject(container, elementName, object);
		}
	}

	public static IElement serializeObject(IElement container, String propertyName, Object value)
			throws ElementSerializationException {
		if (value == null) {
			return null;
		}
		IElement newElement = container.newElement(propertyName);
		if (value instanceof IElementSerializable) {
			((IElementSerializable) value).serialize(newElement);
			newElement.setName(propertyName);
		} else {
			// as good as any...
			newElement.setAttributeValue("class", value.getClass().getName());
			newElement.setAttributeValue("value", value.toString());
		}
		return newElement;
	}

	public static void setCDATA(IElement element, String value) {
		String cdata = "<![CDATA[" + value + "]]>";
		element.setText(cdata);
	}

	/**
	 * Set properties in object based on the list of property information
	 * contained in element.
	 *
	 * <pre>
	 * ...
	 * &lt;object ...>
	 *    &lt;property name="foo" ...
	 *    &lt;property name="bar" ...
	 * &lt;/object>
	 * ...
	 * </pre>
	 */
	public static void setProperties(Object object, IElement element, Object context)
			throws FieldException, ObjectCreationException {
		Iterator<IElement> it = element.elementIterator("property");
		while (it.hasNext()) {
			IElement propertyElement = it.next();
			setProperty(object, propertyElement, context);
		}
	}

	/**
	 * Set a property in object based on the property information contained in element.
	 *
	 * <ul>
	 * <li>Long form
	 * 
	 * <pre>
	 *   ...
	 *   &lt;property name="foo" [operation="set|insert|remove"]>
	 *     &lt;object .../>
	 *   &lt;/property>
	 *   ...
	 * </pre>
	 *
	 * <li>Short form
	 * 
	 * <pre>
	 *   ...
	 *   &lt;property name="foo" value="bar" [type="classname"] />
	 *   ...
	 * </pre>
	 * </ul>
	 */
	public static void setProperty(Object object, IElement element, Object context)
			throws FieldException, ObjectCreationException {
		String property = element.attributeValue("name", null); //$NON-NLS-1$
		String operation = element.attributeValue("operation", PROPERTY_SET); //$NON-NLS-1$
		Object value = createPropertyValue(object, element, Object.class, context);
		if (PROPERTY_SET.equals(operation)) {
			ObjectTools.set(object, property, value);
		} else if (PROPERTY_INSERT.equals(operation)) {
			ObjectTools.insert(object, property, value);
		} else if (PROPERTY_REMOVE.equals(operation)) {
			ObjectTools.remove(object, property, value);
		} else {
			throw new ObjectCreationException("unknown property operation '" + operation + "'");
		}
	}

	/**
	 * Create an {@link IElement} from args. This is useful for bridging
	 * arguments to the configuration utility {@link IElementConfigurable}.
	 *
	 * <p>
	 * {@link IArgs} are converted using the following rules:
	 *
	 * <ol>
	 * <li>If "args" contains named bindings, an {@link IElement} is created for
	 * the "args" structure and all named bindings are stored in the element
	 * where a non-{@link IArgs} binding is serialized as an attribute value and
	 * an {@link IArgs} binding is added to the element recursively.
	 *
	 * <li>If "args" contains indexed bindings, an {@link IElement} is created for
	 * each binding and added to the current parent.
	 * </ol>
	 *
	 * <pre>
	 * {
	 * 	a -> "b"
	 * 	x -> "y"
	 * }
	 *
	 * &lt;arg a="b" x="y"/>
	 * </pre>
	 *
	 * <pre>
	 * {
	 * 	a -> "b"
	 * 	x -> {
	 * 		foo = "bar"
	 * 	}
	 * 	y -> {
	 * 		gnu = "gnat"
	 * 	}
	 * }
	 *
	 * &lt;arg a="b">
	 *   &lt;x foo="bar"/>
	 *   &lt;y gnu="gnat"/>
	 * &lt;/arg>
	 * </pre>
	 */
	public static IElement toElement(IArgs args) {
		if (args == null) {
			return null;
		}
		IDocument document = ElementFactory.get().createDocument();
		IElement result = toElement(document, null, "arg", args);
		document.setRootElement(result);
		return result;
	}

	protected static IElement toElement(IDocument document, IElement parent, String parentName, IArgs args) {
		Iterator<IBinding> it = args.bindings();
		IElement argsElement = null;
		while (it.hasNext()) {
			IBinding binding = it.next();
			String name = binding.getName();
			Object value = binding.getValue();
			if (value == null) {
				continue;
			}
			if (name != null && argsElement == null) {
				if (parent == null) {
					argsElement = ElementFactory.get().createElement(parentName);
				} else {
					argsElement = parent.newElement(parentName);
				}
			}
			if (value instanceof IArgs) {
				if (name == null) {
					toElement(document, parent, parentName, (IArgs) value);
				} else {
					toElement(document, argsElement, name, (IArgs) value);
				}
			} else {
				if (name == null) {
					// indexed - no sense
				} else {
					String string = String.valueOf(value);
					argsElement.setAttributeValue(name, string);
				}
			}
		}
		if (parent == null && argsElement == null) {
			argsElement = ElementFactory.get().createElement(parentName);
		}
		return argsElement;
	}

	/**
	 * Return the string representation for an attribute value.
	 */
	public static String toString(Object value) {
		if (value instanceof Secret) {
			return CryptoTools.getString(((Secret) value), null);
		}
		return String.valueOf(value);
	}

	public static void write(ContentHandler handler, IElement element) throws SAXException {
		writeElementStart(handler, element);
		writeElementContent(handler, element);
		writeElementEnd(handler, element);
	}

	protected static void writeElementContent(ContentHandler handler, IElement element) throws SAXException {
		writeElementText(handler, element);
		for (Iterator<IElement> iter = element.elementIterator(); iter.hasNext();) {
			IElement child = iter.next();
			write(handler, child);
		}
	}

	protected static void writeElementEnd(ContentHandler handler, IElement element) throws SAXException {
		handler.endElement(StringTools.EMPTY, element.getName(), element.getName());
	}

	protected static void writeElementStart(ContentHandler handler, IElement element) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		for (Iterator<String> iter = element.attributeNames(); iter.hasNext();) {
			String name = iter.next();
			attributes.addAttribute(StringTools.EMPTY, name, name, "CDATA", element.attributeTemplate(name));
		}
		handler.startElement(StringTools.EMPTY, element.getName(), element.getName(), attributes);
	}

	protected static void writeElementText(ContentHandler handler, IElement element) throws SAXException {
		if (element.getText() != null) {
			char[] chars = element.getText().toCharArray();
			handler.characters(chars, 0, chars.length);
		}
	}

	private ElementTools() {
		super();
	}
}
