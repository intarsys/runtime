package de.intarsys.tools.tag;

import java.util.List;

/**
 * An object that natively supports tags.
 * 
 */
public interface ITagSupport {

	/**
	 * The tag list for this object.
	 * 
	 * @return The tag list for this object.
	 */
	public List<Tag> getTags();
}
