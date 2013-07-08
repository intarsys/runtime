package de.intarsys.tools.serialize;

/**
 * VM singleton access to {@link ISerializationOutlet}.
 * 
 */
public class SerializationOutlet {

	private static ISerializationOutlet ACTIVE = new StandardSerializationOutlet();

	public static ISerializationOutlet get() {
		return ACTIVE;
	}

	public static void set(ISerializationOutlet active) {
		ACTIVE = active;
	}
}
