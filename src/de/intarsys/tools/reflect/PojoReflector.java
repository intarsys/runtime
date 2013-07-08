/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */

package de.intarsys.tools.reflect;

import de.intarsys.tools.functor.IArgs;

/**
 * A POJO wrapper for the ease of reflective access.
 * 
 */
public class PojoReflector implements IAccessSupport, IInvocationSupport {

	final private Object pojo;

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
		Object[] argsArray = new Object[args.size()];
		for (int i = 0; i < args.size(); i++) {
			argsArray[i] = args.get(i);
		}
		return ObjectTools.basicInvoke(pojo, name, argsArray);
	}

	@Override
	public Object setValue(String name, Object value) throws FieldException {
		return ObjectTools.basicSet(pojo, name, value);
	}
}
