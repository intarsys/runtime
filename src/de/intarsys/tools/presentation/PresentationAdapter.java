package de.intarsys.tools.presentation;

import de.intarsys.tools.string.StringTools;

/**
 * A simple tool for creating a {@link IPresentationSupport} aware objects.
 * 
 */
public class PresentationAdapter implements IPresentationSupport {

	public static IPresentationSupport create(Object object) {
		if (object instanceof IPresentationSupport) {
			return (IPresentationSupport) object;
		}
		return new PresentationAdapter(object, StringTools.safeString(object));
	}

	public static IPresentationSupport create(Object object, String label) {
		if (object instanceof IPresentationSupport) {
			return (IPresentationSupport) object;
		}
		return new PresentationAdapter(object, label);
	}

	final private Object object;

	final private String label;

	public PresentationAdapter(Object object, String label) {
		super();
		this.object = object;
		this.label = label;
	}

	public String getDescription() {
		return getTip();
	}

	public String getIconName() {
		return null;
	}

	public String getLabel() {
		return label;
	}

	public Object getObject() {
		return object;
	}

	public String getTip() {
		return getLabel();
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
