package de.intarsys.tools.logging;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * Only accept log requests within a dedicated thread.
 * <p>
 * This is for example suitable in a scenario where a log should be associated
 * with the processing of a functor object but the functor is executed in
 * different worker threads. If the functor activates its filter upon entry, all
 * activities can be sent to the same log, even from different workers. In this
 * case the thread associated with the filter is switched with "activate" and
 * "deactivate". These methods should always be used in a "finally" style block.
 * <p>
 * Another scenario is where a thread has a dedicated task and all its
 * activities have to be logged. Here the thread is associated at startup and
 * remains the same.
 * 
 */
public class ThreadFilter implements Filter {

	private ThreadLocal<Integer> threadActivation = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return 0;
		};
	};

	public ThreadFilter(boolean activate) {
		super();
		if (activate) {
			activate();
		}
	}

	public boolean activate() {
		int tempActivation = threadActivation.get();
		threadActivation.set(tempActivation + 1);
		return tempActivation == 0;
	}

	public boolean deactivate() {
		int tempActivation = threadActivation.get();
		threadActivation.set(tempActivation - 1);
		return tempActivation == 1;
	}

	public boolean isActive() {
		return threadActivation.get() > 0;
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		return threadActivation.get() > 0;
	}

}
