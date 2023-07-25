package de.intarsys.tools.component;

/**
 * An implementation that is required to provide a single, non-configurable object instance.
 * 
 * The implementation is not intended to change between different execution contexts because of
 * contextual configuration.
 * 
 * Initialization may be either lazy or eager.
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface SingletonClass {

}