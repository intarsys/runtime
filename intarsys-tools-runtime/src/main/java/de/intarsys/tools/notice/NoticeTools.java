package de.intarsys.tools.notice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.intarsys.tools.message.AliasMessage;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.state.AtomicState;

/**
 * Some tools to handle notices.
 * 
 */
public class NoticeTools {

	public static final String CODE_ACTIVITY = "activity"; //$NON-NLS-1$

	public static final String CODE_VALIDATION = "validation"; //$NON-NLS-1$

	public static void attachActivityNotice(int severity, AtomicState state, IMessage message, INoticesSupport target) {
		attachActivityNotice(severity, false, state, message, target);
	}

	public static void attachActivityNotice(int severity, boolean sticky, AtomicState state, IMessage message,
			INoticesSupport target) {
		String code = NoticeTools.getActivityCode(state.getId(), null);
		INotice notice = new Notice(severity, sticky, new AliasMessage(code, message));
		target.addNotice(notice);
	}

	/**
	 * Remove all {@link INotice} instances that are not marked as sticky.
	 * 
	 * @param ns
	 */
	public static void clearNonSticky(INoticesSupport ns) {
		List<INotice> notices = new ArrayList<>(ns.getNotices());
		for (INotice notice : notices) {
			if (!notice.isSticky()) {
				ns.removeNotice(notice);
			}
		}
	}

	/**
	 * Create a standard code for marking an activity state.
	 * 
	 * This code has the form "activity.&lt;state&gt;:&lt;optional context&gt;
	 * 
	 * @param state
	 * @param context
	 * @return
	 */
	public static String getActivityCode(String state, String context) {
		StringBuilder sb = new StringBuilder();
		sb.append(CODE_ACTIVITY);
		if (state != null) {
			sb.append(".");
			sb.append(state);
			if (context != null) {
				sb.append(":");
				sb.append(context);
			}
		}
		return sb.toString();
	}

	/**
	 * <code>true</code> if some {@link INotice} is of severity
	 * {@link INotice#SEVERITY_ERROR}
	 * 
	 * @param ns
	 * @return
	 */
	public static boolean hasError(INoticesSupport ns) {
		for (INotice notice : ns.getNotices()) {
			if (notice.getSeverity() == INotice.SEVERITY_ERROR) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if some {@link INotice} is of severity
	 * {@link INotice#SEVERITY_INFO}
	 * 
	 * @param ns
	 * @return
	 */
	public static boolean hasInfo(INoticesSupport ns) {
		for (INotice notice : ns.getNotices()) {
			if (notice.getSeverity() == INotice.SEVERITY_INFO) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if some {@link INotice} instances exist
	 * 
	 * @param ns
	 * @return
	 */
	public static boolean hasNotices(INoticesSupport ns) {
		return !ns.getNotices().isEmpty();
	}

	/**
	 * <code>true</code> if some {@link INotice} is of severity
	 * {@link INotice#SEVERITY_WARNING}
	 * 
	 * @param ns
	 * @return
	 */
	public static boolean hasWarning(INoticesSupport ns) {
		for (INotice notice : ns.getNotices()) {
			if (notice.getSeverity() == INotice.SEVERITY_WARNING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If notice has a lower severity than all existing notices, ignore. If notice
	 * has same or higher severity than all existing notices, then remove all
	 * existing notices and add the new one.
	 * 
	 * @param ns
	 * @param newNotice
	 */
	public static void setNotice(INoticesSupport ns, INotice newNotice) {
		for (INotice notice : ns.getNotices()) {
			if (notice.getSeverity() > newNotice.getSeverity()) {
				return;
			}
		}
		ns.clearNotices();
		ns.addNotice(newNotice);
	}

	/**
	 * Sorts all {@link INotice}s descending in severity, i.e. from
	 * {@link INotice#SEVERITY_ERROR} to {@link INotice#SEVERITY_DEBUG}
	 * 
	 * @param ns
	 * @return a new list containing the nodes sorted by severity
	 */
	public static List<INotice> sortBySeverity(INoticesSupport ns) {
		List<INotice> notices = new ArrayList<>(ns.getNotices());
		Collections.sort(notices, new Comparator<INotice>() {
			@Override
			public int compare(INotice o1, INotice o2) {
				if (o1.getSeverity() < o2.getSeverity()) {
					return 1;
				} else if (o1.getSeverity() > o2.getSeverity()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return notices;
	}

	private NoticeTools() {
	}

}
