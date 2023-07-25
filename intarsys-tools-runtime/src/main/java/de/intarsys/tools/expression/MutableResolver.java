package de.intarsys.tools.expression;

import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.IAccessSupport;

/**
 * A delegating resolver, which allows to add new or override existing expressions via its {@link IAccessSupport}
 * interface as required by {@link ExpressionDefinition}. The evaluation of expressions added via {@link IAccessSupport}
 * supports path navigation.
 */
public class MutableResolver implements IStringEvaluator, IAccessSupport {
	private static final Object NOTHING = new Object();

	private IStringEvaluator delegate;
	private MapResolver values;

	public MutableResolver(IStringEvaluator delegate) {
		this.delegate = delegate;

		values = new MapResolver() {
			@Override
			protected IStringEvaluator createStringEvaluator(Object object) {
				// Do not try to evaluate NOTHING any further!
				return object == NOTHING
						? null
						: super.createStringEvaluator(object);
			}
		};
		values.setNotFoundResult(NOTHING);
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		Object result = values.evaluate(expression, args);
		return result == NOTHING
				? delegate.evaluate(expression, args)
				: result;
	}

	@Override
	public Object getValue(String name) {
		return values.get(name);
	}

	@Override
	public Object setValue(String name, Object value) {
		return values.put(name, value);
	}
}
