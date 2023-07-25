/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.Collections;
import java.util.List;

public class EmptySelection<T> extends CommonSelection<T> {

	@Override
	public List<T> getElements() {
		return Collections.emptyList();
	}

	@Override
	public T getFirstElement() {
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

}
