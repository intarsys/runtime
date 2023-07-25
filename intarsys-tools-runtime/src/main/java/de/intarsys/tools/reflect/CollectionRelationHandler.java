/**
 * 
 */
package de.intarsys.tools.reflect;

import java.util.Collection;

/**
 * The relation is implemented in a plain java collection.
 * 
 */
public class CollectionRelationHandler extends RelationHandlerAdapter {

	@Override
	public Object[] get(Object owner) {
		if (owner == null) {
			return new Object[0];
		}
		return ((Collection) owner).toArray();
	}

	@Override
	public Object insert(Object owner, Object value) {
		if (owner == null) {
			return null;
		}
		if (((Collection) owner).add(value)) {
			return value;
		}
		return null;
	}

	@Override
	public Object remove(Object owner, Object value) {
		if (owner == null) {
			return null;
		}
		if (((Collection) owner).remove(value)) {
			return value;
		}
		return null;
	}

	@Override
	public int size(Object owner) {
		if (owner == null) {
			return 0;
		}
		return ((Collection) owner).size();
	}

	@Override
	public Object update(Object owner, Object value, Object newValue) {
		((Collection) owner).remove(value);
		((Collection) owner).add(newValue);
		return newValue;
	}
}