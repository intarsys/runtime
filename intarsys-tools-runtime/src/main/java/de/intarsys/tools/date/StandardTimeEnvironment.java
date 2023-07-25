package de.intarsys.tools.date;

import java.time.Instant;

/**
 * The standard {@link ITimeEnvironment} falls back to the @deprecated {@link IDateEnvironment}.
 * 
 */
public class StandardTimeEnvironment implements ITimeEnvironment {

	public StandardTimeEnvironment() {
		super();
	}

	@Override
	public Instant now() {
		return Instant.ofEpochMilli(DateEnvironment.get().now().getTime());
	}

}
