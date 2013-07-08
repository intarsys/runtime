package de.intarsys.tools.tree;

import de.intarsys.tools.message.MessageBundle;

/**
 * The "none" node.
 * 
 */
public class NoneNode extends CommonNode {

	private static final MessageBundle Msg = PACKAGE.Messages;

	public NoneNode(CommonNode parent, Object object) {
		super(parent, object);
	}

	@Override
	public String getIconName() {
		return "icons/treenode_none"; //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		return Msg.getString("NoneNode.label"); //$NON-NLS-1$
	}
}
