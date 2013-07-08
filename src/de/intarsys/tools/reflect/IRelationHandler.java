package de.intarsys.tools.reflect;

/**
 * An object that can manage a relationship from an owner to a collection of
 * associated components. This is quite useful for generic implementation of
 * "list viewer" ui interface components.
 * 
 */
public interface IRelationHandler {

	/**
	 * The objects associated with owner.
	 * 
	 * @param owner
	 * @return The objects associated with owner.
	 */
	public Object[] get(Object owner);

	/**
	 * Add a new object to the association in owner.
	 * 
	 * @param owner
	 * @param value
	 * @return The object inserted
	 */
	public Object insert(Object owner, Object value);

	/**
	 * Remove an object from the association in owner.
	 * 
	 * @param owner
	 * @param value
	 * @return The object removed
	 */
	public Object remove(Object owner, Object value);

	/**
	 * Update value in the association for owner to newValue
	 * 
	 * @param owner
	 * @param value
	 * @param newValue
	 * @return The newly associated object
	 */
	public Object update(Object owner, Object value, Object newValue);
}
