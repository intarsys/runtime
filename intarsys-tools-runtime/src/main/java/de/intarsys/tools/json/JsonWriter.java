package de.intarsys.tools.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JsonWriter {

	public static void quote(String string, Writer w) throws IOException {
		if (string == null || string.length() == 0) {
			w.write("\"\"");
			return;
		}

		char b;
		char c = 0;
		String hhhh;
		int i;
		int len = string.length();

		w.write('"');
		for (i = 0; i < len; i += 1) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				w.write('\\');
				w.write(c);
				break;
			case '/':
				if (b == '<') {
					w.write('\\');
				}
				w.write(c);
				break;
			case '\b':
				w.write("\\b");
				break;
			case '\t':
				w.write("\\t");
				break;
			case '\n':
				w.write("\\n");
				break;
			case '\f':
				w.write("\\f");
				break;
			case '\r':
				w.write("\\r");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
					w.write("\\u");
					hhhh = Integer.toHexString(c);
					w.write("0000", 0, 4 - hhhh.length());
					w.write(hhhh);
				} else {
					w.write(c);
				}
			}
		}
		w.write('"');
	}

	public static String toString(Object value, int indentFactor, int indent) throws IOException {
		StringWriter sw = new StringWriter();
		new JsonWriter(sw).write(value, indentFactor, indent);
		return sw.toString();
	}

	private final Writer writer;

	public JsonWriter(Writer w) {
		super();
		this.writer = w;
	}

	protected Writer getWriter() {
		return writer;
	}

	protected void indent(Writer writer, int indent) throws IOException {
		for (int i = 0; i < indent; i += 1) {
			writer.write(' ');
		}
	}

	public void write(Object value, int indentFactor, int indent) throws IOException {
		if (value == null) {
			getWriter().write("null");
		} else if (value instanceof JsonObject) {
			writeObject((JsonObject) value, indentFactor, indent);
		} else if (value instanceof Map) {
			writeMap((Map) value, indentFactor, indent);
		} else if (value instanceof JsonArray) {
			writeArray((JsonArray) value, indentFactor, indent);
		} else if (value instanceof Collection) {
			writeCollection((Collection) value, indentFactor, indent);
		} else if (value instanceof Number) {
			getWriter().write(Json.numberToString((Number) value));
		} else if (value instanceof Boolean) {
			getWriter().write(value.toString());
		} else {
			JsonWriter.quote(value.toString(), writer);
		}
	}

	protected void writeArray(JsonArray value, int indentFactor, int indent) throws IOException {
		boolean commanate = false;
		int length = value.size();
		writer.write('[');

		if (length == 1) {
			write(value.get(0), indentFactor, indent);
		} else if (length != 0) {
			final int newindent = indent + indentFactor;
			Iterator<Object> it = value.iterator();
			while (it.hasNext()) {
				Object child = it.next();
				if (commanate) {
					writer.write(',');
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, newindent);
				write(child, indentFactor, newindent);
				commanate = true;
			}
			if (indentFactor > 0) {
				writer.write('\n');
			}
			indent(writer, indent);
		}
		writer.write(']');
	}

	protected void writeCollection(Collection value, int indentFactor, int indent) throws IOException {
		boolean commanate = false;
		int length = value.size();
		writer.write('[');

		if (length == 1) {
			write(value.iterator().next(), indentFactor, indent);
		} else if (length != 0) {
			final int newindent = indent + indentFactor;
			Iterator<Object> it = value.iterator();
			while (it.hasNext()) {
				Object child = it.next();
				if (commanate) {
					writer.write(',');
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, newindent);
				write(child, indentFactor, newindent);
				commanate = true;
			}
			if (indentFactor > 0) {
				writer.write('\n');
			}
			indent(writer, indent);
		}
		writer.write(']');
	}

	protected void writeMap(Map value, int indentFactor, int indent) throws IOException {
		boolean commanate = false;
		final int length = value.size();
		Iterator<String> itNames = value.keySet().iterator();
		writer.write('{');

		if (length == 1) {
			String key = itNames.next();
			writer.write(Json.quote(key));
			writer.write(':');
			if (indentFactor > 0) {
				writer.write(' ');
			}
			write(value.get(key), indentFactor, indent);
		} else if (length != 0) {
			final int newindent = indent + indentFactor;
			while (itNames.hasNext()) {
				String key = itNames.next();
				if (commanate) {
					writer.write(',');
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, newindent);
				writer.write(Json.quote(key));
				writer.write(':');
				if (indentFactor > 0) {
					writer.write(' ');
				}
				write(value.get(key), indentFactor, newindent);
				commanate = true;
			}
			if (indentFactor > 0) {
				writer.write('\n');
			}
			indent(writer, indent);
		}
		writer.write('}');
	}

	protected void writeObject(JsonObject value, int indentFactor, int indent) throws IOException {
		boolean commanate = false;
		final int length = value.size();
		Iterator<String> itNames = value.names().iterator();
		writer.write('{');

		if (length == 1) {
			String key = itNames.next();
			writer.write(Json.quote(key));
			writer.write(':');
			if (indentFactor > 0) {
				writer.write(' ');
			}
			write(value.get(key), indentFactor, indent);
		} else if (length != 0) {
			final int newindent = indent + indentFactor;
			while (itNames.hasNext()) {
				String key = itNames.next();
				if (commanate) {
					writer.write(',');
				}
				if (indentFactor > 0) {
					writer.write('\n');
				}
				indent(writer, newindent);
				writer.write(Json.quote(key));
				writer.write(':');
				if (indentFactor > 0) {
					writer.write(' ');
				}
				write(value.get(key), indentFactor, newindent);
				commanate = true;
			}
			if (indentFactor > 0) {
				writer.write('\n');
			}
			indent(writer, indent);
		}
		writer.write('}');
	}
}
