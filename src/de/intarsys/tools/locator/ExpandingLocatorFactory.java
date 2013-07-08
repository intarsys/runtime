package de.intarsys.tools.locator;

import java.io.IOException;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.Args;

/**
 * A decorator that will expand the location before use.
 * 
 */
public class ExpandingLocatorFactory extends DelegatingLocatorFactory {

	final private IStringEvaluator templateEvaluator;

	public ExpandingLocatorFactory(ILocatorFactory factory,
			IStringEvaluator evaluator) {
		super(factory);
		this.templateEvaluator = evaluator;
	}

	@Override
	protected ILocator basicCreateLocator(String location) throws IOException {
		String expanded;
		try {
			expanded = (String) templateEvaluator.evaluate(location, Args.create());
		} catch (EvaluationException e) {
			expanded = location;
		}
		return super.basicCreateLocator(expanded);
	}

}
