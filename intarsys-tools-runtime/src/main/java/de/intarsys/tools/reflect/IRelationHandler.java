package de.intarsys.tools.reflect;

/**
 * An object that can manage a relationship from an owner to a collection of
 * associated components. This is quite useful for generic implementation of
 * "list viewer" ui interface components.
 * 
 */
public interface IRelationHandler<C, E> {

	/**
	 * The objects associated with owner.
	 * 
	 * @param owner
	 * @return The objects associated with owner.
	 */
	public E[] get(C owner);

	/**
	 * The owner associated with the element.
	 * 
	 * This may return null for associations that do not support inverse
	 * navigation.
	 * 
	 * @param element
	 * @return The owner associated with the element.
	 */
	public C getOwner(E element);

	/**
	 * Add a new object to the association in owner.
	 * 
	 * @param owner
	 * @param value
	 * @return The object inserted
	 */
	public E insert(C owner, E value);

	/**
	 * Remove an object from the association in owner.
	 * 
	 * @param owner
	 * @param value
	 * @return The object removed
	 */
	public E remove(C owner, E value);

	/**
	 * The number of objects associated with owner.
	 * 
	 * @param owner
	 * @return The number of objects associated with owner.
	 */
	public int size(C owner);

	/**
	 * Update value in the association for owner to newValue
	 * 
	 * @param owner
	 * @param value
	 * @param newValue
	 * @return The newly associated object
	 */
	public E update(C owner, E value, E newValue);
}
