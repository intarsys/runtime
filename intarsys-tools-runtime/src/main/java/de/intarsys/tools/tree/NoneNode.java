package de.intarsys.tools.tree;

import de.intarsys.tools.message.IMessageBundle;

/**
 * The "none" node.
 * 
 */
public class NoneNode extends CommonNode {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	public NoneNode(CommonNode parent, Object object) {
		super(parent, object);
	}

	@Override
	public String getIconName() {
		return "treenode_none"; //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		return Msg.getString("NoneNode.label"); //$NON-NLS-1$
	}
}
