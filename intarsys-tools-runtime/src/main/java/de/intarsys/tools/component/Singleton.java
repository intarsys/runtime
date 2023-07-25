package de.intarsys.tools.component;

/**
 * An object that is intended to be "single" with regard to some context
 * (application, thread, ...)
 *
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface Singleton {

}