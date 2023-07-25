package de.intarsys.tools.serialize;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * An {@link ISerializationOutlet} can provide lookup for
 * {@link ISerializationFactory} instances.
 * 
 */
@ServiceImplementation(StandardSerializationOutlet.class)
public interface ISerializationOutlet {

	public ISerializationFactory lookupSerializationFactory(Class clazz, SerializationContext context);

	public void registerSerializationFactory(ISerializationFactory factory);

	public void unregisterSerializationFactory(ISerializationFactory factory);

}
