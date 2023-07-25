package de.intarsys.tools.lang;

/**
 * An object that supports creating a deep copy of itself.
 *
 * @param <R>
 */
public interface ICopyDeep<R> {

	public R copyDeep();

}
