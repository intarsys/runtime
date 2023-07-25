/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S2160") // no equals required
public class ListSelection<T> extends CommonSelection<T> {

	private List<T> selection;

	public ListSelection(List<T> selection) {
		super();
		if (selection == null) {
			throw new NullPointerException("selection content may not be null");
		}
		this.selection = selection;
	}

	@Override
	public List<T> getElements() {
		return new ArrayList<>(selection);
	}

	@Override
	public T getFirstElement() {
		return selection.iterator().next();
	}

	@Override
	public int getSize() {
		return selection.size();
	}

	@Override
	public boolean isEmpty() {
		return selection.isEmpty();
	}

}
