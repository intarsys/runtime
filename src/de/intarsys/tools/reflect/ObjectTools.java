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
package de.intarsys.tools.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.component.IInitializeable;
import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;

/**
 * A tool class for convenient object related tasks.
 * <p>
 * This tool contains some simple reflection implementations.
 * 
 */
public class ObjectTools {

	private static final Object[] EMPTY_PARAMETERS = new Object[0];

	private static final Class[] EMPTY_PARAMETERTYPES = new Class[0];

	public static final String GET_PREFIX = "get"; //$NON-NLS-1$

	public static final String IS_PREFIX = "is"; //$NON-NLS-1$

	final private static Map<String, Class> PRIMITIVE_CLASSES = new HashMap<String, Class>();

	final private static Map<Class, Class> PRIMITIVE_WRAPPER = new HashMap<Class, Class>();

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

	public static Object basicGet(Object object, String name)
			throws FieldException {
		if (object instanceof IBasicAccessSupport) {
			return ((IBasicAccessSupport) object).basicGetValue(name);
		}
		try {
			Method method = findGetter(object, name);
			return method.invoke(object, (Object[]) null);
		} catch (Exception e) {
			//
		}
		try {
			Field field = object.getClass().getField(name);
			return field.get(object);
		} catch (Exception e) {
			//
		}
		try {
			Method method = object.getClass().getMethod(name, (Class[]) null);
			return method.invoke(object, (Object[]) null);
		} catch (Exception e) {
			throw new FieldAccessException(object.getClass(), name, e);
		}
	}

