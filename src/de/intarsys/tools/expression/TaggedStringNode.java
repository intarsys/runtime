package de.intarsys.tools.expression;

/**
 * A "syntax node" in a tagged string.
 * 
 * This is either literal text or an embedded expression.
 * 
 */
abstract public class TaggedStringNode {

	abstract public Object toTemplate();

}
