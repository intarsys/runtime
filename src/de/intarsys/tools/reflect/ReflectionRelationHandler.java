package de.intarsys.tools.reflect;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * The relation is implemented using plain JDK reflection.
 * 
 */
public class ReflectionRelationHandler extends RelationHandlerAdapter {

	final private Method getter;

	final private Method inserter;

	final private Method remover;

	public ReflectionRelationHandler(Method getter, Method inserter,
			Method remover) {
		super();
		this.getter = getter;
		this.inserter = inserter;
		this.remover = remover;
	}

	@Override
	public Object[] get(Object owner) {
		try {
			Object value = getter.invoke(owner, (Object[]) null);
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

	@Override
	public Object insert(Object owner, Object value) {
		try {
			return inserter.invoke(owner, new Object[] { value });
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object remove(Object owner, Object value) {
		try {
			return remover.invoke(owner, new Object[] { value });
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object update(Object owner, Object value, Object newValue) {
		try {
			remover.invoke(owner, new Object[] { value });
			return inserter.invoke(owner, new Object[] { newValue });
		} catch (Exception e) {
			return null;
		}
	}

}
