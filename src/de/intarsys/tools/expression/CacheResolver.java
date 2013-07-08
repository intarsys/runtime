package de.intarsys.tools.expression;

import java.util.HashMap;
import java.util.Map;

import de.intarsys.tools.functor.IArgs;

/**
 * Cache previous evaluation results.
 * 
 * This is very useful if we evaluate a template for a series of inputs where
 * the template contains dynamic expressions like "system.millis" that should
 * stay constant for the evaluation series.
 * 
 */
public class CacheResolver implements IStringEvaluator {

	final private IStringEvaluator evaluator;

	final private Map<String, Object> cache = new HashMap<String, Object>();

	public CacheResolver(IStringEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
	}

	public void clear() {
		cache.clear();
	}

	@Override
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		Object result = cache.get(expression);
		if (result == null) {
			result = evaluator.evaluate(expression, args);
			cache.put(expression, result);
		}
		return result;
	}

}
