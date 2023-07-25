package de.intarsys.tools.session;

import java.util.stream.Stream;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * A registry for {@link ISession} instances.
 * 
 */
@ServiceImplementation(StandardSessionRegistry.class)
public interface ISessionRegistry {

	/**
	 * Enumerate all available {@link ISession} instances.
	 * 
	 * @return
	 */
	Stream<ISession> getSessions();

	/**
	 * Lookup the {@link ISession} with the matching id.
	 * 
	 * This method returns never null. If no {@link ISession} is found or it is expired, a {@link SessionExpired} will
	 * be thrown.
	 * 
	 * @param id
	 * @return
	 */
	ISession lookup(String id) throws SessionExpired;

	/**
	 * Register a new {@link ISession}.
	 * 
	 * @param session
	 */
	void register(ISession session);

	/**
	 * Unregister an {@link ISession}.
	 * 
	 * @param session
	 */
	void unregister(ISession session);

}
