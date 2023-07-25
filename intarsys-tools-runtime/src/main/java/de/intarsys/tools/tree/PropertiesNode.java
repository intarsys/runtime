package de.intarsys.tools.tree;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.message.IMessageBundle;

/**
 * A common superclass for a node providing some grouped detail information on a
 * properties subset of its parent.
 * <p>
 * 
 */
public abstract class PropertiesNode<T extends Object> extends CommonNode<T> {

	private static final IMessageBundle Msg = PACKAGE.Messages;

	private String iconName = "treenode_properties"; //$NON-NLS-1$

	private String label = Msg.getString("PropertiesNode.label"); //$NON-NLS-1$

	protected PropertiesNode(CommonNode parent, T object) {
		super(parent, object);
	}

	@Override
	protected final CommonNode[] basicCreateChildren() {
		List<CommonNode> result = new ArrayList<>();
		createPropertyNodes(result);
		return result.toArray(new CommonNode[result.size()]);
	}

	protected abstract void createPropertyNodes(List<CommonNode> properties);

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
