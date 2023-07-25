package de.intarsys.tools.tree;

import de.intarsys.tools.presentation.IPresentationHandler;

/**
 * This strategy object allows modification of a {@link CommonNode} behavior if
 * it is not appropriate to create a subclass of its own.
 * 
 */
public interface INodeHandler extends IPresentationHandler {

	public CommonNode[] createChildren(CommonNode node);

	public boolean hasChildren(CommonNode node);

}
