package de.intarsys.tools.expression;

/**
 * A literal part in a tagged string.
 * 
 */
public class TaggedStringLiteral extends TaggedStringNode {

	private String text;

	public TaggedStringLiteral(String text) {
		super();
		this.text = text;
	}

	public String getLabel() {
		String label = text.replaceAll("\\n", "\\\\n"); //$NON-NLS-1$ //$NON-NLS-2$
		label = label.replaceAll("\\t", "\\\\t"); //$NON-NLS-1$ //$NON-NLS-2$
		label = label.replaceAll(" ", "_"); //$NON-NLS-1$ //$NON-NLS-2$
		return "\"" + label + "\"";
	}

	public String getText() {
		return text;
	}

	public boolean isExpression() {
		return false;
	}

	public boolean isLiteral() {
		return true;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public String toTemplate() {
		return text;
	}
}
