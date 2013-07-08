/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.infoset;

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.bean.BeanContainer;
import de.intarsys.tools.codeexit.CodeExit;
import de.intarsys.tools.component.IContextSupport;
import de.intarsys.tools.component.IInitializeable;
import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;
import de.intarsys.tools.factory.CommonFactory;
import de.intarsys.tools.factory.IFactory;
import de.intarsys.tools.factory.Outlet;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.functor.ArgumentDeclarator;
import de.intarsys.tools.functor.DeclarationBlock;
import de.intarsys.tools.functor.DeclarationException;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.FunctorFieldHandler;
import de.intarsys.tools.functor.FunctorInvocationException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.functor.IFunctorCall;
import de.intarsys.tools.functor.common.DeclarationIO;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.IClassLoaderAccess;
import de.intarsys.tools.reflect.IClassLoaderSupport;
import de.intarsys.tools.reflect.IFieldHandler;
import de.intarsys.tools.reflect.ObjectCreationException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;

public class ElementTools {

	static class ElementProxyInvocationHandler implements InvocationHandler,
			IAttributeSupport {

		/*
		 * this a a singleton (one handler per proxy) - so we can afford a
		 * single attributes map.
		 */
		private AttributeMap attributes;

		private Map<String, IFunctor> functors = new HashMap<String, IFunctor>();

		public ElementProxyInvocationHandler(IElement element, Class[] clazzes,
				ClassLoader loader) throws ObjectCreationException {
			createFunctors(element, clazzes, loader);
		}

		protected void createFunctors(IElement element, Class[] clazzes,
				ClassLoader loader) throws ObjectCreationException {
			IElement implementation = element.element("implementation"); //$NON-NLS-1$
			if (implementation != null) {
				for (Iterator<IElement> it = implementation
						.elementIterator("method"); it //$NON-NLS-1$
						.hasNext();) {
					IElement methodElement = it.next();
					String name = methodElement.attributeValue("name", null); //$NON-NLS-1$
					IFunctor functor = ElementTools.createFunctor(this,
							methodElement, loader);
					functors.put(name, functor);
				}
			}
			IFunctor tempFunctor;
			tempFunctor = new IFunctor() {
				@Override
				public Object perform(IFunctorCall call)
						throws FunctorInvocationException {
					return getAttribute(call.getArgs().get(0));
				}
			};
			functors.put("getAttribute", tempFunctor); //$NON-NLS-1$
			tempFunctor = new IFunctor() {
				@Override
				public Object perform(IFunctorCall call)
						throws FunctorInvocationException {
					return setAttribute(call.getArgs().get(0), call.getArgs()
							.get(1));
				}
			};
			functors.put("setAttribute", tempFunctor); //$NON-NLS-1$
			tempFunctor = new IFunctor() {
				@Override
				public Object perform(IFunctorCall call)
						throws FunctorInvocationException {
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
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String name = method.getName();
			IFunctor functor = functors.get(name);
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

	// TODO move elsewhere?
	public static final String ATTR_FACTORY = "factory"; //$NON-NLS-1$

	private static final Logger Log = PACKAGE.Log;

	private static Pattern splitPattern = Pattern.compile("\\."); //$NON-NLS-1$

	static private long postProcessingTime = 0;

	protected static InvocationHandler basicCreateInvocationHandler(
			IElement element, Class[] clazzes, ClassLoader loader)
			throws ObjectCreationException {
		return new ElementProxyInvocationHandler(element, clazzes, loader);
	}

	protected static Object basicCreateProxy(IElement element, Class[] clazzes,
			ClassLoader loader) throws ObjectCreationException {
		// always implement IAttributeSupport
		Class[] extendedClasses = new Class[clazzes.length + 1];
		System.arraycopy(clazzes, 0, extendedClasses, 0, clazzes.length);
		extendedClasses[extendedClasses.length - 1] = IAttributeSupport.class;
		InvocationHandler handler = basicCreateInvocationHandler(element,
				extendedClasses, loader);
		return Proxy.newProxyInstance(loader, extendedClasses, handler);
	}

	protected static Class basicCreateProxyClass(IElement element,
			Class[] clazzes, ClassLoader loader) throws ObjectCreationException {
		Class[] extendedClasses = new Class[clazzes.length + 1];
		System.arraycopy(clazzes, 0, extendedClasses, 0, clazzes.length);
		extendedClasses[extendedClasses.length - 1] = IAttributeSupport.class;
		return Proxy.getProxyClass(loader, extendedClasses);
	}

	public static <T> Class<T> createClass(IElement element,
			String classAttribute, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		String className = element.attributeValue(classAttribute, null);
		if (StringTools.isEmpty(className)) {
			return null;
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		String[] classNames = className.split("\\;"); //$NON-NLS-1$
		Class[] clazzes = new Class[classNames.length];
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
			clazz = basicCreateProxyClass(element, clazzes, classLoader);
		} else {
			clazz = clazzes[0];
		}
		return clazz;
	}

	protected static IFieldHandler createFieldHandler(IElement element,
			Object owner, Object context) throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		if (element.attributeValue("class", null) != null //$NON-NLS-1$
				|| element.attributeValue(ATTR_FACTORY, null) != null) {
			return ElementTools.createObject(element, IFieldHandler.class,
					context);
		} else {
			FunctorFieldHandler tempAccessor = new FunctorFieldHandler();
			IElement getElement = element.element("get"); //$NON-NLS-1$
			if (getElement != null) {
				tempAccessor
						.setGetter(createFunctor(owner, getElement, context));
			}
			IElement setElement = element.element("set"); //$NON-NLS-1$
			if (setElement != null) {
				tempAccessor
						.setSetter(createFunctor(owner, setElement, context));
			}
			tempAccessor.setName(element.attributeValue("name", "unknown"));
			return tempAccessor;
		}
	}

	/**
	 * This is a tool method to create an {@link IFunctor}. We use this because
	 * of a small deviation from the standard XML syntax. The functor can be
	 * either created "normally" or via an embedded "perform" element.
	 * 
	 * @param owner
	 * @param element
	 * @param context
	 * @return
	 * @throws ObjectCreationException
	 */
	public static IFunctor createFunctor(Object owner, IElement element,
			Object context) throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		IFunctor functor = null;
		if (element.attributeValue("class", null) != null //$NON-NLS-1$
				|| element.attributeValue(ATTR_FACTORY, null) != null) {
			functor = ElementTools.createObject(element, IFunctor.class,
					context);
		} else {
			IElement codeExitElement = element.element("perform"); //$NON-NLS-1$
			if (codeExitElement != null) {
				functor = CodeExit.createFromElement(codeExitElement);
				((CodeExit) functor).setOwner(owner);
				((CodeExit) functor).setClassLoader(getClassLoader(context,
						CodeExit.class));
			}
		}
		return functor;
	}

	public static <T> T createObject(IElement element, Class<T> expectedClass,
			Object context) throws ObjectCreationException {
		return createObject(element, (String) null, expectedClass, context);
	}

	/**
	 * Create an object of type expectedClass as described in element.
	 * 
	 * The object can stem from one the following sources:
	 * 
	 * <ul>
	 * <li>ref</li> A reference to a registered object in a bean container
	 * <li>class</li> A new instance of the designated class
	 * <li>factory</li> A new instance created by the designated
	 * {@link IFactory}
	 * </ul>
	 * 
	 * The implementation class is accessed via classLoader.
	 * 
	 * @param element
	 * @param role
	 * @param expectedClass
	 * @param context
	 *            TODO
	 * @return
	 * @throws ObjectCreationException
	 */
	public static <T> T createObject(IElement element, String role,
			Class<T> expectedClass, Object context)
			throws ObjectCreationException {
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
				return createObjectFromClass(element, target, expectedClass,
						context);
			}
		}
		attributeName = role + "ref"; //$NON-NLS-1$
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromContainer(element, target, expectedClass,
					context);
		}
		attributeName = role + "class"; //$NON-NLS-1$
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromClass(element, target, expectedClass,
					context);
		}
		attributeName = role + ATTR_FACTORY;
		target = element.attributeValue(attributeName, null);
		if (target != null) {
			return createObjectFromFactory(element, target, expectedClass,
					context);
		}
		Iterator<IElement> it = element.elementIterator();
		if (it.hasNext()) {
			return createObjectChild(null, it.next(), expectedClass, context);
		}
		throw new ObjectCreationException(
				"can't create object (no 'ref', 'class' or 'factory'"); //$NON-NLS-1$
	}

	public static <T> T createObjectChild(Object owner, IElement element,
			Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		if (element == null) {
			return null;
		}
		String name = element.getName();
		if ("object".equals(name)) { //$NON-NLS-1$
			return createObject(element, expectedClass, context);
		} else if ("value".equals(name)) { //$NON-NLS-1$
			Object value;
			value = element.getText();
			String typeName = element.attributeValue("type", null);
			return (T) ObjectTools.convert(value, typeName,
					getClassLoader(context, expectedClass));
		} else if ("args".equals(name)) { //$NON-NLS-1$
			DeclarationBlock block = new DeclarationBlock(owner);
			new DeclarationIO().deserializeDeclarationElements(block, element,
					false);
			Args value = Args.create();
			try {
				new ArgumentDeclarator().apply(block, value);
			} catch (DeclarationException e) {
				throw new ObjectCreationException(e);
			}
			String typeName = element.attributeValue("type", null);
			return (T) ObjectTools.convert(value, typeName,
					getClassLoader(context, expectedClass));
		} else if ("null".equals(name)) { //$NON-NLS-1$
			return null;
		} else if ("perform".equals(name)) { //$NON-NLS-1$
			CodeExit functor = CodeExit.createFromElement(element);
			functor.setOwner(owner);
			functor.setClassLoader(getClassLoader(context, expectedClass));
			try {
				return (T) functor.perform(FunctorCall.noargs(owner));
			} catch (FunctorInvocationException e) {
				throw new ObjectCreationException(e);
			}
		} else if ("accessor".equals(name)) { //$NON-NLS-1$
			return (T) createFieldHandler(element, owner, context);
		} else {
			throw new ObjectCreationException("unknown value element '" + name //$NON-NLS-1$
					+ "'"); //$NON-NLS-1$
		}
	}

	protected static <T> T createObjectFromClass(IElement element,
			String className, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		if (className == null) {
			throw new ObjectCreationException("class name missing"); //$NON-NLS-1$
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		String[] classNames = className.split("\\;"); //$NON-NLS-1$
		Class[] clazzes = new Class[classNames.length];
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
				((IContextSupport) object).setContext(context);
			}
			if (object instanceof IClassLoaderAccess) {
				((IClassLoaderAccess) object).setClassLoader(classLoader);
			}
			if (object instanceof IElementConfigurable) {
				((IElementConfigurable) object).configure(element);
			}
			setProperties(object, element, classLoader);
			if (object instanceof IInitializeable) {
				((IInitializeable) object).initializeAfterConstruction();
			}
			postProcess(object);
		} catch (ObjectCreationException e) {
			throw e;
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
		return (T) object;
	}

	/**
	 * Lookup an object in the current bean container
	 * 
	 * @param element
	 * @param refName
	 * @param expectedClass
	 * @param classLoader
	 * @return
	 * @throws ObjectCreationException
	 */
	protected static <T> T createObjectFromContainer(IElement element,
			String refName, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
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

	protected static <T> T createObjectFromFactory(IElement element,
			String factoryName, Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		if (factoryName == null) {
			throw new ObjectCreationException("factory name missing"); //$NON-NLS-1$
		}
		ClassLoader classLoader = getClassLoader(context, expectedClass);
		IFactory<T> factory = (IFactory<T>) Outlet.get().lookupFactory(
				factoryName);
		if (factory == null) {
			try {
				factory = ObjectTools.createObject(factoryName, IFactory.class,
						classLoader);
				Outlet.get().registerFactory(factory);
				Log.log(Level.INFO, "created default factory '" + factoryName //$NON-NLS-1$
						+ "'"); //$NON-NLS-1$
			} catch (Exception e) {
				throw new ObjectCreationException(
						"factory '" + factoryName + "' missing"); //$NON-NLS-1$
			}
		}
		IArgs args = Args.create();
		args.put(CommonFactory.ARG_CONTEXT, context);
		args.put(CommonFactory.ARG_CONFIGURATION, element);
		// the IFactory is required to make container specific initialization on
		// its own! This ensure consistent instance creation when using the
		// factory directly.
		return factory.createInstance(args);
	}

	public static <T> T createPropertyValue(Object owner, IElement element,
			Class<T> expectedClass, Object context)
			throws ObjectCreationException {
		IElement valueElement = null;
		Iterator<IElement> itElement = element.elementIterator();
		if (itElement.hasNext()) {
			valueElement = itElement.next();
			if (itElement.hasNext()) {
				throw new ObjectCreationException("too many children");
			}
			return createObjectChild(owner, valueElement, expectedClass,
					context);
		} else {
			String ref = element.attributeValue("ref", null);
			if (ref != null) {
				return BeanContainer.get().lookupBean(ref, expectedClass);
			} else {
				String value = element.attributeValue("value", null);
				String typeName = element.attributeValue("type", null);
				return (T) ObjectTools.convert(value, typeName,
						getClassLoader(context, null));
			}
		}
	}

	public static boolean getBool(IElement element, String attributeName,
			boolean defaultValue) {
		return getBoolean(element, attributeName, defaultValue);
	}

	public static boolean getBoolean(IElement element, String attributeName,
			boolean defaultValue) {
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

	public static char[] getCharArray(IElement element, String attributeName,
			char[] defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value == null) {
			return defaultValue;
		}
		return value.toCharArray();
	}

	protected static <T> ClassLoader getClassLoader(Object context,
			Class<T> expectedClass) {
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
	 * @param args
	 * @param name
	 * @param defaultValue
	 * @return The argument value at <code>name</code> as a {@link Color}.
	 */
	public static Color getColor(IElement element, String name,
			Color defaultValue) {
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

	public static double getDouble(IElement element, String attributeName,
			double defaultValue) {
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

	public static <T extends EnumItem> T getEnumItem(IElement element,
			String attributeName, EnumMeta<T> meta) {
		String id = getString(element, attributeName, null);
		return meta.getItemOrDefault(id);
	}

	public static <T extends EnumItem> T getEnumItem(IElement element,
			String attributeName, EnumMeta<T> meta, T defaultValue) {
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

	public static float getFloat(IElement element, String attributeName,
			float defaultValue) {
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

	public static int getInt(IElement element, String attributeName,
			int defaultValue) {
		String value = null;
		if (element != null) {
			value = element.attributeValue(attributeName, null);
		}
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				//
			}
		}
		return defaultValue;
	}

	public static long getLong(IElement element, String attributeName,
			long defaultValue) {
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

	public static boolean getPathBoolean(IElement element, String path,
			boolean defaultValue) {
		IElement nextElement = element;
		String[] split = splitPattern.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getBoolean(nextElement, split[i], defaultValue);
	}

	public static double getPathDouble(IElement element, String path,
			double defaultValue) {
		IElement nextElement = element;
		String[] split = splitPattern.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getDouble(nextElement, split[i], defaultValue);
	}

	public static float getPathFloat(IElement element, String path,
			float defaultValue) {
		IElement nextElement = element;
		String[] split = splitPattern.split(path, 0);
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
		String[] split = splitPattern.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getInt(nextElement, split[i], defaultValue);
	}

	public static String getPathString(IElement element, String path,
			String defaultValue) {
		IElement nextElement = element;
		String[] split = splitPattern.split(path, 0);
		int count = split.length - 1;
		int i = 0;
		while (nextElement != null && i < count) {
			nextElement = nextElement.element(split[i]);
			i++;
		}
		return getString(nextElement, split[i], defaultValue);
	}

	public static String getString(IElement element, String attributeName,
			String defaultValue) {
		if (element != null) {
			return element.attributeValue(attributeName, defaultValue);
		}
		return defaultValue;
	}

	static public IElement parseElement(String value) throws IOException {
		StringReader reader = new StringReader(value);
		return ElementFactory.get().parse(reader).getRootElement();
	}

	static protected void postProcess(Object object)
			throws ObjectCreationException {
		long start = System.currentTimeMillis();
		Class clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		Method tempMethod = null;
		for (Method method : methods) {
			if (method.getAnnotation(PostConstruct.class) != null) {
				tempMethod = method;
			}
		}
		long duration = System.currentTimeMillis() - start;
		postProcessingTime = postProcessingTime + duration;
		Log.log(Level.INFO, "post processing took " + postProcessingTime
				+ " millis");
		if (tempMethod != null) {
			try {
				tempMethod.invoke(object);
			} catch (Exception e) {
				throw new ObjectCreationException(e);
			}
		}
	}

	public static void setCDATA(IElement element, String value) {
		String cdata = "<![CDATA[" + value + "]]>";
		element.setText(cdata);
	}

	/**
	 * Set properties in object based on the list of property information
	 * contained in element.
	 * <p>
	 * <code>
	 * ...
	 * <object ...>
	 *    <property name="foo" ...
	 *    <property name="bar" ...
	 * </object>
	 * ...
	 * </code>
	 * 
	 * @param object
	 * @param element
	 * @param classLoader
	 * @throws FieldException
	 * @throws ObjectCreationException
	 * @throws FunctorInvocationException
	 */
	public static void setProperties(Object object, IElement element,
			Object context) throws FieldException, ObjectCreationException,
			FunctorInvocationException {
		Iterator<IElement> it = element.elementIterator("property");
		while (it.hasNext()) {
			IElement propertyElement = it.next();
			setProperty(object, propertyElement, context);
		}
	}

	/**
	 * Set a property in object based on the property information contained in
	 * element.
	 * <p>
	 * Long form <code>
	 * ...
	 * <property name="foo" [operation="set|insert|remove"]>
	 *    <object .../>
	 * </property>
	 * ...
	 * </code>
	 * 
	 * Short form <code>
	 * ...
	 * <property name="foo" value="bar" [type="classname"] />
	 * ...
	 * </code>
	 * 
	 * @param object
	 * @param element
	 * @param classLoader
	 * @throws FieldException
	 * @throws ObjectCreationException
	 * @throws FunctorInvocationException
	 */
	public static void setProperty(Object object, IElement element,
			Object context) throws FieldException, ObjectCreationException,
			FunctorInvocationException {
		String property = element.attributeValue("name", null); //$NON-NLS-1$
		String operation = element.attributeValue("operation", "set"); //$NON-NLS-1$
		Object value = createPropertyValue(object, element, Object.class,
				context);
		if ("set".equals(operation)) {
			ObjectTools.set(object, property, value);
		} else if ("insert".equals(operation)) {
			ObjectTools.insert(object, property, value);
		} else if ("remove".equals(operation)) {
			ObjectTools.remove(object, property, value);
		} else {
			throw new ObjectCreationException("unknown property operation '"
					+ operation + "'");
		}
	}

	/**
	 * Create an {@link Element} from args. This is useful for bridging
	 * arguments to the configuration utility {@link IElementConfigurable}.
	 * <p>
	 * {@link IArgs} are converted using the following rules:<br>
	 * 
	 * 1) If "args" contains named bindings, an {@link Element} is created for
	 * the "args" structure and all named bindings are stored in the element
	 * where a non-{@link IArgs} binding is serialized as an attribute value and
	 * an {@link IArgs} binding is added to the element recursively.<br>
	 * 
	 * 2) If "args" contains indexed bindings, an {@link Element} is created for
	 * each binding and added to the current parent.<br>
	 * 
	 * 3)
	 * 
	 * @param args
	 * @return
	 */
	public static IElement toElement(IArgs args) {
		IDocument document = ElementFactory.get().createDocument();
		return toElement(document, null, "arg", args);
	}

	protected static IElement toElement(IDocument document, IElement parent,
			String parentName, IArgs args) {
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
					argsElement = ElementFactory.get()
							.createElement(parentName);
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

	public static void write(ContentHandler handler, IElement element)
			throws SAXException {
		writeElementStart(handler, element);
		writeElementContent(handler, element);
		writeElementEnd(handler, element);
	}

	protected static void writeElementContent(ContentHandler handler,
			IElement element) throws SAXException {
		writeElementText(handler, element);
		for (Iterator<IElement> iter = element.elementIterator(); iter
				.hasNext();) {
			IElement child = iter.next();
			write(handler, child);
		}
	}

	protected static void writeElementEnd(ContentHandler handler,
			IElement element) throws SAXException {
		handler.endElement(null, element.getName(), null);
	}

	protected static void writeElementStart(ContentHandler handler,
			IElement element) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();
		for (Iterator<String> iter = element.attributeNames(); iter.hasNext();) {
			String name = iter.next();
			attributes.addAttribute(null, name, null, "CDATA",
					element.attributeTemplate(name));
		}
		handler.startElement(null, element.getName(), null, attributes);
	}

	protected static void writeElementText(ContentHandler handler,
			IElement element) throws SAXException {
		if (element.getText() != null) {
			char[] chars = element.getText().toCharArray();
			handler.characters(chars, 0, chars.length);
		}
	}
}
