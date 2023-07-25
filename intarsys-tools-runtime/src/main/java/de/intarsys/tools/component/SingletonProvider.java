package de.intarsys.tools.component;

/**
 * An implementation that provides you with a single object instance providing a given service.
 * 
 * The service itself is not known upfront, only a discriminating type.This is used for example for
 * looking up a concrete implementation of an interface, an abstract class or a concrete configuration
 * of a generic service implementation.
 * 
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface SingletonProvider {

}