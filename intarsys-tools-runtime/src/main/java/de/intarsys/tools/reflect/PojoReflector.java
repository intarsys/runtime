/*
 * intarsys GmbH
 * all rights reserved
 *
 */

package de.intarsys.tools.reflect;

import de.intarsys.tools.functor.IArgs;

/**
 * A POJO wrapper publishing "high level" reflection api's.
 * 
 */
public class PojoReflector implements IAccessSupport, IInvocationSupport {

	private final Object pojo;

	public PojoReflector(Object target) {
		pojo = target;
	}

	public Object getPojo() {
		return pojo;
	}

	@Override
	public Object getValue(String name) throws FieldException {
		return ObjectTools.basicGet(pojo, name);
	}

	@Override
	public Object invoke(String name, IArgs args) throws MethodException {
		return ObjectTools.basicInvoke(pojo, name, args);
	}

	@Override
	public Object setValue(String name, Object value) throws FieldException {
		return ObjectTools.basicSet(pojo, name, value);
	}
}
