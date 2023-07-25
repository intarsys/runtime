package de.intarsys.tools.activity;

import java.util.concurrent.CompletionStage;

import de.intarsys.tools.concurrent.ICompletable;

/**
 * This abstraction tries to model the decoupling of an interaction (most
 * probably via some fat or web UI) with an activity.
 * 
 * The idea is that an interaction is requested via some factory method (like
 * "enterMyInteraction"). The underlying activity does not have any assumptions
 * on the control of the new interaction - it only publishes
 * 
 * - a means to control the interaction lifecycle (via the {@link ICompletable})
 * - a means to interact with the interaction state (via the model) - a means to
 * couple interactions (via the {@link CompletionStage})
 * 
 * @param <M> The underlying model class (an {@link IActivity<R>} for example
 * @param <R> The underlying result class (the result of the
 *            {@link IActivity<R>}s
 */
public interface IInteraction<M, R> {

	ICompletable<R> getCompletable();

	M getModel();

	CompletionStage<R> getStage();

}
