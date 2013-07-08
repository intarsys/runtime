package de.intarsys.tools.presentation;

/**
 * Common superclass for the PropertyPresentation implementation.
 * <p>
 * This implementation summarizes all methods for representing the "field"
 * (meta) part of the presentation.
 * 
 */
abstract public class PropertyPresentation implements IPresentationSupport {

	private final String label;

	private final Object object;

	protected PropertyPresentation(Object object, String label) {
		super();
		this.object = object;
		this.label = label;
	}

	public String getDescription() {
		return getTip();
	}

	public IPresentationSupport getFieldPresentation() {
		return this;
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

	public IPresentationSupport getValuePresentation() {
		return this;
	}

}