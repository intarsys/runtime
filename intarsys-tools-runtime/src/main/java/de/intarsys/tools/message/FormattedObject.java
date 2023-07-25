package de.intarsys.tools.message;

import java.text.Format;

import de.intarsys.tools.string.StringTools;

/**
 * This is a helper container for an object that gets formatted in an
 * {@link IMessage}. It allows to inject a preformatted view on the plain object
 * while still able to reconstruct the original object argument.
 * 
 * The {@link FormattedObject} is simply injected like any argument in the
 * {@link IMessage} factory methods. The message itself splits it up into the
 * plain and the formatted part.
 * 
 */
public class FormattedObject {

	private final Object object;

	private final String label;

	private final Format format;

	public FormattedObject(Object object, Format format) {
		super();
		this.object = object;
		this.label = null;
		this.format = format;
	}

	public FormattedObject(Object object, String label) {
		super();
		this.object = object;
		this.label = label;
		this.format = null;
	}

	public Format getFormat() {
		return format;
	}

	public String getFormattedObject() {
		if (getLabel() != null) {
			return getLabel();
		}
		if (getFormat() != null) {
			return getFormat().format(getObject());
		}
		return StringTools.safeString(getObject());
	}

	public String getLabel() {
		return label;
	}

	public Object getObject() {
		return object;
	}

	@Override
	public String toString() {
		return getFormattedObject();
	}
}
