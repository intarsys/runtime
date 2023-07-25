package de.intarsys.tools.session;

/**
 * An object that is aware of a possible {@link ISession} context.
 * 
 * The object is informed upon registration or unregistration from the {@link ISession}.
 * 
 */
public interface ISessionBindingAware {

	void valueBound(ISession session);

	void valueUnbound(ISession session);
}
