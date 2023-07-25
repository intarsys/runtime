package de.intarsys.tools.expression;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import de.intarsys.tools.string.StringTools;

/**
 * A parser for a simple expression syntax.
 * 
 * <pre>
 * expression ::= [string] [open expression close] [token open expression close] [delimiter | EOF]
 * string ::= quote chars quote
 * token ::= chars
 * open ::= "{" | "[" | "("
 * close ::= "}" | "]" | ")"
 * </pre>
 * 
 */
public class ExpressionParser {

	private final char delimiter;

	public ExpressionParser() {
		this.delimiter = ';';
	}

	public ExpressionParser(char delimiter) {
		super();
		this.delimiter = delimiter;
	}

	public char getDelimiter() {
		return delimiter;
	}

	/**
	 * Read an "expression" from r. The end of the expression is marked by delimiter or EOF.
	 * 
	 * @param reader
	 * @return an {@link Expression}
	 * @throws IOException
	 *             If IO operation fails
	 */
	@SuppressWarnings("java:S2677")
	public Expression parse(Reader reader) throws IOException {
		Expression result = parseExpression(reader, false);
		reader.read();
		// ':' or EOF expected
		return result;
	}

	protected Expression parseExpression(Reader reader, boolean nested) throws IOException {
		while (true) {
			reader.mark(2);
			int i = reader.read();
			if (i == -1 || i == delimiter || i == ')') {
				reader.reset();
				return null;
			} else if (Character.isWhitespace(i)) {
				// do nothing
			} else if (i == ',') {
				reader.reset();
				return new Token("");
			} else if (i == '"' || i == '\'') {
				return parseStringLiteral(reader, (char) i);
			} else if (i == '(') {
				return parseParantheses(reader);
			} else {
				reader.reset();
				return parseTokenOrFunction(reader, nested);
			}
		}
	}

	@SuppressWarnings("java:S2677")
	protected Expression parseFunction(Reader reader, String name) throws IOException {
		ArrayList<Expression> args = new ArrayList<>();
		Expression arg = parseExpression(reader, true);
		while (arg != null) {
			args.add(arg);
			arg = null;
			reader.mark(2);
			int i = reader.read();
			if (i == -1 || i == delimiter) {
				throw new IOException("unexpected end of expression");
			} else if (i == ')') {
				reader.reset();
			} else if (Character.isWhitespace(i)) {
				// do nothing
			} else if (i == ',') {
				arg = parseExpression(reader, true);
			} else {
				throw new IOException("unexpected char '" + (char) i + "' in expression");
			}
		}
		reader.read();
		// ')' expected
		return new Function(name, args);
	}

	@SuppressWarnings("java:S2677")
	protected Expression parseParantheses(Reader reader) throws IOException {
		Expression result = parseExpression(reader, true);
		while (true) {
			int i = reader.read();
			if (i == -1 || i == delimiter) {
				throw new IOException("unexpected end of expression");
			} else if (i == ')') {
				break;
			} else if (Character.isWhitespace(i)) {
				// do nothing
			} else if (i == ',') {
				throw new IOException("unexpected char '" + (char) i + "' in expression");
			}
		}
		return new Parantheses(result);
	}

	protected StringLiteral parseStringLiteral(Reader reader, char quote)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			int i = reader.read();
			if (i == -1) {
				return new StringLiteral(sb.toString(), quote);
			} else if (i == '\\') {
				i = reader.read();
				if (i != -1) {
					String mapped = StringTools.ESCAPES.get(Character.valueOf((char) i));
					if (mapped != null) {
						sb.append(mapped);
					}
				}
			} else if (i == quote) {
				return new StringLiteral(sb.toString(), quote);
			} else {
				sb.append((char) i);
			}
		}
	}

	protected Expression parseTokenOrFunction(Reader reader, boolean nested) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (true) {
			reader.mark(2);
			int i = reader.read();
			if (i == -1) {
				return new Token(sb.toString().trim());
			} else if (i == ',' || i == ')' || (i == delimiter && !nested)) {
				reader.reset();
				return new Token(sb.toString().trim());
			} else if (i == '(') {
				return parseFunction(reader, sb.toString().trim());
			} else {
				sb.append((char) i);
			}
		}
	}

}
