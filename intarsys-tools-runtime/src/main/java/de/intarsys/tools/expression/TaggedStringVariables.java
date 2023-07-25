package de.intarsys.tools.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.intarsys.tools.functor.IArgs;

/**
 * A collection of {@link TaggedStringVariable} instances.
 * 
 */
public class TaggedStringVariables implements IStringEvaluator, Iterable<TaggedStringVariable> {

	private final Map<String, TaggedStringVariable> variables;

	public TaggedStringVariables() {
		variables = new HashMap<>();
	}

	public List<TaggedStringVariables> asList() {
		return new ArrayList(variables.values());
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		TaggedStringVariable variable = variables.get(expression);
		return variable == null ? null : variable.getValue();
	}

	public TaggedStringVariable get(String expression) {
		return variables.get(expression);
	}

	public boolean isDefined(String expr) {
		return variables.get(expr) != null;
	}

	@Override
	public Iterator<TaggedStringVariable> iterator() {
		return variables.values().iterator();
	}

	public TaggedStringVariable put(String expression, Object value) {
		TaggedStringVariable variable = new TaggedStringVariable(expression, value);
		variables.put(expression, variable);
		return variable;
	}

}