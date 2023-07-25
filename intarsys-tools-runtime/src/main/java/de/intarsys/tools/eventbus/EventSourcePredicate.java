package de.intarsys.tools.eventbus;

/**
 * A predicate for filtering source objects in the {@link IEventBus}.
 * 
 */
public interface EventSourcePredicate {

	public boolean accepts(Object source);

}
