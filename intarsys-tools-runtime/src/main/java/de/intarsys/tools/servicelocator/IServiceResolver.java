package de.intarsys.tools.servicelocator;

import java.util.function.Function;

/**
 * An implementation that can resolve a concrete service implementation for the
 * requested Class&lt;T&gt;.
 * 
 * The resolver must return <code>null</code> if is not responsible for
 * resolving, indicating that another {@link IServiceResolver} should take care.
 * 
 * If the resolver fails to resolve, a {@link ServiceLocatorException} should be
 * thrown.
 * 
 * @param <T>
 */
public interface IServiceResolver<T> extends Function<Class<T>, T> {

}
