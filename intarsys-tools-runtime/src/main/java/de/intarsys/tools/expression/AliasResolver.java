package de.intarsys.tools.expression;

import java.util.Map;
import java.util.function.UnaryOperator;

import de.intarsys.tools.functor.IArgs;

/**
 * A delegating resolver that supports aliases for expressions and expression namespaces.
 */
public class AliasResolver implements IStringEvaluator {
	private IStringEvaluator delegate;
	private UnaryOperator<String> resolver;

	public AliasResolver(IStringEvaluator delegate, Map<String, String> aliases) {
		this(delegate, Map.copyOf(aliases)::get);
	}

	public AliasResolver(IStringEvaluator delegate, UnaryOperator<String> resolver) {
		this.delegate = delegate;
		this.resolver = resolver;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		String alias = resolveAliases(expression);
		if (alias == null) {
			return delegate.evaluate(expression, args);
		}

		try {
			return delegate.evaluate(alias, args);
		} catch (EvaluationException exception) {
			throw new EvaluationException(String.format("Could not evaluate alias: %s -> %s", expression, alias),
					exception);
		}
	}

	private String resolveAliases(String expression) {
		for (int headLength = expression.length(); headLength > 0; headLength = expression.lastIndexOf('.', headLength - 1)) {
			String head = expression.substring(0, headLength);
			String alias = resolver.apply(head);
			// The first condition handles Map::get and similar lookups. The second condition handles Aliases::resolve,
			// which returns the name, if there is no such alias defined.
			if (alias != null && !alias.equals(head)) {
				return alias + expression.substring(headLength);
			}
		}

		return null;
	}
}
