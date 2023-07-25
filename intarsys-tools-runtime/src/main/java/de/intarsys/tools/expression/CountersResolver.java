package de.intarsys.tools.expression;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.intarsys.tools.functor.IArgs;

/**
 * Keeps track of zero-based counters, which are incremented by one each time they are evaluated. Note that the number
 * of counters is limited. If the number of counters exceeds the limit, then the value of the oldest counter is
 * discarded.
 */
public class CountersResolver implements IStringEvaluator {
	private static final int MAX_CACHE_SIZE = 5000;

	private Map<String, Integer> counters = Collections.synchronizedMap(new LinkedHashMap<>() {
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<String, Integer> eldest) {
			return size() > MAX_CACHE_SIZE;
		}
	});

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		return counters.compute(expression, (key, value) -> value == null ? 0 : value + 1);
	}
}
