package de.intarsys.tools.ipc;

import de.intarsys.tools.reflect.ObjectCreationException;

public class StandardIPCStubFactory<T> implements IIPCStubFactory<T> {

	private final Class<T> targetClass;

	public StandardIPCStubFactory(Class<T> targetClass) {
		super();
		this.targetClass = targetClass;
	}

	@Override
	public T createObject(IPCHandle value) throws ObjectCreationException {
		try {
			return getTargetClass().getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new ObjectCreationException(e);
		}
	}

	@Override
	public Class<T> getTargetClass() {
		return targetClass;
	}

}
