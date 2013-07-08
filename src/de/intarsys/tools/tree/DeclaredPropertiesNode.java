package de.intarsys.tools.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic {@link PropertiesNode} to collect predefined property children.
 * These are for example created by the parent node directly.
 * 
 */
public class DeclaredPropertiesNode extends PropertiesNode {

	private List<CommonNode> properties = new ArrayList<CommonNode>();

	public DeclaredPropertiesNode(CommonNode parent, Object object) {
		super(parent, object);
	};

	public void addPropertyNode(CommonNode node) {
		properties.add(node);
		updateChildren();
	}

	public void clearPropertyNodes() {
		properties.clear();
		unlinkChildren();
	}

	@Override
	protected void createPropertyNodes(List<CommonNode> pProperties) {
		pProperties.addAll(properties);
	}

	public boolean isEmpty() {
		return properties.size() == 0;
	}

	public void removePropertyNode(CommonNode node) {
		properties.remove(node);
		node.unlink();
		updateChildren();
	}
}
