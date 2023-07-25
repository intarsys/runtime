package de.intarsys.tools.session;

/**
 * An {@link ISessionProvider} that provides a new {@link ISession} every time.
 * 
 */
public class StandardSessionProvider implements ISessionProvider {

	@Override
	public ISession getSession() {
		return new StandardSession();
	}

}
