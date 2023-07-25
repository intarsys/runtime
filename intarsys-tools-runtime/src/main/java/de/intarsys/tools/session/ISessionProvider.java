package de.intarsys.tools.session;

/**
 * An object that can provide you with an {@link ISession} context.
 * 
 */
public interface ISessionProvider {

	/**
	 * An {@link ISession} valid in the context of the caller (most probably
	 * {@link Thread} specific).
	 * 
	 * @return
	 */
	public ISession getSession();

}
