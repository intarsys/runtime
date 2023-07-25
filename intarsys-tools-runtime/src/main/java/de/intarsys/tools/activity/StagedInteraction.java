package de.intarsys.tools.activity;

import java.util.concurrent.CompletionStage;

import de.intarsys.tools.concurrent.ICompletable;

public class StagedInteraction<M, R> implements IInteraction<M, R> {

	public static <M, R> IInteraction<M, R> wrap(IInteraction<M, R> interaction, CompletionStage<R> stage) {
		return new StagedInteraction(interaction, stage);
	}

	private final CompletionStage<R> stage;

	private final IInteraction<M, R> wrapped;

	public StagedInteraction(IInteraction<M, R> wrapped, CompletionStage<R> stage) {
		super();
		this.wrapped = wrapped;
		this.stage = stage;
	}

	@Override
	public ICompletable<R> getCompletable() {
		return wrapped.getCompletable();
	}

	@Override
	public M getModel() {
		return wrapped.getModel();
	}

	@Override
	public CompletionStage<R> getStage() {
		return stage;
	}

}
