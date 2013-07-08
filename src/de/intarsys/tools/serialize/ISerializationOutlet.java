package de.intarsys.tools.serialize;

/**
 * An {@link ISerializationOutlet} can provide lookup for
 * {@link ISerializationFactory} instances.
 * 
 */
public interface ISerializationOutlet {

	public ISerializationFactory lookupSerializationFactory(Class clazz,
			SerializationContext context);

	public void registerSerializationFactory(ISerializationFactory factory);

	public void unregisterSerializationFactory(ISerializationFactory factory);

}
