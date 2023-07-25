package de.intarsys.tools.notice;

import de.intarsys.tools.activity.IActivity;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.message.IMessage;

/**
 * The abstraction of an information attached to some target
 * {@link INoticesSupport}.
 * 
 * The {@link INotice} should provide information besides the target attribute
 * state. Examples for this are:
 * 
 * <ul>
 * <li>The state of some concurrent activity/algorithm acting on the target
 * &ndash; The reading or parsing of some external resource is a good example
 * for this. Be aware that a notice is not suitable to bring up details
 * regarding the activity. Watching an {@link IActivity} lifecycle is more
 * appropriate if you need these details. A good example for a code for such a
 * notice is "activity.connect". It may be a good idea to attach the activity as
 * a detail.</li>
 * 
 * <li>The outcome of some activity/algorithm that worked on target &ndash;
 * Basically, you should use indicate the process and the outcome in the code,
 * like "activity.ok" or "activity.failed". To get some more detailed
 * information fast, one may attach a more specific context to the final state,
 * such as "activity.done:payload". A good candidate to append as a context
 * suffix may be the name of the attribute the activity worked on. It may be a
 * good idea to attach the activity as a detail.</li>
 * 
 * <li>The outcome of a validation that worked on target &ndash; This is a
 * special case of the above, where the algorithm is a validation. A good code
 * prefix is "validation", followed by the validation statement (like
 * "validation:required.name").</li>
 * </ul>
 * 
 * Be aware not to overuse this instrument. Most often there are more specific
 * ways to achieve this information - at the price of being a little more
 * complex.
 * 
 * E.g. for information about ongoing activities on a target you may interact
 * with the lifecycle of the activity itself (see {@link IActivity}.
 * 
 * For information on the state of the target you may connect to update
 * propagation mechanics of the target (see {@link INotificationSupport}.
 */
public interface INotice extends IMessage {

	public static final int SEVERITY_DEBUG = 0;
	public static final int SEVERITY_INFO = 10;
	public static final int SEVERITY_WARNING = 20;
	public static final int SEVERITY_ERROR = 30;

	/**
	 * The severity of the {@link INotice}. This is one of the SEVERITY_*
	 * constants or a user defined value.
	 * 
	 * @return The severity of the {@link INotice}
	 */
	public int getSeverity();

	/**
	 * Answer true if this is a sticky notice.
	 * 
	 * @return
	 */
	public boolean isSticky();

}
