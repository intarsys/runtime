package de.intarsys.tools.expression;

public class Token extends Expression {

	private final String name;

	public Token(String value) {
		super();
		this.name = value;
	}

	@Override
	public String getCode() {
		return getName();
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isToken() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s(name=%s)", getClass().getSimpleName(), name);
	}
}
