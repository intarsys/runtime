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
package de.intarsys.tools.message;

import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Arrays;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.reflect.ClassLoaderTools;
import de.intarsys.tools.reflect.ClassTools;
import de.intarsys.tools.string.StringTools;

/**
 * A tool class for some message related tasks.
 * 
 */
public final class MessageTools {

	public static final String MSG = "messages"; //$NON-NLS-1$

	/**
	 * Utility method to combine two {@link IMessageBundleFactory}.
	 * 
	 * @param factory
	 * @param newFactory
	 * @return An {@link IMessageBundleFactory} that contains both input factories
	 */
	public static IMessageBundleFactory combineFactory(IMessageBundleFactory factory,
			IMessageBundleFactory newFactory) {
		if (factory == null) {
			return newFactory;
		}
		CompositeMessageBundleFactory compositeFactory;
		if (factory instanceof CompositeMessageBundleFactory) {
			compositeFactory = (CompositeMessageBundleFactory) factory;
		} else {
			compositeFactory = new CompositeMessageBundleFactory();
			compositeFactory.addFactory(factory);
		}
		compositeFactory.addFactory(newFactory);
		return compositeFactory;
	}

	protected static ClassLoader createClassLoader(String classpath) {
		if (StringTools.isEmpty(classpath)) {
			return Thread.currentThread().getContextClassLoader();
		}
		URL[] urls = ClassLoaderTools.parseURLs(classpath);
		if (urls == null || urls.length == 0) {
			return Thread.currentThread().getContextClassLoader();
		}
		return new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Create an {@link IMessage} with a new code from an existing message.
	 * 
	 * @param code
	 * @param message
	 * @param args
	 * @return
	 */
	public static IMessage createMessage(String code, IMessage message, Object... args) {
		return new AliasMessage(code, message, args);
	}

	/**
	 * Create an {@link IMessage} from literal code and value.
	 * 
	 * No bundle is assigned. No NLS is done. This is a fast &amp; furious helper
	 * method, use with care.
	 * 
	 * @param code
	 * @param value
	 * @param args
	 * @return
	 */
	public static IMessage createMessage(String code, String value, Object... args) {
		return new LiteralMessage(code, value, args);
	}

	public static String format(String pattern, Object... objects) {
		try {
			return MessageFormat.format(pattern, objects);
		} catch (Exception e) {
			return "[" + pattern + "]";
		}
	}

	/**
	 * The default bundle name for message resources of a class..
	 * 
	 * The name is derived as the package name of the class + ".messages" suffix.
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getBundleName(Class clazz) {
		return getBundleName(clazz, MSG);
	}

	/**
	 * The default bundle name for resources of a class.
	 * 
	 * The name is derived as the package name of the class + suffix.
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getBundleName(Class clazz, String suffix) {
		return ClassTools.getPackageName(clazz) + "." + suffix; //$NON-NLS-1$
	}

	/**
	 * The bundle name for a classname.
	 * 
	 * The name is derived as the package name of the class, so all classes from
	 * a package share the same bundle by default.
	 * 
	 * @param className
	 * @return
	 */
	public static String getBundleName(String className) {
		return ClassTools.getPackageName(className) + "." + MSG; //$NON-NLS-1$
	}

	/**
	 * Get a message for an object.
	 * 
	 * The bundle is derived from the objects class (or the object itself, if it
	 * is a class).
	 * 
	 * @param object
	 * @param code
	 * @param args
	 * @return
	 */
	public static IMessage getMessage(Object object, String code, Object... args) {
		Class clazz;
		if (object instanceof Class) {
			clazz = (Class<?>) object;
		} else {
			clazz = object.getClass();
		}
		return getMessageBundle(clazz).getMessage(ClassTools.getUnqualifiedName(clazz) + "." + code, args);
	}

	/**
	 * The {@link IMessageBundle} for a class.
	 * 
	 * The name is derived as the package name of the class, so all classes from
	 * a package share the same bundle by default.
	 * 
	 * @param clazz
	 * @return
	 */
	public static IMessageBundle getMessageBundle(Class clazz) {
		return MessageBundleFactory.get().getMessageBundle(getBundleName(clazz), clazz.getClassLoader());
	}

	/**
	 * The {@link IMessageBundle} for a name and a class loader.
	 * 
	 * No additional computation is done on the bundle name, so be sure to
	 * address the correct properties file yourself!
	 * 
	 * @param name
	 * @param classloader
	 * @return
	 */
	public static IMessageBundle getMessageBundle(String name, ClassLoader classloader) {
		return MessageBundleFactory.get().getMessageBundle(name, classloader);
	}

	/**
	 * The {@link IMessageBundle} for a name and a classpath.
	 * 
	 * No additional computation is done on the bundle name, so be sure to
	 * address the correct properties file yourself!
	 * 
	 * @param name
	 * @param classpath
	 * @return
	 */
	public static IMessageBundle getMessageBundle(String name, String classpath) {
		ClassLoader classLoader = createClassLoader(classpath);
		return getMessageBundle(name, classLoader);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to an {@link Object} recursively.
	 * 
	 * @param message
	 * @return
	 */
	public static Object toObjectExpanded(IStringEvaluator evaluator, Object message) {
		return toObjectExpandedRecurse(evaluator, message, 4);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to an {@link Object} recursively, restricting recursion depth to
	 * {@code maxDepth}.
	 * 
	 * @param evaluator
	 * @param message
	 * @param maxDepth
	 * @return
	 */
	public static Object toObjectExpandedRecurse(IStringEvaluator evaluator, Object message, int maxDepth) {
		if (message == null) {
			return null;
		}
		Object tmpMessage = null;
		if (message instanceof IMessage) {
			tmpMessage = ((IMessage) message).getString();
		} else {
			tmpMessage = message;
		}
		Object result = StringEvaluatorTools.evaluate(evaluator, tmpMessage);
		if (result instanceof IMessage) {
			if (maxDepth > 0) {
				return toSafeStringExpandedRecurse(evaluator, result, maxDepth - 1);
			}
		}
		return result;
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to an {@link Object}
	 * recursively, restricting recursion depth to {@code maxDepth}. Always return a
	 * {@link String} even if message is null.
	 * 
	 * 
	 * @param message
	 * @return
	 */
	public static String toSafeString(Object message) {
		if (message instanceof IMessage) {
			return ((IMessage) message).getString();
		} else {
			return StringTools.safeString(message);
		}
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to a {@link String}
	 * recursively. Always return a {@link String} even if message is null.
	 * 
	 * @param message
	 * @return
	 */
	public static String toSafeStringExpanded(IStringEvaluator evaluator, Object message) {
		return toSafeStringExpandedRecurse(evaluator, message, 4);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to a {@link String}
	 * recursively, restricting recursion depth to {@code maxDepth}. Always return a
	 * {@link String} even if message is null.
	 * 
	 * @param evaluator
	 * @param message
	 * @param maxDepth
	 * @return
	 */
	public static String toSafeStringExpandedRecurse(IStringEvaluator evaluator, Object message, int maxDepth) {
		Object result = toObjectExpandedRecurse(evaluator, message, maxDepth);
		if (result instanceof IMessage) {
			if (maxDepth > 0) {
				return toSafeStringExpandedRecurse(evaluator, result, maxDepth - 1);
			}
		}
		return StringTools.safeString(result);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to an {@link Object}
	 * recursively, restricting recursion depth to {@code maxDepth}.
	 * 
	 * @param message
	 * @return
	 */
	public static String toString(Object message) {
		if (message == null) {
			return null;
		}
		return toSafeString(message);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to a {@link String}
	 * recursively.
	 * 
	 * @param message
	 * @return
	 */
	public static String toStringExpanded(IStringEvaluator evaluator, Object message) {
		if (message == null) {
			return null;
		}
		return toSafeStringExpanded(evaluator, message);
	}

	/**
	 * Resolve and expand an optional {@link IMessage} to a {@link String}
	 * recursively, restricting recursion depth to {@code maxDepth}.
	 * 
	 * @param evaluator
	 * @param message
	 * @param maxDepth
	 * @return
	 */
	public static String toStringExpandedRecurse(IStringEvaluator evaluator, Object message, int maxDepth) {
		if (message == null) {
			return null;
		}
		return toSafeStringExpandedRecurse(evaluator, message, maxDepth);
	}

	/**
	 * Convert an array of messages to an array of strings.
	 * 
	 * @param messages
	 * @return
	 */
	public static String[] toStrings(IMessage[] messages) {
		return Arrays.stream(messages).map(message -> message.getString()).toArray(String[]::new);
	}

	private MessageTools() {
	}
}
