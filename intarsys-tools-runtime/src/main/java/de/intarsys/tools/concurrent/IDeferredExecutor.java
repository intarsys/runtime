package de.intarsys.tools.concurrent;

import java.util.concurrent.ExecutorService;

import de.intarsys.tools.servicelocator.ServiceImplementation;

/**
 * An executor that holds all commands until {@link #release()}.
 * 
 */
@ServiceImplementation(DeferredExecutor.class)
public interface IDeferredExecutor extends ExecutorService {

	/**
	 * Start submitting all retained runnables.
	 * 
	 */
	public void release();

}
