package de.intarsys.tools.functor;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.intarsys.tools.functor.IArgs.IBinding;
import de.intarsys.tools.string.StringTools;

public class ArgsPrinter {

	private static final Object PRESENT = new Object();

	private Map<Object, Object> visited = new IdentityHashMap<>();

	private int nesting = 0;

	private final Set<String> hidden = new HashSet<>();

	public ArgsPrinter hide(String name) {
		hidden.add(name);
		return this;
	}

	protected void lineBegin(String prefix, StringBuilder sb) {
		sb.append(prefix);
		for (int i = 1; i < nesting; i++) {
			sb.append("   ");
		}
	}

	public String toPrintString(IArgs args, String prefix) {
		StringBuilder sb = new StringBuilder();
		lineBegin(prefix, sb);
		toPrintStringValueArgs(prefix, sb, args);
		return sb.toString();
	}

	protected void toPrintStringValue(String prefix, StringBuilder sb, Object value) {
		if (value instanceof IArgs) {
			toPrintStringValueArgs(prefix, sb, (IArgs) value);
		} else {
			toPrintStringValuePlain(sb, value);
		}
	}

	protected void toPrintStringValueArgs(String prefix, StringBuilder sb, IArgs args) {
		if (visited.get(args) != null) {
			sb.append("...recursive...");
			return;
		}
		if (nesting > 20) {
			sb.append("...nested to deep...");
			return;
		}
		visited.put(args, PRESENT);
		String tokenOpen;
		String tokenClose;
		boolean named = ArgTools.isNamed(args);
		if (named) {
			tokenOpen = "{";
			tokenClose = "}";
		} else {
			tokenOpen = "[";
			tokenClose = "]";
		}
		sb.append(tokenOpen);
		sb.append("\n");
		nesting++;
		try {
			for (Iterator<IBinding> it = args.bindings(); it.hasNext();) {
				IBinding binding = it.next();
				if (!binding.isDefined()) {
					continue;
				}
				Object value = binding.getValue();
				lineBegin(prefix, sb);
				if (named) {
					String name = binding.getName();
					if (hidden.contains(name)) {
						continue;
					}
					sb.append("\"");
					sb.append(name);
					sb.append("\"");
					sb.append(": ");
				}
				toPrintStringValue(prefix, sb, value);
				if (it.hasNext()) {
					sb.append(",");
				}
				sb.append("\n");
			}
		} finally {
			nesting--;
			lineBegin(prefix, sb);
			sb.append(tokenClose);
		}
	}

	protected void toPrintStringValuePlain(StringBuilder sb, Object value) {
		if (value != null) {
			sb.append("\"");
		}
		sb.append(StringTools.getLeading(StringTools.safeString(value), 10 * 1024));
		if (value != null) {
			sb.append("\"");
		}
	}

}
