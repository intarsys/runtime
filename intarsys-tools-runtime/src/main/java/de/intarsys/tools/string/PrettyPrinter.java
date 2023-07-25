package de.intarsys.tools.string;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import de.intarsys.tools.hex.HexTools;

/**
 * Create a pretty printed version of an object.
 * 
 * The printing process can be customized by implementing
 * {@link IPrettyPrinterPrimitive} to support more basic type formatting and by
 * {@link IPrettyPrintable} to create a printed version of the own state.
 *
 */
public class PrettyPrinter {

	protected static class Context {

		protected IPrettyPrinterPrimitive primitive;

		protected Object object;

		protected boolean open;

		public Context(Object object) {
			this.object = object;
			if (object instanceof IPrettyPrinterPrimitive) {
				this.primitive = (IPrettyPrinterPrimitive) object;
			}
		}
	}

	private static final String NEWLINE = "\n";

	private static IPrettyPrinterPrimitive PrimitivePrinter = new IPrettyPrinterPrimitive() {
		@Override
		public boolean toString(PrettyPrinter printer, Object value) {
			return toStringPrimitive(printer, value);
		}
	};

	public static boolean toStringPrimitive(PrettyPrinter printer, Object value) {
		if (value instanceof String) {
			printer.appendString((String) value);
		} else if (value instanceof Integer) {
			printer.appendString("" + value);
			printer.appendString(", ");
			printer.appendString("0x");
			printer.appendString(Integer.toHexString((Integer) value));
		} else if (value == null) {
			printer.appendString("null");
		} else if (value instanceof byte[]) {
			printer.appendString(HexTools.bytesToHexString((byte[]) value));
		} else if (value instanceof IPrettyPrintable) {
			((IPrettyPrintable) value).toString(printer);
		} else if (value instanceof Map) {
			for (Map.Entry entry : (Set<Map.Entry>) ((Map) value).entrySet()) {
				printer.appendMember(String.valueOf(entry.getKey()), entry.getValue(), null);
			}
		} else if (value instanceof Collection) {
			int index = 0;
			for (Object element : (Collection) value) {
				printer.appendMember("[" + index++ + "]", element, null);
			}
		} else if (value instanceof Object[]) {
			int index = 0;
			for (Object element : (Object[]) value) {
				printer.appendMember("[" + index++ + "]", element, null);
			}
		} else {
			printer.appendString(value.getClass().getName());
			printer.appendString(" "); //$NON-NLS-1$
			printer.appendString(StringTools.safeString(value));
		}
		return true;
	}

	private final StringBuilder sb;

	private final Stack<Context> stack = new Stack<>(); // NOSONAR

	private int level;

	public PrettyPrinter() {
		this(new StringBuilder(), 0);
	}

	public PrettyPrinter(PrettyPrinter parent) {
		this(parent.getSb(), parent.getLevel());
	}

	public PrettyPrinter(StringBuilder sb, int level) {
		super();
		this.sb = sb;
		this.level = level;
	}

	protected void appendIndent() {
		for (int i = 0; i < level; i++) {
			sb.append("   ");
		}
	}

	public void appendMember(String name, Object value, String valueLabel) {
		if (!stack.isEmpty()) {
			Context context = stack.peek();
			open(context);
		}
		appendIndent();
		sb.append(name);
		sb.append(" = ");
		if (valueLabel != null) {
			sb.append("<");
			sb.append(valueLabel);
			sb.append("> ");
		}
		appendValue(value);
		sb.append(NEWLINE);
	}

	public void appendMembers(String name, List list) {
		if (!stack.isEmpty()) {
			Context context = stack.peek();
			open(context);
		}
		appendIndent();
		sb.append(name);
		sb.append(" = ");
		appendValue(list);
		sb.append(NEWLINE);
	}

	public void appendString(String value) {
		sb.append(value);
	}

	public final void appendValue(Object value) {
		Context context = openLazy(value);
		try {
			ListIterator<Context> li = stack.listIterator(stack.size());
			boolean printed = false;
			while (li.hasPrevious()) {
				IPrettyPrinterPrimitive primitive = li.previous().primitive;
				if (primitive != null && primitive.toString(this, value)) {
					printed = true;
					break;
				}
			}
			if (!printed) {
				PrimitivePrinter.toString(this, value);
			}
		} catch (Exception e) {
			sb.append("<error>");
		} finally {
			close(context);
		}
	}

	public void close() {
		close(stack.peek());
	}

	protected void close(Context context) {
		if (context.open) {
			context.open = false;
			--level;
			appendIndent();
			sb.append("}");
		}
		stack.pop();
	}

	protected int getLevel() {
		return level;
	}

	protected StringBuilder getSb() {
		return sb;
	}

	protected void open(Context context) {
		if (context.open) {
			return;
		}
		context.open = true;
		sb.append("{");
		sb.append(NEWLINE);
		level++;
	}

	public void open(Object value) {
		Context context = openLazy(value);
		open(context);
	}

	public Context openLazy(Object value) {
		Context context = new Context(value);
		stack.push(context);
		return context;
	}

	/*
	 * Return the string built by this printer.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return getSb().toString();
	}

	/**
	 * Create a pretty printed version of value.
	 * 
	 * 
	 * @param value
	 * @return
	 */
	public final String toString(Object value) {
		appendValue(value);
		return sb.toString();
	}
}
