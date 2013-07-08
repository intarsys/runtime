package de.intarsys.tools.tree;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.message.MessageBundle;

/**
 * A common superclass for a node providing some grouped detail information on a
 * properties subset of its parent.
 * <p>
 * 
 */
abstract public class PropertiesNode extends CommonNode {

	private static final MessageBundle Msg = PACKAGE.Messages;

	private String iconName = "icons/treenode_properties"; //$NON-NLS-1$

	private String label = Msg.getString("PropertiesNode.label"); //$NON-NLS-1$

	protected PropertiesNode(CommonNode parent, Object object) {
		super(parent, object);
	}

	@Override
	final protected CommonNode[] basicCreateChildren() {
		List<CommonNode> result = new ArrayList<CommonNode>();
		createPropertyNodes(result);
		return result.toArray(new CommonNode[result.size()]);
	}

	abstract protected void createPropertyNodes(List<CommonNode> properties);

	@Override
	public String getIconName() {
		return iconName;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	protected void onAttributeChanged(AttributeChangedEvent event) {
		// we are static aggregation nodes....
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
