package de.intarsys.tools.session;

/**
 * An {@link ISessionProvider} that provides a predefined {@link ISession}.
 * 
 */
public class StaticSessionProvider implements ISessionProvider {

	private final ISession session;

	public StaticSessionProvider(ISession session) {
		super();
		this.session = session;
	}

	@Override
	public ISession getSession() {
		return session;
	}

}
