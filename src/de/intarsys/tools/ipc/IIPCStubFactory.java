package de.intarsys.tools.ipc;

import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * This object creates a business level object from an {@link IPCHandle} for
 * better use in local client code.
 * 
 * @param <T>
 */
public interface IIPCStubFactory<T> {

	public T createObject(IPCHandle value) throws ObjectCreationException;

	public Class<T> getTargetClass();

}
