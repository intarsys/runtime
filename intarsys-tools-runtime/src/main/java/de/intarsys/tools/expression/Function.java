package de.intarsys.tools.expression;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.collection.ListTools;

/**
 * A function representation in an expression.
 * 
 */
public class Function extends Expression {

	private final String name;

	private final List<Expression> args;

	public Function(String value, Expression... args) {
		super();
		this.name = value;
		this.args = ListTools.with(args);
	}

	public Function(String value, List<Expression> args) {
		super();
		this.name = value;
		this.args = new ArrayList<>(args);
	}

	@Override
	public String getCode() {
		boolean addSeparator = false;
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append("(");
		for (Expression arg : args) {
			if (addSeparator) {
				sb.append(",");
			}
			sb.append(arg.getCode());
			addSeparator = true;
		}
		sb.append(")");
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean isFunction() {
		return true;
	}
}
