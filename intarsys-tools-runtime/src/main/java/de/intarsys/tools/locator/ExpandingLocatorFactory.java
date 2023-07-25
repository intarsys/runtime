package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.StringEvaluatorTools;

/**
 * A decorator that will expand the location before use.
 * 
 */
public class ExpandingLocatorFactory extends DelegatingLocatorFactory {

	private final IStringEvaluator templateEvaluator;

	public ExpandingLocatorFactory(ILocatorFactory factory, IStringEvaluator evaluator) {
		super(factory);
		this.templateEvaluator = evaluator;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		String expanded = StringEvaluatorTools.evaluateString(templateEvaluator, location);
		return super.basicCreateLocator(expanded);
	}

}
