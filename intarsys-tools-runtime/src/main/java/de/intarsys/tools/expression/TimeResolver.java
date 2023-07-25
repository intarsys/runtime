package de.intarsys.tools.expression;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import de.intarsys.tools.functor.IArgs;

/**
 * Resolves the following time-related expressions:
 *
 * <ul>
 *   <li>now (current instant of the system clock; string representation will be ISO)
 *   <li>millis (current time in milliseconds)
 *   <li>uniquemillis (current time in milliseconds; each evaluation is guaranteed to yield a different value)
 * </ul>
 */
public class TimeResolver implements IStringEvaluator {
	private AtomicLong lastUniqueMillis = new AtomicLong();

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if (expression == null) {
			throw new EvaluationException("expression is null"); //$NON-NLS-1$
		}

		switch (expression) {
		case "now": //$NON-NLS-1$
			return Instant.now();

		case "millis": //$NON-NLS-1$
			return System.currentTimeMillis();

		case "uniquemillis": //$NON-NLS-1$
			return lastUniqueMillis.updateAndGet(oldMillis -> {
				long newMillis;
				do {
					newMillis = System.currentTimeMillis();
				} while (newMillis == oldMillis);

				return newMillis;
			});

		default:
			throw new EvaluationException(String.format("can't evaluate '%s'", expression)); //$NON-NLS-1$
		}
	}
}
