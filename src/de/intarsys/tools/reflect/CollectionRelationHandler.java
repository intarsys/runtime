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
}