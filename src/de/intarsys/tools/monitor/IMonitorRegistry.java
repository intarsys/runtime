package de.intarsys.tools.monitor;

import java.util.List;

/**
 * A registry for {@link IMonitor} instances.
 */
public interface IMonitorRegistry {

	/**
	 * A snapshot of available monitors at the moment of the request.
	 * 
	 * @return A snapshot of available monitors at the moment of the request.
	 */
	public List getMonitors();

	/**
	 * The monitor named <code>id</code>. If none is available a new one
	 * according to configuration or default should be created.
	 * 
	 * @param id
	 *            The id of the monitor to return.
	 * 
	 * @return The monitor named <code>id</code>.
	 */
	public IMonitor lookupMonitor(String id);

	/**
	 * Register a new monitor with the factory to allow access by name.
	 * 
	 * @param monitor
	 *            THe monitor object to register.
	 */
	public void registerMonitor(IMonitor monitor);

}
