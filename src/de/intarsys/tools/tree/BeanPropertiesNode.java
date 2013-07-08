package de.intarsys.tools.tree;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * A {@link PropertiesNode} that reflects the object properties using plain Java
 * beans techniques.
 * 
 */
public class BeanPropertiesNode extends PropertiesNode {

	public BeanPropertiesNode(CommonNode parent, Object object) {
		super(parent, object);
	};

	@Override
	protected void createPropertyNodes(List<CommonNode> properties) {
		try {
			BeanInfo info = Introspector.getBeanInfo(getObject().getClass());
			PropertyDescriptor[] propertyDescriptors = info
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
				PropertyNode tempNode = new PropertyNode(this, getObject(),
						propertyDescriptor.getDisplayName(),
						propertyDescriptor.getName(), false);
				Object tempValue = tempNode.getPropertyValue();
				if (tempValue instanceof String || tempValue instanceof Number) {
					properties.add(tempNode);
				}
			}
		} catch (Exception e) {
			//
		}
	}

}
