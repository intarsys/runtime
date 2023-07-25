package de.intarsys.tools.tree;

public class NodeHandlerAdapter implements INodeHandler {

	@Override
	public CommonNode[] createChildren(CommonNode node) {
		return node.basicCreateChildren();
	}

	@Override
	public String getDescription(Object object) {
		return ((CommonNode) object).basicGetObjectDescription();
	}

	@Override
	public String getIconName(Object object) {
		return ((CommonNode) object).basicGetObjectIconName();
	}

	@Override
	public String getLabel(Object object) {
		return ((CommonNode) object).basicGetObjectLabel();
	}

	@Override
	public String getTip(Object object) {
		return ((CommonNode) object).basicGetObjectTip();
	}

	@Override
	public boolean hasChildren(CommonNode node) {
		return node.hasChildren();
	}

}
