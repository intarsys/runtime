package de.intarsys.tools.servicelocator;

/**
 * Describe the preferred mechanics for resolving an implementation for the annotated type.
 * 
 */
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
public @interface ServiceImplementation {

	/**
	 * A {@link Class} that is used as the factory for service resolution if all other attempts fail.
	 */
	Class defaultImplementation() default void.class;

	/**
	 * An {@link IServiceResolver} instance that is tried after all other attempts failed.
	 * 
	 */
	Class<? extends IServiceResolver> defaultResolver() default ServiceResolverNull.class;

	/**
	 * A collection of {@link IServiceResolver} instances that are tried *first* when resolving.
	 * 
	 * If resolution fails, the default {@link IServiceResolver} instances are used.
	 */
	Class<? extends IServiceResolver>[] resolver() default {};

	/**
	 * Synonym for {@link #defaultImplementation()}
	 */
	Class value() default void.class;

}