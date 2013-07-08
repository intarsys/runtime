package de.intarsys.tools.tree;

import java.text.Format;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.reflect.FieldException;
import de.intarsys.tools.reflect.ObjectTools;
import de.intarsys.tools.string.StringTools;

/**
 * A common superclass for a node providing detail information on a specifc
 * property of its parent.
 * <p>
 */
public class PropertyNode extends CommonNode {

	private Format propertyFormat;

	private String propertyLabel;

	private String propertyName;

	final private boolean reusable;

	public PropertyNode(CommonNode parent, Object object, String label,
			String name, boolean reusable) {
		super(parent, object);
		this.propertyLabel = label;
		this.propertyName = name;
		this.reusable = reusable;
	};

	@Override
	public String getIconName() {
		return "icons/treenode_property"; //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		Object value = getPropertyValue();
		String valueLabel;
		if (propertyFormat != null) {
			try {
				valueLabel = propertyFormat.format(value);
			} catch (Exception e) {
				valueLabel = StringTools.safeString(value);
			}
		} else {
			if (value instanceof IPresentationSupport) {
				valueLabel = ((IPresentationSupport) value).getLabel();
			} else {
				valueLabel = StringTools.safeString(value);
			}
		}
		return getPropertyLabel() + "=" + valueLabel; //$NON-NLS-1$
	}

	public Format getPropertyFormat() {
		return propertyFormat;
	}

	public String getPropertyLabel() {
		return propertyLabel;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getPropertyValue() {
		try {
			return ObjectTools.get(getObject(), getPropertyName());
		} catch (FieldException e) {
			return "<not available>"; //$NON-NLS-1$
		}
	}

	@Override
	protected boolean isReusable() {
		return reusable;
	}

	@Override
	protected void onAttributeChanged(AttributeChangedEvent event) {
		String tempName = event.getAttribute() instanceof String ? (String) event
				.getAttribute() : "?";
		if (propertyName.startsWith(tempName)) {
			super.onAttributeChanged(event);
		}
	}

	public void setPropertyFormat(Format propertyFormat) {
		this.propertyFormat = propertyFormat;
	}

}
