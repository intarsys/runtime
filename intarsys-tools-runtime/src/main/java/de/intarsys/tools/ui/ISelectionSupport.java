/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

/**
 * An object that is able to manage a selection.
 * 
 */
public interface ISelectionSupport {

	/**
	 * The generic selection object or null.
	 * 
	 * @return The generic selection object or null.
	 */
	public ISelection getSelection();

	/**
	 * Set the generic selection object. To terminate a selection set the
	 * selection to null.
	 * <p>
	 * The selection is not necessarily interpreted by the receiver itself. It
	 * is used as a clipboard for interested clients to store and share their
	 * selection in whatever form they need.
	 * 
	 * @param selection
	 *            The new current selection
	 */
	public void setSelection(ISelection selection);
}
