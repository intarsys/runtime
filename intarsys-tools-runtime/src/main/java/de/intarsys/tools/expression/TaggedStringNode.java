package de.intarsys.tools.expression;

/**
 * A "syntax node" in a tagged string.
 * 
 * This is either literal text or an embedded expression.
 * 
 */
public abstract class TaggedStringNode {

	public abstract Object toTemplate();

}
