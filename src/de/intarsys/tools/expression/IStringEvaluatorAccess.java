package de.intarsys.tools.expression;

/**
 * An object whose string expansion strategies can be customized.
 * 
 */
public interface IStringEvaluatorAccess extends IStringEvaluatorSupport {

	/**
	 * Assign an {@link IStringEvaluator} that manages string expansion within
	 * the receiver.
	 */
	public void setStringEvaluator(IStringEvaluator evaluator);

}
