package de.intarsys.tools.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class JsonReader {

	public static Object tokenToValue(String string) {
		if ("true".equalsIgnoreCase(string)) {
			return Boolean.TRUE;
		}
		if ("false".equalsIgnoreCase(string)) {
			return Boolean.FALSE;
		}
		if ("null".equalsIgnoreCase(string)) {
			return null;
		}
		char b = string.charAt(0);
		if ((b >= '0' && b <= '9') || b == '-' || b == '.') {
			try {
				if (string.indexOf('.') > -1 || string.indexOf('e') > -1 || string.indexOf('E') > -1) {
					Double d = Double.valueOf(string);
					if (!d.isInfinite() && !d.isNaN()) {
						return d;
					}
				} else {
					Long myLong = Long.valueOf(string);
					if (string.equals(myLong.toString())) {
						if (myLong.longValue() == myLong.intValue()) {
							return Integer.valueOf(myLong.intValue());
						} else {
							return myLong;
						}
					}
				}
			} catch (Exception ignore) {
				//
			}
		}
		return string;
	}

	private long column;
	private long index;
	private long line;
	private int buffer;

	private final Reader reader;

	public JsonReader(InputStream inputStream) throws IOException {
		this(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	}

	public JsonReader(Reader pReader) {
		reader = pReader.markSupported() ? pReader : new BufferedReader(pReader);
		buffer = 0;
		index = 0;
		column = 0;
		line = 1;
	}

	public JsonReader(String s) {
		this(new StringReader(s));
	}

	protected IOException error(String message) {
		return new IOException(message + getPosition());
	}

	protected String getPosition() {
		return " at " + index + " [" + line + ", " + column + " ]";
	}

	protected Reader getReader() {
		return reader;
	}

	protected int read(boolean endExpected) throws IOException {
		int c;
		if (buffer != 0) {
			c = buffer;
			buffer = 0;
			return c;
		}
		c = reader.read();
		if (c == -1) {
			if (!endExpected) {
				throw error("unexpected end of input");
			}
			return c;
		}
		index++;
		if (c == '\n') {
			line++;
			column = 0;
		} else {
			column++;
		}
		return c;
	}

	protected char[] read(int n) throws IOException {
		char[] chars = new char[n];
		int pos = 0;
		while (pos < n) {
			char c = (char) read(false);
			chars[pos++] = c;
		}
		return chars;
	}

	public JsonArray readJsonArray() throws IOException {
		int c = readNonWS(true);
		if (c == -1) {
			return null;
		}
		if (c != '[') {
			throw error("'[' expected");
		}
		JsonArray result = new JsonArray();
		// empty
		c = readNonWS(false);
		if (c == ']') {
			return result;
		}
		for (;;) {
			unread(c);
			result.basicAdd(readValue());
			c = readNonWS(false);
			if (c == ',') {
				c = readNonWS(false);
				continue;
			}
			if (c == ']') {
				return result;
			}
			throw error("',' or ']' expected");
		}
	}

	public JsonObject readJsonObject() throws IOException {
		int c = readNonWS(true);
		if (c == 1) {
			return null;
		}
		if (c != '{') {
			throw error("'{' expected");
		}
		JsonObject result = new JsonObject();
		String key;
		c = readNonWS(false);
		if (c == '}') {
			return result;
		}
		for (;;) {
			// key part
			unread(c);
			Object value = readValue();
			key = value == null ? null : value.toString();
			// separator
			c = readNonWS(false);
			if (c != ':') {
				throw error("':' expected");
			}
			// value part
			value = readValue();
			result.basicPut(key, value);
			// going on?
			c = readNonWS(false);
			if (c == '}') {
				return result;
			}
			if (c == ',') {
				c = readNonWS(false);
				continue;
			}
			throw error("',' or '}' expected");
		}
	}

	protected int readNonWS(boolean endExpected) throws IOException {
		for (;;) {
			int c = read(endExpected);
			if (c == -1 || c > ' ') {
				return c;
			}
		}
	}

	public String readString(int quote) throws IOException {
		int c = readNonWS(true);
		if (c == 1) {
			return null;
		}
		if (c != quote) {
			throw error("'" + (char) quote + "' expected");
		}
		StringBuilder sb = new StringBuilder();
		for (;;) {
			c = read(false);
			switch (c) {
			case '\n':
			case '\r':
				throw error("string not terminated");
			case '\\':
				c = read(false);
				switch (c) {
				case 'b':
					sb.append('\b');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 'u':
					sb.append((char) Integer.parseInt(new String(read(4)), 16));
					break;
				case '"':
				case '\'':
				case '\\':
				case '/':
					sb.append((char) c);
					break;
				default:
					throw error("illegal escape code '\\" + (char) c + "'");
				}
				break;
			default:
				if (c == quote) {
					return sb.toString();
				}
				sb.append((char) c);
			}
		}
	}

	public Object readUnquoted() throws IOException {
		int c = readNonWS(true);
		if (c == -1) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		while (c > ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
			sb.append((char) c);
			c = read(true);
		}
		String token = sb.toString().trim();
		if (token.length() == 0) {
			throw error("unexpected char '" + (char) c + "'");
		}
		if (c != -1) {
			unread(c);
		}
		return tokenToValue(token);
	}

	public Object readValue() throws IOException {
		int c = readNonWS(true);
		switch (c) {
		case -1:
			return null;
		case '"':
		case '\'':
			unread(c);
			return readString(c);
		case '{':
			unread(c);
			return readJsonObject();
		case '[':
			unread(c);
			return readJsonArray();
		default:
			unread(c);
			return readUnquoted();
		}
	}

	protected void unread(int c) throws IOException {
		buffer = c;
	}
}
