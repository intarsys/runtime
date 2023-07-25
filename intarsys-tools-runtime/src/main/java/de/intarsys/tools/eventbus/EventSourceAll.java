package de.intarsys.tools.eventbus;

/**
 * A predicate for accepting all source objects.
 * 
 */
public class EventSourceAll implements EventSourcePredicate {

	@Override
	public boolean accepts(Object source) {
		return true;
	}
}
