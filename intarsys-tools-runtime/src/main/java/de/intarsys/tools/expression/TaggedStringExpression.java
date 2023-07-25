package de.intarsys.tools.expression;

/**
 * An expression syntax node "${expr}" in the template evaluation framework.
 * 
 */
public class TaggedStringExpression extends TaggedStringNode {

	private String expression;

	public TaggedStringExpression(String expression) {
		super();
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	public boolean isExpression() {
		return true;
	}

	public boolean isLiteral() {
		return false;
	}

	public void setExpression(String expr) {
		this.expression = expr;
	}

	@Override
	public String toString() {
		return getExpression();
	}

	@Override
	public String toTemplate() {
		return "${" + getExpression() + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
