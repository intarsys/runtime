package de.intarsys.tools.expression;

import java.util.Objects;

import de.intarsys.tools.string.StringTools;

public class StringLiteral extends Expression {

	private final String value;

	private final char quote;

	public StringLiteral(String value, char quote) {
		super();
		Objects.requireNonNull(value, "'value' must be nun null");
		this.value = value;
		this.quote = quote;
	}

	@Override
	public String getCode() {
		return StringTools.quote(value, quote);
	}

	public char getQuote() {
		return quote;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean isQuoted() {
		return true;
	}

	@Override
	public boolean isString() {
		return true;
	}
}
