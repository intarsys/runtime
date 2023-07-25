package de.intarsys.tools.string;

/**
 * A simple string value along with the information if the serialized value
 * is/was quoted.
 *
 */
public class Token {

	private final String value;

	private final char quote;

	private boolean eof;

	public Token(String value) {
		super();
		this.value = value;
		this.quote = 0;
		this.eof = false;
	}

	public Token(String value, char quote) {
		super();
		this.value = value;
		this.quote = quote;
		this.eof = false;
	}

	public Token(String value, char quote, boolean eof) {
		super();
		this.value = value;
		this.quote = quote;
		this.eof = eof;
	}

	public char getQuote() {
		return quote;
	}

	public String getValue() {
		return value;
	}

	public boolean isEof() {
		return eof;
	}

	public boolean isQuoted() {
		return quote != 0;
	}

	public void setEof(boolean eof) {
		this.eof = eof;
	}

	@Override
	public String toString() {
		return "t" + (isQuoted() ? "'" : "") + "(" + getValue() + ")";
	}
}
