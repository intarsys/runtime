package de.intarsys.tools.notice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Some tools to handle notices.
 * 
 */
public class NoticeTools {

	/**
	 * Remove all {@link Notice} instances that are not marked as sticky.
	 * 
	 * @param ns
	 */
	public static void clearNonSticky(INoticesSupport ns) {
		List<Notice> notices = new ArrayList<>(ns.getNotices());
		for (Notice notice : notices) {
			if (!notice.isSticky()) {
				ns.removeNotice(notice);
			}
		}
	}

	/**
	 * <code>true</code> if some {@link Notice} is of severity {@link Notice#SEVERITY_ERROR}
	 * 
	 * @param ns
	 * @return
	 */
	static public boolean hasError(INoticesSupport ns) {
		for (Notice notice : ns.getNotices()) {
			if (notice.isError()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if some {@link Notice} is of severity {@link Notice#SEVERITY_INFO}
	 * 
	 * @param ns
	 * @return
	 */
	static public boolean hasInfo(INoticesSupport ns) {
		for (Notice notice : ns.getNotices()) {
			if (notice.isInfo()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if some {@link Notice} instances exist
	 * 
	 * @param ns
	 * @return
	 */
	static public boolean hasNotices(INoticesSupport ns) {
		return ns.getNotices().size() > 0;
	}

	/**
	 * <code>true</code> if some {@link Notice} is of severity {@link Notice#SEVERITY_WARNING}
	 * 
	 * @param ns
	 * @return
	 */
	static public boolean hasWarning(INoticesSupport ns) {
		for (Notice notice : ns.getNotices()) {
			if (notice.isWarning()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sorts all {@link Notice}s descending in severity, i.e. from {@link Notice#SEVERITY_ERROR} to
	 * {@link Notice#SEVERITY_DEBUG}
	 * 
	 * @param ns
	 * @return a new list containing the nodes sorted by severity
	 */
	static public List<Notice> sortBySeverity(INoticesSupport ns) {
		List<Notice> notices = new ArrayList<Notice>(ns.getNotices());
		Collections.sort(notices, new Comparator<Notice>() {
			@Override
			public int compare(Notice o1, Notice o2) {
				if (o1.getSeverity() < o2.getSeverity()) {
					return 1;
				} else if (o1.getSeverity() > o2.getSeverity()) {
					return -1;
				} else {
					return 0;
				}
			};
		});
		return notices;
	}

}
