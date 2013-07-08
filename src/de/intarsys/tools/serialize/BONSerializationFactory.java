package de.intarsys.tools.serialize;

import de.intarsys.tools.mime.IMimeTypeSupport;

/**
 * The {@link ISerializationFactory} for "BON" serializing and deserializing.
 * <p>
 * "BON", basic object notation is a simple, self contained, readable
 * serialization format for primitive types, array and maps. The serialization
 * format is very similiar to "JSON".
 * 
 */
public class BONSerializationFactory implements ISerializationFactory,
		IMimeTypeSupport {

	public static final String MIMETYPE = "application/vnd.intarsys.primitive";

	@Override
	public IDeserializer createDeserializer(SerializationContext context) {
		return new BONDeserializer(
				((StreamSerializationContext) context).getInputStream());
	}

	@Override
	public ISerializer createSerializer(SerializationContext context) {
		return new BONSerializer(
				((StreamSerializationContext) context).getOutputStream());
	}

	@Override
	public String getContentType() {
		return MIMETYPE;
	}

	@Override
	public Class getSerializationType() {
		return Object.class;
	}
}
