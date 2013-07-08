/*
 * Copyright (c) 2008, intarsys consulting GmbH
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
package de.intarsys.tools.objectmodel;

import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.FieldNotFoundException;
import de.intarsys.tools.reflect.MethodException;
import de.intarsys.tools.reflect.MethodInvocationException;
import de.intarsys.tools.reflect.MethodNotFoundException;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * Some tool methods for dealing with the object model.
 * 
 */
public class ObjectModelTools {

	static public IClassSelector createSelector(String value)
			throws ObjectCreationException {
		String[] fragments = value.split(":");
		java.lang.Class<?> clazz = ClassTools.createClass(fragments[0],
				Object.class, null);
		if (fragments.length > 1) {
			return new JavaInstanceSelector(clazz, fragments[1]);
		}
		return new JavaClassSelector(clazz);
	}

	static public Object get(Object receiver, Object id, String name)
			throws FieldException {
		if (receiver == null) {
			throw new NullPointerException("can not call '" + name
					+ "' for 'null'");
		}
		IField field = lookupField(receiver.getClass(), id, name);
		return field.getValue(receiver);
	}

	static public Object get(Object receiver, String name)
			throws FieldException {
		if (receiver == null) {
			throw new NullPointerException("can not call '" + name
					+ "' for 'null'");
		}
		IField field = lookupField(receiver.getClass(), name);
		return field.getValue(receiver);
	}

	static public Object invoke(Object receiver, Object id, String name,
			IArgs args) throws MethodException {
		if (receiver == null) {
			throw new NullPointerException("can't call '" + name
					+ "' for 'null'");
		}
		IMethod method = lookupMethod(receiver.getClass(), id, name);
		return method.invoke(receiver, args);
	}

	static public Object invoke(Object receiver, String name, IArgs args)
			throws MethodException {
		if (receiver == null) {
			throw new NullPointerException("can't call '" + name
					+ "' for 'null'");
		}
		IMethod method = lookupMethod(receiver.getClass(), name);
		return method.invoke(receiver, args);
	}

	protected static Object invokeIntercept(Object receiver,
			INotificationListener dispatcher, IMethod method, IArgs args)
			throws MethodInvocationException {
		// intercept invocation
		InvokeIntercept intercept = new InvokeIntercept(receiver, dispatcher,
				method, args);
		dispatcher.handleEvent(intercept);
		if (intercept.isVetoed()) {
			return intercept.getResult();
		}
		return invokeInterceptResume(receiver, dispatcher, method, args);
	}

	static public Object invokeIntercept(Object receiver,
			INotificationListener dispatcher, String name, IArgs args)
			throws MethodException {
		if (receiver == null) {
			throw new NullPointerException("can't call '" + name
					+ "' for 'null'");
		}
		IMethod method = lookupMethod(receiver.getClass(), name);
		return invokeIntercept(receiver, dispatcher, method, args);
	}

	static public Object invokeIntercept(Object receiver, Object id,
			INotificationListener dispatcher, String name, IArgs args)
			throws MethodException {
		if (receiver == null) {
			throw new NullPointerException("can't call '" + name
					+ "' for 'null'");
		}
		IMethod method = lookupMethod(receiver.getClass(), id, name);
		return invokeIntercept(receiver, dispatcher, method, args);
	}

	protected static Object invokeInterceptResume(Object receiver,
			INotificationListener dispatcher, IMethod method, IArgs args)
			throws MethodInvocationException {
		Object result = method.invoke(receiver, args);
		// accept invocation
		InvokeAccept accept = new InvokeAccept(method, receiver, args);
		accept.setResult(result);
		dispatcher.handleEvent(accept);
		//
		return accept.getResult();
	}

	static public IClass lookupClass(IClassSelector selector) {
		IClass extension = ClassRegistry.get().lookupClass(selector);
		if (extension == null) {
			extension = new Class(selector);
			ClassRegistry.get().registerClass(extension);
		}
		return extension;
	}

	static public IClass lookupClass(java.lang.Class<?> clazz) {
		IClassSelector selector = new JavaClassSelector(clazz);
		return lookupClass(selector);
	}

	static public IClass lookupClass(java.lang.Class<?> clazz, Object id) {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		return lookupClass(selector);
	}

	static public IField lookupField(java.lang.Class<?> clazz, Object id,
			String name) throws FieldNotFoundException {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		IField result = null;
		IClass extension = ClassRegistry.get().lookupClass(selector);
		if (extension != null) {
			result = extension.lookupField(name);
			if (result != null) {
				return result;
			}
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			try {
				result = lookupField(superClass, id, name);
				if (result != null) {
					return result;
				}
			} catch (Exception e) {
				// continue searching
			}
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			try {
				result = lookupField(interfaces[i], id, name);
				if (result != null) {
					return result;
				}
			} catch (Exception e) {
				// continue searching
			}
		}
		return lookupField(clazz, name);
	}

