package de.intarsys.tools.tag;

import java.util.Collections;
import java.util.List;

public class TagList implements ITagSupport {

	private final List<Tag> tags;

	public TagList(List<Tag> tags) {
		super();
		this.tags = tags == null ? Collections.emptyList() : tags;
	}

	@Override
	public List<Tag> getTags() {
		return tags;
	}

}
