package de.intarsys.tools.reflect;

import de.intarsys.tools.converter.NoopConverter;

/**
 * Add meta information for a method parameter for reflective invocation.
 * 
 * This information is used by the
 * {@link ObjectTools#invokeArgs(Object, String, de.intarsys.tools.functor.IArgs)}
 * flavored methods.
 *
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.PARAMETER })
public @interface InvocableArgument {

	public Class converter() default NoopConverter.class;

	public String name();

}