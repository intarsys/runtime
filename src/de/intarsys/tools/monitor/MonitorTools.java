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
	static protected IMonitor createDefaultMonitor(String id) {
		return new NullMonitor(id);
	}

	static public synchronized IMonitor getMonitor(String id) {
		IMonitor result = MonitorRegistry.get().lookupMonitor(id);
		if (result == null) {
			result = createDefaultMonitor(id);
			MonitorRegistry.get().registerMonitor(result);
		}
		return result;
	}
}
