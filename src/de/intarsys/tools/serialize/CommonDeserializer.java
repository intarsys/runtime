package de.intarsys.tools.serialize;

/**
 * A common superclass for implementing an {@link IDeserializer}.
 */
abstract public class CommonDeserializer implements IDeserializer {

	final private SerializationContext context;

	public CommonDeserializer(SerializationContext context) {
		super();
		this.context = context;
	}

	public SerializationContext getContext() {
		return context;
	}

}
