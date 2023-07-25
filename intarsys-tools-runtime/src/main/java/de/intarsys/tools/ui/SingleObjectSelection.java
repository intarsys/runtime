/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("java:S2160") // no equals required
public class SingleObjectSelection<T> extends CommonSelection<T> {

	private T selection;

	public SingleObjectSelection(T selection) {
		super();
		if (selection == null) {
			throw new NullPointerException("selection content may not be null");
		}
		this.selection = selection;
	}

	@Override
	public List<T> getElements() {
		List<T> result = new ArrayList<>();
		result.add(selection);
		return result;
	}

	@Override
	public T getFirstElement() {
		return selection;
	}

	@Override
	public int getSize() {
		return selection == null ? 0 : 1;
	}

	@Override
	public boolean isEmpty() {
		return selection == null;
	}

}
