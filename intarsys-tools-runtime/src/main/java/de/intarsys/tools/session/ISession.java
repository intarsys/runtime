package de.intarsys.tools.session;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IDisposable;
import de.intarsys.tools.component.IExpirationSupport;
import de.intarsys.tools.component.IIdentifiable;

/**
 * Provide a context for an ongoing task. This is both useful for the loose
 * coupling of tasks collaborating on a common set of state and for demarcation
 * of the lifespan of the state represented by the {@link ISession}.
 * 
 * The {@link ISession} defines the lifespan of this common information,
 * starting with the creation and ending with the {@link #dispose()}. Operations
 * (like setAttribute) outside of the session lifespan MUST be discarded to
 * avoid resource leaks.
 * 
 * {@link ISession} disposal is forwarded to every {@link IDisposable} session
 * attribute. Session disposal is either triggered via the API or by a provider
 * internal expiration mechanism.
 * 
 * To get an {@link ISession} normally you simply ask an
 * {@link ISessionProvider}. The {@link ISessionProvider} may be available via
 * reference or a suitable default can be acquired from {@link SessionProvider}
 * singleton.
 * 
 * While you might be tempted to interpret an {@link ISession} as a thread
 * confined resource, it is not! The instance may be associated with multiple
 * threads, depending on the underlying protocol.
 * 
 * The {@link ISession} does not have any information/behavior about how she
 * gets activated or deactivated or how is state preserved / persisted in
 * between activations. This is part of the driving protocol. Examples of
 * activations are - HTTP call events carrying session ids - AWT event queue
 * callbacks from dedicated UI components
 * 
 * 
 */
public interface ISession extends IAttributeSupport, IDisposable, IIdentifiable, IExpirationSupport {

}
