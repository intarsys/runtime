package de.intarsys.tools.session;

import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.component.IReferenceCounter;

/**
 * Provides a context for an ongoing "activity". This is both useful for the
 * loose coupling of tasks collaborating on a common set of objects and for
 * demarcation of the lifespan of objects attached to the context.
 * 
 * The scope of an {@link IActivityContext} can be one of the following:
 * <ul>
 * <li>Thread &ndash; This behaves like a {@link ThreadLocal} container.
 * Information is accessible from every client running on the "current
 * thread".</li>
 * <li>Session &ndash; Provide contextual information for a "session", where
 * context is attached and detached to a Thread multiple times and the threads
 * do not have to be the same for subsequent attaches.</li>
 * </ul>
 * The {@link IActivityContext} defines the lifespan of this common information,
 * starting with the first acquire and ending with the last release. Operations
 * (like setAttribute) outside of the lifespan MUST be discarded to avoid
 * resource leaks.
 */
public interface IActivityContext extends IAttributeSupport, IReferenceCounter {

}
