package de.intarsys.tools.string;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class TestTools {

	public static String canonicalize(String value) {
		StringWriter w = new StringWriter();
		try {
			StringReader r = new StringReader(value);
			while (true) {
				int ca = read(r);
				if (ca == -1) {
					break;
				}
				if (ca == '"') {
					w.write("'");
					continue;
				}
				w.write(ca);
			}
		} catch (IOException e) {
			return "";
		}
		return w.toString();
	}

	public static boolean equals(String a, String b) {
		try {
			StringReader ra = new StringReader(a);
			StringReader rb = new StringReader(b);
			while (true) {
				int ca = read(ra);
				int cb = read(rb);
				if (ca == -1 && cb == -1) {
					return true;
				}
				if (ca == -1 || cb == -1) {
					return false;
				}
				if (ca == cb) {
					continue;
				}
				if (ca == '"' && cb == '\'') {
					continue;
				}
				if (ca == '\'' && cb == '"') {
					continue;
				}
				return false;
			}
		} catch (IOException e) {
			return false;
		}
	}

	protected static int read(StringReader r) throws IOException {
		int c = r.read();
		while (Character.isWhitespace(c)) {
			c = r.read();
		}
		return c;
	}

	private TestTools() {
	}
}
