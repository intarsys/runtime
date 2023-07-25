package de.intarsys.tools.serialize;

import java.io.IOException;

/**
 * An {@link IDeserializer} can deserialize an object from its current state.
 * <p>
 * Multiple calls to "deserialize" may be possible.
 */
public interface IDeserializer {

	/**
	 * Deserialize and return the next object from the current state.
	 * 
	 * @return The next object deserialized from the current state.
	 * 
	 * @throws IOException
	 */
	public Object deserialize() throws IOException;

}
