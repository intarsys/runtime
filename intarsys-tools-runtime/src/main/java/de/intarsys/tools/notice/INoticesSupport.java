package de.intarsys.tools.notice;

import java.util.List;

import de.intarsys.tools.reflect.InvocableMethod;

/**
 * An object that can handles {@link INotice} tags.
 * 
 */
public interface INoticesSupport {

	/**
	 * Add a new {@link INotice}
	 * 
	 * @param notice
	 */
	public void addNotice(INotice notice);

	/**
	 * Remove all {@link INotice} instances from the target.
	 */
	public void clearNotices();

	/**
	 * A list of {@link INotice} instances associated with this object. If no
	 * notices are available, this method returns an empty list.
	 * 
	 * Its invalid to add or remove notices directly, the effects of such an
	 * operation are undefined.
	 * 
	 * @return a list of {@link INotice} instances associated with this object,
	 *         never null.
	 */
	@InvocableMethod
	public List<INotice> getNotices();

	/**
	 * Remove the {@link INotice} from the target.
	 * 
	 * @param notice
	 * @return
	 */
	public boolean removeNotice(INotice notice);
}
