package de.intarsys.tools.monitor;

/**
 * VM singleton accesss to a {@link IMonitorRegistry}.
 */
public class MonitorRegistry {

	private static IMonitorRegistry ACTIVE = new StandardMonitorRegistry();

	public static IMonitorRegistry get() {
		return ACTIVE;
	}

	public static void set(IMonitorRegistry active) {
		ACTIVE = active;
	}
}
