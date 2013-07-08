package de.intarsys.tools.expression;

import java.io.Serializable;

/**
 * A concrete replacement value for a ${expression}
 * 
 */
public class TaggedStringVariable implements Serializable {

	final private String expression;

	private Object value;

	public TaggedStringVariable(String expr, Object value) {
		this.expression = expr;
		this.value = value;
	}

	public String getExpression() {
		return expression;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}