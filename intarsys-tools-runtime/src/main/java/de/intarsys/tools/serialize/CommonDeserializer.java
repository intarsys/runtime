package de.intarsys.tools.serialize;

/**
 * A common superclass for implementing an {@link IDeserializer}.
 */
public abstract class CommonDeserializer implements IDeserializer {

	private final SerializationContext context;

	protected CommonDeserializer(SerializationContext context) {
		super();
		this.context = context;
	}

	public SerializationContext getContext() {
		return context;
	}

}
