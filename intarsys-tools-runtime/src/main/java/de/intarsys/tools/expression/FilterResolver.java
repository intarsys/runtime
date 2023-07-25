package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;

/**
 * An {@link IStringEvaluator} that hides another {@link IStringEvaluator} behind a filter namespace.
 * 
 * This can be used for a multi-phase expansion, where a string is expanded e.g. in two distinct environments with maybe
 * overlapping namespaces. Hiding the first environment behind a filter will help in separating the expansion process
 * exactly.
 */
public class FilterResolver extends ContainerResolver {

	final private String filter;

	final private IStringEvaluator guardedEvaluator;

	public FilterResolver(String filter, IStringEvaluator guardedEvaluator) {
		super();
		this.filter = filter;
		this.guardedEvaluator = guardedEvaluator;
	}

	@Override
	protected Object basicEvaluate(String expression, IArgs args) throws EvaluationException {
		if (expression.equals(getFilter())) {
			return guardedEvaluator;
		}
		return notFound(expression);
	}

	public String getFilter() {
		return filter;
	}

	public IStringEvaluator getGuardedEvaluator() {
		return guardedEvaluator;
	}

}
