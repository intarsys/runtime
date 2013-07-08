package de.intarsys.tools.notice;

import java.util.List;

/**
 * An object that can handles notices.
 * 
 */
public interface INoticesSupport {

	/**
	 * Add a new {@link Notice}
	 * 
	 * @param notice
	 */
	public void addNotice(Notice notice);

	/**
	 * Remove all {@link Notice} instances from the target.
	 */
	public void clearNotices();

	/**
	 * A list of {@link Notice} instances associated with this object.
	 * {@link Notice} instances are added and removed directly.
	 * 
	 * @return a list of {@link Notice} instances associated with this object.
	 */
	public List<Notice> getNotices();

	/**
	 * Remove the {@link Notice} from the target.
	 * 
	 * @param notice
	 * @return
	 */
	public boolean removeNotice(Notice notice);

}
