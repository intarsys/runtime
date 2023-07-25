package de.intarsys.tools.reflect;

/**
 * Add meta information for a method for reflective invocation.
 * 
 * This information is used by the
 * {@link ObjectTools#invokeArgs(Object, String, de.intarsys.tools.functor.IArgs)}
 * flavored methods.
 *
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
public @interface InvocableMethod {

}