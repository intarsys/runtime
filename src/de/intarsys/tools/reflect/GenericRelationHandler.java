package de.intarsys.tools.reflect;

import java.util.Collection;

/**
 * The relation is implemented using basic "intarsys" style reflection.
 * 
 */
public class GenericRelationHandler extends RelationHandlerAdapter {

	final private String attribute;

	public GenericRelationHandler(String attribute) {
		super();
		this.attribute = attribute;
	}

	@Override
	public Object[] get(Object owner) {
		try {
			Object value = ObjectTools.get(owner, getAttribute());
			if (value instanceof Collection) {
				return ((Collection) value).toArray();
			}
			if (value instanceof Object[]) {
				return (Object[]) value;
			}
		} catch (Exception e) {
			//
		}
		return new Object[0];
	}

	public String getAttribute() {
		return attribute;
	}

	@Override
	public Object insert(Object owner, Object value) {
		try {
			ObjectTools.insert(owner, getAttribute(), value);
			return value;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object remove(Object owner, Object value) {
		try {
			return ObjectTools.remove(owner, getAttribute(), value);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object update(Object owner, Object value, Object newValue) {
		try {
			ObjectTools.remove(owner, getAttribute(), value);
			return ObjectTools.insert(owner, getAttribute(), newValue);
		} catch (Exception e) {
			return null;
		}
	}

}
