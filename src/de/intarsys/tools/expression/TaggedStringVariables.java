package de.intarsys.tools.expression;

import java.io.Serializable;
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
public class TaggedStringVariables implements Serializable, IStringEvaluator,
		Iterable<TaggedStringVariable> {

	final private Map<String, TaggedStringVariable> variables;

	public TaggedStringVariables() {
		variables = new HashMap<>();
	}

	public List<TaggedStringVariables> asList() {
		return new ArrayList(variables.values());
	}

	@Override
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		TaggedStringVariable var = variables.get(expression);
		if (var == null) {
			return null;
		}
		return var.getValue();
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
		TaggedStringVariable var = new TaggedStringVariable(expression, value);
		variables.put(expression, var);
		return var;
	}

}