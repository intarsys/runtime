package de.intarsys.tools.reflect;

/**
 * Simple adapter for {@link IRelationHandler}
 * 
 */
public class RelationHandlerAdapter implements IRelationHandler {

	public Object[] get(Object owner) {
		return new Object[0];
	}

	public Object insert(Object owner, Object value) {
		return value;
	}

	public Object remove(Object owner, Object value) {
		return value;
	}

	public Object update(Object owner, Object value, Object newValue) {
		return newValue;
	}
}
