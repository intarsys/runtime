package de.intarsys.tools.monitor;

public class MonitorTools {

	/**
	 * Create a default IMonitor.
	 * 
	 * @param id
	 *            The id of the monitor to create.
	 * 
	 * @return A default IMonitor.
	 */
	protected static IMonitor createDefaultMonitor(String id) {
		return new NullMonitor(id);
	}

	public static synchronized IMonitor getMonitor(String id) {
		IMonitor result = MonitorRegistry.get().lookupMonitor(id);
		if (result == null) {
			result = createDefaultMonitor(id);
			MonitorRegistry.get().registerMonitor(result);
		}
		return result;
	}

	private MonitorTools() {
	}
}
