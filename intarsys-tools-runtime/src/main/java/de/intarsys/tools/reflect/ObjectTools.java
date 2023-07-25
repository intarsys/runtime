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
package de.intarsys.tools.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import de.intarsys.tools.component.IInitializeable;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;
import de.intarsys.tools.converter.IConverter;
import de.intarsys.tools.converter.NoopConverter;
import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.FunctorException;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.string.Token;

/**
 * A tool class for convenient object related tasks.
 * <p>
 * This tool contains some simple reflection implementations.
 * 
 */
public class ObjectTools {

	class Any {
		// placeholder for runtime null values
	}

	public static final Object[] EMPTY_PARAMETERS = new Object[0];

	public static final Class[] EMPTY_PARAMETERTYPES = new Class[0];

	public static final String GET_PREFIX = "get"; //$NON-NLS-1$

	public static final String IS_PREFIX = "is"; //$NON-NLS-1$

	private static final Map<String, Class> PRIMITIVE_CLASSES = new HashMap<>();

	private static final Map<Class, Class> PRIMITIVE_WRAPPER = new HashMap<>();

	static {
		PRIMITIVE_CLASSES.put("int", Integer.TYPE);
		PRIMITIVE_CLASSES.put("long", Long.TYPE);
		PRIMITIVE_CLASSES.put("double", Double.TYPE);
		PRIMITIVE_CLASSES.put("float", Float.TYPE);
		PRIMITIVE_CLASSES.put("bool", Boolean.TYPE);
		PRIMITIVE_CLASSES.put("char", Character.TYPE);
		PRIMITIVE_CLASSES.put("byte", Byte.TYPE);
		PRIMITIVE_CLASSES.put("void", Void.TYPE);
		PRIMITIVE_CLASSES.put("short", Short.TYPE);

		PRIMITIVE_WRAPPER.put(Integer.TYPE, Integer.class);
		PRIMITIVE_WRAPPER.put(Long.TYPE, Long.class);
		PRIMITIVE_WRAPPER.put(Double.TYPE, Double.class);
		PRIMITIVE_WRAPPER.put(Float.TYPE, Float.class);
		PRIMITIVE_WRAPPER.put(Boolean.TYPE, Boolean.class);
		PRIMITIVE_WRAPPER.put(Character.TYPE, Character.class);
		PRIMITIVE_WRAPPER.put(Byte.TYPE, Byte.class);
		PRIMITIVE_WRAPPER.put(Void.TYPE, Void.class);
		PRIMITIVE_WRAPPER.put(Short.TYPE, Short.class);
	}

	private static final IConverter DEFAULT_CONVERTER = new NoopConverter();

