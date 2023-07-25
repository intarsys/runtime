package de.intarsys.tools.concurrent;

import java.util.concurrent.Executor;

/**
 * Ensure some piece of code is executed before and after a {@link Runnable}.
 */
public interface IExecutionDecorator extends Executor {

	/**
	 * Execute command, sandwiched in {@link #executeBefore()} and {@link #executeAfter()}.
	 */
	@Override
	default void execute(Runnable command) {
		try {
			executeBefore();
			command.run();
		} finally {
			executeAfter();
		}
	}

	/**
	 * This code is to be executed after command is run.
	 * 
	 * Note that the default implementation ensures this cod to be executed even in case of failure of
	 * {@link #executeBefore()}.
	 */
	default void executeAfter() {
	}

	/**
	 * This code is to be executed before command is run.
	 * 
	 */
	default void executeBefore() {
	}

}
