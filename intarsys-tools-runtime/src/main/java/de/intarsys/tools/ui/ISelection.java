/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.util.List;

/**
 * An abstraction of a selection made by the user in an application.
 * <p>
 * The selection may be a single document element, for example an annotation or
 * a multi selection of objects.
 *
 */
public interface ISelection<T> {

	/**
	 * The list of selected objects
	 * 
	 * @return The list of selected objects
	 */
	public List<T> getElements();

	/**
	 * The first selected object in the list.
	 * 
	 * @return The first selected object in the list.
	 */
	public T getFirstElement();

	/**
	 * The number of objects selected.
	 * 
	 * @return The number of objects selected.
	 */
	public int getSize();

	/**
	 * "true" if no object is selected.
	 * 
	 * @return "true" if no object is selected.
	 */
	public boolean isEmpty();
}