	public static Object basicGet(Object object, String name) throws FieldException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		Exception ex = null;
		if (object instanceof IBasicAccessSupport) {
			try {
				return ((IBasicAccessSupport) object).basicGetValue(name);
			} catch (FieldNotFoundException e) {
				// ignore exception, supporting "mixed mode" implementations
				ex = ex == null ? e : ex; // NOSONAR
			}
		}
		if (object instanceof Map) {
			return ((Map) object).get(name);
		}
		if (object instanceof IArgs) {
			return ((IArgs) object).get(name);
		}
		try {
			Method method = findGetter(object.getClass(), name);
			return method.invoke(object, (Object[]) null);
		} catch (Exception e) {
			ex = ex == null ? e : ex; // NOSONAR
		}
		try {
			Field field = object.getClass().getField(name);
			return field.get(object);
		} catch (Exception e) {
			ex = ex == null ? e : ex; // NOSONAR
		}
		try {
			Method method = object.getClass().getMethod(name, (Class[]) null);
			return method.invoke(object, (Object[]) null);
		} catch (Exception e) {
			ex = ex == null ? e : ex; // NOSONAR
		}
		if (ex instanceof FieldException) {
			throw (FieldException) ex;
		}
		throw new FieldAccessException(object.getClass(), name, ex);
	}

	public static Object basicInsert(Object object, String name, Object value) throws FieldException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		try {
			Method method = findInserter(getClass(object), name, value);
			return method.invoke(object, value);
		} catch (Exception e) {
			//
		}
		Object tempValue = get(object, name);
		if (tempValue instanceof Collection) {
			if (((Collection) tempValue).add(value)) {
				return value;
			}
			return null;
		}
		throw new FieldAccessException(object.getClass(), name, "can't insert in " + name);
	}

	/**
	 * Reflective method invocation with indexed arguments.
	 * 
	 * This invocation looks up a method with the given name and the exact
	 * parameters classes as defined by the classes of values.
	 * 
	 * @param object
	 * @param name
	 * @param values
	 * @return The result of the invocation
	 * @throws MethodException
	 */
	public static Object basicInvoke(Object object, String name, Object... values) throws MethodException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		if (object instanceof IBasicInvocationSupport) {
			return ((IBasicInvocationSupport) object).basicInvoke(name, values);
		}
		Method method = findMethod(getClass(object), name, false, getClasses(values));
		try {
			return method.invoke(object, values);
		} catch (IllegalAccessException e) {
			throw new MethodAccessException(object.getClass(), name, e);
		} catch (IllegalArgumentException e) {
			throw new MethodInternalException(object.getClass(), name, e);
		} catch (InvocationTargetException e) {
			throw new MethodExecutionException(object.getClass(), name, e.getCause());
		}
	}

	/**
	 * Reflective method invocation with named arguments.
	 * 
	 * This invocation looks up a method annotated with {@link InvocableMethod}
	 * with the given name and tries to map via {@link InvocableArgument}
	 * annotations the correct content from args.
	 * 
	 * @param object
	 * @param name
	 * @param args
	 * @return The result of the invocation
	 * @throws MethodException
	 */
	public static Object basicInvokeArgs(Object object, String name, IArgs args) throws MethodException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		Method method = findInvocableMethod(getClass(object), name);
		if (method == null) {
			throw new MethodNotFoundException(getClass(object), name);
		}
		InvocableArgument[] invocableArguments = findInvocableMethodInvocableArgumentArray(method);
		Object[] argValues = new Object[invocableArguments.length];
		for (int i = 0; i < argValues.length; i++) {
			InvocableArgument invocableArgument = invocableArguments[i];
			if (invocableArgument != null) {
				String argName = invocableArgument.name();
				Object argValue;
				if (".".equals(argName)) {
					argValue = args;
				} else {
					argValue = ArgTools.getObject(args, argName, null);
				}
				IConverter converter = DEFAULT_CONVERTER;
				if (!invocableArgument.converter().isAssignableFrom(DEFAULT_CONVERTER.getClass())) {
					try {
						converter = ObjectTools.createObject(invocableArgument.converter(), IConverter.class);
					} catch (ObjectCreationException e) {
						throw new MethodInternalException(object.getClass(), name, "converter creation failed");
					}
				}
				try {
					argValues[i] = converter.convert(argValue);
				} catch (ConversionException e) {
					throw new MethodInternalException(object.getClass(), name, "conversion failed");
				}
			}
		}
		try {
			return method.invoke(object, argValues);
		} catch (IllegalAccessException e) {
			throw new MethodAccessException(object.getClass(), name, e);
		} catch (IllegalArgumentException e) {
			throw new MethodInternalException(object.getClass(), name, e);
		} catch (InvocationTargetException e) {
			throw new MethodExecutionException(object.getClass(), name, e.getCause());
		}
	}

	public static Object basicRemove(Object object, String name, Object value) throws FieldException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		try {
			Method method = findRemover(getClass(object), name, value);
			return method.invoke(object, value);
		} catch (Exception e) {
			//
		}
		Object tempValue = get(object, name);
		if (tempValue instanceof Collection) {
			if (((Collection) tempValue).remove(value)) {
				return value;
			}
			return null;
		}
		throw new FieldAccessException(object.getClass(), name, "can't remove from " + name);
	}

	public static Object basicSet(Object object, String name, Object value) throws FieldException {
		if (object == null) {
			throw new NullPointerException("object can't be null");
		}
		if (object instanceof IBasicAccessSupport) {
			return ((IBasicAccessSupport) object).basicSetValue(name, value);
		}
		if (object instanceof Map) {
			return ((Map) object).put(name, value);
		}
		if (object instanceof IArgs) {
			return ((IArgs) object).put(name, value);
		}
		try {
			Method method = findSetter(getClass(object), name, value);
			return method.invoke(object, value);
		} catch (InvocationTargetException e) {
			throw new FieldAccessException(object.getClass(), name, e.getCause());
		} catch (Exception e) {
			//
		}
		try {
			Field field = object.getClass().getField(name);
			Object oldValue = field.get(object);
			field.set(object, value);
			return oldValue;
		} catch (Exception e) {
			throw new FieldAccessException(object.getClass(), name, e);
		}
	}

	public static Object convert(Object value, String typeName, ClassLoader classLoader)
			throws ObjectCreationException {
		if (typeName == null) {
			return value;
		}
		Class type = ClassTools.createClass(typeName, Object.class, classLoader);
		try {
			return ConverterRegistry.get().convert(value, type);
		} catch (ConversionException e) {
			throw new ObjectCreationException(e);
		}
	}

	/**
	 * Create a VM unique id that is suitable for identifying object, for
	 * example in a log.
	 * 
	 * @param object
	 * @return
	 */
	public static String createId(Object object) {
		return Integer.toHexString(System.identityHashCode(object));
	}

	/**
	 * Create a VM unique label that is suitable for identifying object, for
	 * example in a log.
	 * 
	 * @param object
	 * @return
	 */
	public static String createLabel(Object object) {
		return createLabel(object, createId(object));
	}

	/**
	 * Create a VM unique label that is suitable for identifying object, for
	 * example in a log.
	 * 
	 * @param object
	 * @return
	 */
	public static String createLabel(Object object, Object id) {
		return ClassTools.getUnqualifiedName(object.getClass()) + "@" + id;
	}

	/**
	 * Create a new instance of Class "class"
	 * 
	 * @param clazz
	 * @param expectedClass
	 * @return The new instance
	 * @throws ObjectCreationException
	 */
	public static <T> T createObject(Class clazz, Class<T> expectedClass) throws ObjectCreationException {
		return createObject(clazz, expectedClass, EMPTY_PARAMETERTYPES, EMPTY_PARAMETERS);
	}

	/**
	 * Create a new instance of Class "class"
	 * 
	 * @param clazz
	 * @param expectedClass
	 * @param parameterTypes
	 * @param parameters
	 * @return The new instance
	 * @throws ObjectCreationException
	 */
	public static <T> T createObject(Class clazz, Class<T> expectedClass, Class[] parameterTypes, Object[] parameters) // NOSONAR
			throws ObjectCreationException {
		if (clazz == null) {
			throw new ObjectCreationException("class missing"); //$NON-NLS-1$
		}
		try {
			T object;
			if (parameters == EMPTY_PARAMETERS || parameters.length == 0) {
				object = (T) clazz.getDeclaredConstructor().newInstance();
			} else {
				Constructor constructor = clazz.getConstructor(parameterTypes);
				object = (T) constructor.newInstance(parameters);
			}
			if (object instanceof IInitializeable) {
				((IInitializeable) object).initializeAfterCreation();
			}
			return object;
		} catch (Throwable e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		}
	}

	/**
	 * Create a new instance of Class "className" via "classLoader".
	 * 
	 * @param className
	 * @param expectedClass
	 * @param classLoader
	 * @return The new instance
	 * @throws ObjectCreationException
	 */
	public static <T> T createObject(String className, Class<T> expectedClass, ClassLoader classLoader)
			throws ObjectCreationException {
		Class clazz = ClassTools.createClass(className, expectedClass, classLoader);
		return createObject(clazz, expectedClass);
	}

	public static Method findGetter(Class clazz, String name) throws MethodException {
		try {
			String methodName = GET_PREFIX + Character.toUpperCase(name.charAt(0)) + name.substring(1);
			return clazz.getMethod(methodName, (Class[]) null);
		} catch (Exception e) {
			//
		}
		String methodName = IS_PREFIX + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		try {
			return clazz.getMethod(methodName, (Class[]) null);
		} catch (Exception e) {
			throw new MethodNotFoundException(clazz, name);
		}
	}

	public static Method findInserter(Class clazz, String attribute, Object value) throws MethodException {
		String methodName;
		Method result;
		try {
			methodName = "add" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
			result = findMethod(clazz, methodName, false, getClass(value));
		} catch (Exception e) {
			try {
				methodName = "insert" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
				result = findMethod(clazz, methodName, false, getClass(value));
			} catch (Exception nested) {
				methodName = "register" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
				result = findMethod(clazz, methodName, false, getClass(value));
			}
		}
		return result;
	}

	protected static Method findInvocableMethod(Class clazz, String name) {
		Method method;
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			method = methods[i];
			if (!method.getName().equals(name)) {
				continue;
			}
			InvocableMethod annot = method.getAnnotation(InvocableMethod.class);
			if (annot != null) {
				return method;
			}
		}
		if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
			method = findInvocableMethod(clazz.getSuperclass(), name);
			if (method != null) {
				return method;
			}
		}
		Class[] ifaces = clazz.getInterfaces();
		for (Class iface : ifaces) {
			method = findInvocableMethod(iface, name);
			if (method != null) {
				return method;
			}
		}
		return null;
	}

	protected static InvocableArgument[] findInvocableMethodInvocableArgumentArray(Method method) {
		Annotation[][] allAnnots = method.getParameterAnnotations();
		InvocableArgument[] result = new InvocableArgument[allAnnots.length];
		int i = 0;
		for (Annotation[] paramAnnots : allAnnots) {
			for (Annotation paramAnnot : paramAnnots) {
				if (paramAnnot.annotationType() == InvocableArgument.class) {
					result[i] = (InvocableArgument) paramAnnot;
				}
			}
			i++;
		}
		return result;
	}

	public static Method findMethod(Class clazz, String name, boolean wildcard, Class... classes)
			throws MethodNotFoundException {
		if (classes != null) {
			try {
				return clazz.getMethod(name, classes);
			} catch (NoSuchMethodException e) {
				// go on...
			}
		}
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (wildcard) {
				if (!method.getName().startsWith(name)) {
					continue;
				}
			} else {
				if (!method.getName().equals(name)) {
					continue;
				}
			}
			if (classes == null) {
				return method;
			}
			Class[] parameterTypes = method.getParameterTypes();
			if (classes.length != parameterTypes.length) {
				continue;
			}
			if (isAssignable(classes, parameterTypes)) {
				return method;
			}
		}
		throw new MethodNotFoundException(clazz, name);
	}

	public static Method findMethodPrefixed(Class clazz, String methodPrefix, Object... parameters)
			throws MethodException {
		Class[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterClasses[i] = parameters[i].getClass();
		}
		Method method = null;
		method = findMethod(clazz, methodPrefix, true, parameterClasses);
		return method;
	}

	public static Method findRegister(Class clazz, Object value) throws MethodException {
		String methodPrefix = "register";
		return findMethodPrefixed(clazz, methodPrefix, value);
	}

	public static Method findRemover(Class clazz, String attribute, Object value) throws MethodException {
		String methodName = "remove" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
		return findMethod(clazz, methodName, false, getClass(value));
	}

	public static Method findSetter(Class clazz, String attribute, Object value) throws MethodException {
		String methodName = "set" + Character.toUpperCase(attribute.charAt(0)) + attribute.substring(1);
		return findMethod(clazz, methodName, false, getClass(value));
	}

	/**
	 * Get the value for field <code>name</code> in <code>object</code>.
	 * 
	 * @param object
	 * @param name
	 * @return the value for field <code>name</code> in <code>object</code>.
	 * @throws FieldException
	 */
	public static Object get(Object object, String name) throws FieldException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		for (Token token : tokens) {
			current = basicGet(current, token.getValue());
		}
		return current;
	}

	protected static Class getClass(Object value) {
		return value == null ? Any.class : value.getClass();
	}

	protected static Class[] getClasses(Object... parameters) {
		Class[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterClasses[i] = getClass(parameters[i]);
		}
		return parameterClasses;
	}

	public static void initObject(Object object) throws ObjectCreationException {
		if (object == null) {
			return;
		}
		if (object instanceof IInitializeable) {
			((IInitializeable) object).initializeAfterConstruction();
		}
		try {
			ObjectTools.invokeMethodAnnotatedWith(object, PostConstruct.class);
		} catch (MethodExecutionException e) {
			throw new ObjectCreationException(e.getCause());
		} catch (MethodException e) {
			throw new ObjectCreationException(e);
		}
	}

	/**
	 * Insert <code>value</code> in the relation field <code>name</code> in
	 * <code>object</code>. The value that was really inserted is returned (if
	 * supported by the underlying object implementation). To be exact, the
	 * result of the insert method invoked is returned.
	 * 
	 * @param object
	 * @param name
	 * @param value
	 * @return the result of the insert method invoked is returned.
	 * @throws FieldException
	 */
	public static Object insert(Object object, String name, Object value) throws FieldException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		Iterator<Token> itTokens = tokens.iterator();
		Token token = itTokens.next();
		while (itTokens.hasNext()) {
			current = basicGet(current, token.getValue());
			token = itTokens.next();
		}
		return basicInsert(current, name, value);
	}

	/**
	 * Invoke method <code>name</code> in <code>object</code>. The result of the
	 * invocation is returned.
	 * 
	 * @param object
	 * @param name
	 * @param values
	 * @return The result of the invocation is returned.
	 * @throws MethodException
	 */
	public static Object invoke(Object object, String name, Object... values) throws MethodException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		Iterator<Token> itTokens = tokens.iterator();
		Token token = itTokens.next();
		while (itTokens.hasNext()) {
			try {
				current = basicGet(current, token.getValue());
			} catch (FieldException e) {
				throw new MethodExecutionException(object.getClass(), name, e);
			}
			token = itTokens.next();
		}
		return basicInvoke(current, name, values);
	}

	public static Object invokeArgs(Object object, String name, IArgs args) throws MethodException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		Iterator<Token> itTokens = tokens.iterator();
		Token token = itTokens.next();
		while (itTokens.hasNext()) {
			try {
				current = basicGet(current, token.getValue());
			} catch (FieldException e) {
				throw new MethodExecutionException(object.getClass(), name, e);
			}
			token = itTokens.next();
		}
		return basicInvokeArgs(current, token.getValue(), args);
	}

	public static Object invokeMethodAnnotatedWith(Object object, Class annotation) throws MethodException {
		Class<?> clazz = object.getClass();
		Method[] methods = clazz.getMethods();
		Object result = null;
		/*
		 * documentation states that methods returned are not in any particular
		 * order, but current implementation puts own methods first then supers.
		 * Assume that "super" initialization is to be done first
		 */
		for (int index = methods.length - 1; index >= 0; index--) {
			Method method = methods[index];
			if (method.getAnnotation(annotation) != null) {
				try {
					result = method.invoke(object);
				} catch (IllegalAccessException e) {
					throw new MethodAccessException(object.getClass(), method.getName(), e);
				} catch (IllegalArgumentException e) {
					throw new MethodInternalException(object.getClass(), method.getName(), e.getCause());
				} catch (InvocationTargetException e) {
					throw new MethodExecutionException(object.getClass(), method.getName(), e.getCause());
				}
			}
		}
		return result;
	}

	public static boolean isAssignable(Class target, Class source) {
		if (source == Any.class) {
			return true;
		}
		if (target.isAssignableFrom(source)) {
			return true;
		}
		if (target.isPrimitive()) {
			return PRIMITIVE_WRAPPER.get(target).isAssignableFrom(source);
		}
		if (source.isPrimitive()) {
			return target.isAssignableFrom(PRIMITIVE_WRAPPER.get(source));
		}
		return false;
	}

	private static boolean isAssignable(Class[] classes, Class[] parameterTypes) {
		for (int j = 0; j < parameterTypes.length; j++) {
			Class searchType = classes[j];
			Class parameterType = parameterTypes[j];
			if (!isAssignable(parameterType, searchType)) {
				return false;
			}
		}
		return true;
	}

	public static Object register(Object registry, Object value) throws MethodException {
		if (registry instanceof IBasicRegistrySupport) {
			return ((IBasicRegistrySupport) registry).basicRegister(value);
		}
		Method method = findRegister(getClass(registry), value);
		try {
			return method.invoke(registry, value);
		} catch (Exception e) {
			throw new MethodExecutionException(registry.getClass(), "register", e.getCause());
		}
	}

	/**
	 * Remove <code>value</code> in the relation field <code>name</code> in
	 * <code>object</code>. The value that was removed is returned (if supported
	 * by the underlying object implementation). To be exact, the result of the
	 * remove method invoked is returned.
	 * 
	 * @param object
	 * @param name
	 * @param value
	 * @return the result of the remove method invoked is returned.
	 * @throws FieldException
	 */
	public static Object remove(Object object, String name, Object value) throws FieldException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		Iterator<Token> itTokens = tokens.iterator();
		Token token = itTokens.next();
		while (itTokens.hasNext()) {
			current = basicGet(current, token.getValue());
			token = itTokens.next();
		}
		return basicRemove(current, name, value);
	}

	/**
	 * Set field <code>name</code> in <code>object</code> to <code>value</code>.
	 * The old value is returned (if supported by the underlying object
	 * implementation). To be exact, the result of the setter method invoked is
	 * returned.
	 * 
	 * @param object
	 * @param name
	 * @param value
	 * @return the result of the setter method invoked is returned.
	 * @throws FieldException
	 */
	public static Object set(Object object, String name, Object value) throws FieldException {
		List<Token> tokens = StringTools.pathParse(name, '.');
		Object current = object;
		Iterator<Token> itTokens = tokens.iterator();
		Token token = itTokens.next();
		while (itTokens.hasNext()) {
			current = basicGet(current, token.getValue());
			token = itTokens.next();
		}
		return basicSet(current, token.getValue(), value);
	}

	public static void setProperties(Object object, IArgs initProperties)
			throws FieldException, ObjectCreationException, FunctorException {
		Iterator<IBinding> it = initProperties.bindings();
		while (it.hasNext()) {
			IBinding binding = it.next();
			setProperty(object, binding);
		}
	}

	public static void setProperty(Object object, IBinding binding)
			throws FieldException, ObjectCreationException, FunctorException {
		String property = binding.getName(); // $NON-NLS-1$
		Object value = binding.getValue();
		set(object, property, value);
	}

}
