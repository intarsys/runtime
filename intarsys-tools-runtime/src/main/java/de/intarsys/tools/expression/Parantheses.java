package de.intarsys.tools.expression;

/**
 * Parantheses around an {@link Expression}.
 * 
 */
public class Parantheses extends Expression {

	private final Expression nested;

	public Parantheses(Expression nested) {
		this.nested = nested;
	}

	@Override
	public String getCode() {
		if (getNested() == null) {
			return "()";
		} else {
			return "(" + getNested().getCode() + ")";
		}
	}

	public Expression getNested() {
		return nested;
	}

	@Override
	public boolean isParantheses() {
		return true;
	}
}
