package de.intarsys.tools.presentation;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

/**
 * A grouping of {@link PropertyPresentationItem} instances.
 * 
 */
public class PropertyPresentationGroup extends PropertyPresentation {

	final private List<PropertyPresentation> properties = new ArrayList<PropertyPresentation>();

	private PropertyPresentationItem header;

	public PropertyPresentationGroup(Object object, String label) {
		super(object, label);
	}

	public PropertyPresentationItem addProperty(PropertyPresentationItem p) {
		properties.add(p);
		return p;
	}

	public PropertyPresentationItem addProperty(String label, Object value) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				null, label, value, null);
		properties.add(p);
		return p;
	}

	public PropertyPresentationItem addProperty(String label, Object value,
			Format format) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				null, label, value, format);
		properties.add(p);
		return p;
	}

	public PropertyPresentationItem addPropertyDynamic(String name, String label) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				name, label);
		properties.add(p);
		return p;
	}

	public PropertyPresentationItem addPropertyDynamic(String name,
			String label, Format format) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				name, label, format);
		properties.add(p);
		return p;
	}

	public PropertyPresentationItem getHeader() {
		return header;
	}

	public List<PropertyPresentation> getProperties() {
		return properties;
	}

	public PropertyPresentation getProperty(int index) {
		if (index < 0 || index >= properties.size()) {
			return null;
		}
		return properties.get(index);
	}

	public void setHeader(PropertyPresentationItem header) {
		this.header = header;
	}

	public PropertyPresentationItem setHeader(String label, Object value) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				null, label, value, null);
		header = p;
		return p;
	}

	public PropertyPresentationItem setHeader(String label, Object value,
			Format format) {
		PropertyPresentationItem p = new PropertyPresentationItem(getObject(),
				null, label, value, format);
		header = p;
		return p;
	}

	public int size() {
		return properties.size();
	}
}
