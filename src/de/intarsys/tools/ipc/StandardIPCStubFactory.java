package de.intarsys.tools.ipc;

import de.intarsys.tools.reflect.ObjectCreationException;

public class StandardIPCStubFactory<T> implements IIPCStubFactory<T> {

	final private Class<T> targetClass;

	public StandardIPCStubFactory(Class<T> targetClass) {
		super();
		this.targetClass = targetClass;
	}

	@Override
	public T createObject(IPCHandle value) throws ObjectCreationException {
		try {
			return getTargetClass().newInstance();
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
	}

	public Class<T> getTargetClass() {
		return targetClass;
	}

}
