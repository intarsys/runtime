package de.intarsys.tools.date;

/**
 * A VM singleton for {@link IDateEnvironment}.
 * 
 */
public class DateEnvironment {

	private static IDateEnvironment ACTIVE = new StandardDateEnvironment();

	public static IDateEnvironment get() {
		return ACTIVE;
	}

	public static void set(IDateEnvironment active) {
		ACTIVE = active;
	}
}