	public static Object basicInsert(Object object, String name, Object value)
			throws FieldException {
		try {
			Method method = findInserter(object, name, value);
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
		throw new FieldAccessException(object.getClass(), name,
				"can't insert in " + name);
	}

	public static Object basicInvoke(Object object, String name,
			Object... values) throws MethodException {
		if (object instanceof IBasicInvocationSupport) {
			return ((IBasicInvocationSupport) object).basicInvoke(name, values);
		}
		Method method = findMethod(object, name, values);
		try {
			return method.invoke(object, values);
		} catch (Exception e) {
			throw new MethodInvocationException(object.getClass(), name, e);
		}
	}

	public static Object basicRemove(Object object, String name, Object value)
			throws FieldException {
		try {
			Method method = findRemover(object, name, value);
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
		throw new FieldAccessException(object.getClass(), name,
				"can't remove from " + name);
	}

	public static Object basicSet(Object object, String name, Object value)
			throws FieldException {
		if (object instanceof IBasicAccessSupport) {
			return ((IBasicAccessSupport) object).basicSetValue(name, value);
		}
		try {
			Method method = findSetter(object, name, value);
			return method.invoke(object, value);
		} catch (InvocationTargetException e) {
			throw new FieldAccessException(object.getClass(), name,
					e.getCause());
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

	protected static boolean checkCandidate(Method method, String methodName,
			Class[] pClasses) {
		if (!method.getName().equals(methodName)) {
			return false;
		}
		Class[] mClasses = method.getParameterTypes();
		return checkCandidateClasses(mClasses, pClasses);
	}

	protected static boolean checkCandidateClasses(Class[] mClasses,
			Class[] pClasses) {
		if (mClasses.length != pClasses.length) {
			return false;
		}
		for (int i = 0; i < mClasses.length; i++) {
			if (!isAssignable(mClasses[i], pClasses[i])) {
				return false;
			}
		}
		return true;
	}

	public static Object convert(Object value, String typeName,
			ClassLoader classLoader) throws ObjectCreationException {
		if (typeName == null) {
			return value;
		}
		Class type = ClassTools
				.createClass(typeName, Object.class, classLoader);
		try {
			return ConverterRegistry.get().convert(value, type);
		} catch (ConversionException e) {
			throw new ObjectCreationException(e);
		}
	}

	/**
	 * Create a new instance of Class "class"
	 * 
	 * @param clazz
	 * @param expectedClass
	 * @return The new instance
	 * @throws ObjectCreationException
	 */
	public static <T> T createObject(Class clazz, Class<T> expectedClass)
			throws ObjectCreationException {
		return createObject(clazz, expectedClass, EMPTY_PARAMETERTYPES,
				EMPTY_PARAMETERS);
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
	public static <T> T createObject(Class clazz, Class<T> expectedClass,
			Class[] parameterTypes, Object[] parameters)
			throws ObjectCreationException {
		if (clazz == null) {
			throw new ObjectCreationException("class missing"); //$NON-NLS-1$
		}
		try {
			T object;
			if (parameters == EMPTY_PARAMETERS) {
				object = (T) clazz.newInstance();
			} else {
				Constructor constructor = clazz.getConstructor(parameterTypes);
				object = (T) constructor.newInstance(parameters);
			}
			if (object instanceof IInitializeable) {
				((IInitializeable) object).initializeAfterCreation();
			}
			return object;
		} catch (NoClassDefFoundError e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (InstantiationException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (IllegalAccessException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (SecurityException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (InvocationTargetException e) {
			throw new ObjectCreationException("class '" + clazz.getName() //$NON-NLS-1$
					+ "' can't be instantiated", e); //$NON-NLS-1$
		} catch (Exception e) {
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
	public static <T> T createObject(String className, Class<T> expectedClass,
			ClassLoader classLoader) throws ObjectCreationException {
		Class clazz = ClassTools.createClass(className, expectedClass,
				classLoader);
		return createObject(clazz, expectedClass);
	}

	public static Method findGetter(Object object, String name)
			throws MethodException {
		try {
			String methodName = GET_PREFIX
					+ Character.toUpperCase(name.charAt(0)) + name.substring(1);
			return object.getClass().getMethod(methodName, (Class[]) null);
		} catch (Exception e) {
			//
		}
		String methodName = IS_PREFIX + Character.toUpperCase(name.charAt(0))
				+ name.substring(1);
		try {
			return object.getClass().getMethod(methodName, (Class[]) null);
		} catch (Exception e) {
			throw new MethodNotFoundException(object.getClass(), name);
		}
	}

	public static Method findInserter(Object object, String attribute,
			Object value) throws MethodException {
		String methodName;
		Method result;
		try {
			methodName = "add" + Character.toUpperCase(attribute.charAt(0))
					+ attribute.substring(1);
			result = findMethod(object, methodName, new Object[] { value });
		} catch (Exception e) {
			try {
				methodName = "insert"
						+ Character.toUpperCase(attribute.charAt(0))
						+ attribute.substring(1);
				result = findMethod(object, methodName, new Object[] { value });
			} catch (Exception e2) {
				methodName = "register"
						+ Character.toUpperCase(attribute.charAt(0))
						+ attribute.substring(1);
				result = findMethod(object, methodName, new Object[] { value });
			}
		}
		return result;
	}

	protected static Method findMatchingMethod(Class clazz, String name,
			Class[] classes) throws MethodException {
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (checkCandidate(method, name, classes)) {
				// todo quick hack. lookup best match, not first.
				return method;
			}
		}
		try {
			return clazz.getMethod(name, classes);
		} catch (Exception e) {
			throw new MethodNotFoundException(clazz, name);
		}
	}

	public static Method findMethod(Object object, String methodName,
			Object... parameters) throws MethodException {
		Class clazz = object.getClass();
		Class[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterClasses[i] = parameters[i].getClass();
		}
		Method method = null;
		try {
			method = findMethodFast(clazz, methodName, parameterClasses);
		} catch (Exception e) {
			method = findMethodNamed(clazz, methodName, false, parameterClasses);
		}
		return method;
	}

	protected static Method findMethodFast(Class clazz, String name,
			Class... classes) throws MethodException {
		try {
			return clazz.getMethod(name, classes);
		} catch (Exception e) {
			throw new MethodNotFoundException(clazz, name);
		}
	}

	protected static Method findMethodNamed(Class clazz, String name,
			boolean wildcard, Class... classes) throws MethodException {
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
			boolean found = true;
			Class[] parameterTypes = method.getParameterTypes();
			for (int j = 0; j < parameterTypes.length; j++) {
				Class searchType = classes[j];
				Class parameterType = parameterTypes[j];
				if (!isAssignable(parameterType, searchType)) {
					found = false;
					break;
				}
			}
			if (found) {
				return method;
			}
		}
		throw new MethodNotFoundException(clazz, name);
	}

	public static Method findMethodPrefixed(Object object, String methodPrefix,
			Object... parameters) throws MethodException {
		Class clazz = object.getClass();
		Class[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterClasses[i] = parameters[i].getClass();
		}
		Method method = null;
		method = findMethodNamed(clazz, methodPrefix, true, parameterClasses);
		return method;
	}

	public static Method findRegister(Object object, Object value)
			throws MethodException {
		String methodPrefix = "register";
		return findMethodPrefixed(object, methodPrefix, new Object[] { value });
	}

	public static Method findRemover(Object object, String attribute,
			Object value) throws MethodException {
		String methodName = "remove"
				+ Character.toUpperCase(attribute.charAt(0))
				+ attribute.substring(1);
		return findMethod(object, methodName, new Object[] { value });
	}

	public static Method findSetter(Object object, String attribute,
			Object value) throws MethodException {
		String methodName = "set" + Character.toUpperCase(attribute.charAt(0))
				+ attribute.substring(1);
		return findMethod(object, methodName, new Object[] { value });
	}

	/**
	 * Get the value for field <code>name</code> in <code>object</code>.
	 * 
	 * @param object
	 * @param name
	 * @return the value for field <code>name</code> in <code>object</code>.
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	static public Object get(Object object, String name) throws FieldException {
		String tempExpr = name;
		int pos = tempExpr.indexOf('.');
		if (pos == -1) {
			return basicGet(object, name);
		} else {
			String pathPrefix = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result = basicGet(object, pathPrefix);
			return get(result, pathTrail);
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
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	static public Object insert(Object object, String name, Object value)
			throws FieldException {
		// fix array syntax
		String tempExpr = name.replace('[', '.');
		if (tempExpr != name) {
			tempExpr = tempExpr.replace(']', ' ');
		}
		int pos = tempExpr.indexOf('.');
		if (pos == -1) {
			return basicInsert(object, name, value);
		} else {
			String pathPrefix = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result = basicGet(object, pathPrefix);
			return insert(result, pathTrail, value);
		}
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
	static public Object invoke(Object object, String name, Object... values)
			throws MethodException {
		// fix array syntax
		String tempExpr = name.replace('[', '.');
		if (tempExpr != name) {
			tempExpr = tempExpr.replace(']', ' ');
		}
		int pos = tempExpr.indexOf('.');
		if (pos == -1) {
			return basicInvoke(object, name, values);
		} else {
			String pathPrefix = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result;
			try {
				result = basicGet(object, pathPrefix);
			} catch (FieldException e) {
				throw new MethodInvocationException(object.getClass(), name, e);
			}
			return invoke(result, pathTrail, values);
		}
	}

	public static boolean isAssignable(Class target, Class source) {
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

	public static Object register(Object registry, Object value)
			throws MethodException {
		if (registry instanceof IBasicRegistrySupport) {
			return ((IBasicRegistrySupport) registry).basicRegister(value);
		}
		Method method = findRegister(registry, value);
		try {
			return method.invoke(registry, value);
		} catch (Exception e) {
			throw new MethodInvocationException(registry.getClass(),
					"register", e.getCause());
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
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	static public Object remove(Object object, String name, Object value)
			throws FieldException {
		// fix array syntax
		String tempExpr = name.replace('[', '.');
		if (tempExpr != name) {
			tempExpr = tempExpr.replace(']', ' ');
		}
		int pos = tempExpr.indexOf('.');
		if (pos == -1) {
			return basicRemove(object, name, value);
		} else {
			String pathPrefix = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result = basicGet(object, pathPrefix);
			return remove(result, pathTrail, value);
		}
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
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	static public Object set(Object object, String name, Object value)
			throws FieldException {
		// fix array syntax
		String tempExpr = name.replace('[', '.');
		if (tempExpr != name) {
			tempExpr = tempExpr.replace(']', ' ');
		}
		int pos = tempExpr.indexOf('.');
		if (pos == -1) {
			return basicSet(object, name, value);
		} else {
			String pathPrefix = tempExpr.substring(0, pos);
			String pathTrail = tempExpr.substring(pos + 1);
			Object result = basicGet(object, pathPrefix);
			return set(result, pathTrail, value);
		}
	}
}