	static public IField lookupField(java.lang.Class<?> clazz, String name)
			throws FieldNotFoundException {
		IClassSelector selector = new JavaClassSelector(clazz);
		IField result = null;
		IClass extension = ClassRegistry.get().lookupClass(selector);
		if (extension != null) {
			result = extension.lookupField(name);
			if (result != null) {
				return result;
			}
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			try {
				result = lookupField(superClass, name);
				if (result != null) {
					return result;
				}
			} catch (Exception e) {
				// continue searching
			}
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			try {
				result = lookupField(interfaces[i], name);
				if (result != null) {
					return result;
				}
			} catch (Exception e) {
				// continue searching
			}
		}
		throw new FieldNotFoundException(clazz, name);
	}

	static public IMethod lookupMethod(java.lang.Class<?> clazz, Object id,
			String name) throws MethodNotFoundException {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		IMethod result = null;
		IClass extension = ClassRegistry.get().lookupClass(selector);
		if (extension != null) {
			result = extension.lookupMethod(name);
			if (result != null) {
				return result;
			}
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			try {
				result = lookupMethod(superClass, id, name);
				if (result != null) {
					return result;
				}
			} catch (MethodNotFoundException e) {
				// continue lookup
			}
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			try {
				result = lookupMethod(interfaces[i], id, name);
				if (result != null) {
					return result;
				}
			} catch (MethodNotFoundException e) {
				// continue lookup
			}
		}
		return lookupMethod(clazz, name);
	}

	static public IMethod lookupMethod(java.lang.Class<?> clazz, String name)
			throws MethodNotFoundException {
		IClassSelector selector = new JavaClassSelector(clazz);
		IMethod result = null;
		IClass extension = ClassRegistry.get().lookupClass(selector);
		if (extension != null) {
			result = extension.lookupMethod(name);
			if (result != null) {
				return result;
			}
		}
		java.lang.Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			try {
				result = lookupMethod(superClass, name);
				if (result != null) {
					return result;
				}
			} catch (MethodNotFoundException e) {
				// continue lookup
			}
		}
		java.lang.Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			try {
				result = lookupMethod(interfaces[i], name);
				if (result != null) {
					return result;
				}
			} catch (MethodNotFoundException e) {
				// continue lookup
			}
		}
		throw new MethodNotFoundException(clazz, name);
	}

	public static void registerClass(IClassSelector selector) {
		IClass clazz = ClassRegistry.get().lookupClass(selector);
		if (clazz == null) {
			clazz = new Class(selector);
			ClassRegistry.get().registerClass(clazz);
		}
	}

	static public void registerClass(java.lang.Class<?> clazz) {
		IClassSelector selector = new JavaClassSelector(clazz);
		registerClass(selector);
	}

	static public void registerClass(java.lang.Class<?> clazz, Object id) {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		registerClass(selector);
	}

	public static void registerField(IClassSelector selector, IField field) {
		IClass clazz = ClassRegistry.get().lookupClass(selector);
		if (clazz == null) {
			clazz = new Class(selector);
			ClassRegistry.get().registerClass(clazz);
		}
		((Class) clazz).registerField(field);
	}

	static public void registerField(java.lang.Class<?> clazz, IField field) {
		IClassSelector selector = new JavaClassSelector(clazz);
		registerField(selector, field);
	}

	static public void registerField(java.lang.Class<?> clazz, Object id,
			IField field) {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		registerField(selector, field);
	}

	public static void registerMethod(IClassSelector selector, IMethod method) {
		IClass clazz = ClassRegistry.get().lookupClass(selector);
		if (clazz == null) {
			clazz = new Class(selector);
			ClassRegistry.get().registerClass(clazz);
		}
		((Class) clazz).registerMethod(method);
	}

	static public void registerMethod(java.lang.Class<?> clazz, IMethod method) {
		IClassSelector selector = new JavaClassSelector(clazz);
		registerMethod(selector, method);
	}

	static public void registerMethod(java.lang.Class<?> clazz, Object id,
			IMethod method) {
		IClassSelector selector = new JavaInstanceSelector(clazz, id);
		registerMethod(selector, method);
	}

	static public void set(Object receiver, Object id, String name, Object value)
			throws FieldException {
		if (receiver == null) {
			throw new NullPointerException("can not call '" + name
					+ "' for 'null'");
		}
		IField field = lookupField(receiver.getClass(), id, name);
		field.setValue(receiver, value);
	}

	static public void set(Object receiver, String name, Object value)
			throws FieldException {
		if (receiver == null) {
			throw new NullPointerException("can not call '" + name
					+ "' for 'null'");
		}
		IField field = lookupField(receiver.getClass(), name);
		field.setValue(receiver, value);
	}
}
