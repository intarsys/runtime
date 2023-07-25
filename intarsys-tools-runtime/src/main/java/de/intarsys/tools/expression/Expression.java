package de.intarsys.tools.expression;

/**
 * The abstract representation of an expression.
 * 
 */
public abstract class Expression {

	public abstract String getCode();

	public boolean isFunction() {
		return false;
	}

	public boolean isParantheses() {
		return false;
	}

	public boolean isQuoted() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public boolean isToken() {
		return false;
	}

	@Override
	public String toString() {
		return getCode();
	}

}
