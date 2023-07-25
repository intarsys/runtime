/*
 * (c) intarsys GmbH
 * all rights reserved
 *
 */
package de.intarsys.tools.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Level;

/**
 * The concrete Toolkit implementation for a "headless" platform.
 */
public class SilentToolkit extends CommonToolkit {

	private static ILogger Log = PACKAGE.Log;

	private Thread workerThread;

	private final ExecutorService executorServiceWorker = Executors.newSingleThreadExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			workerThread = new Thread(r, "headless worker");
			// vm should shut down if only this thread available
			workerThread.setDaemon(true);
			workerThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					Log.log(Level.WARN, "uncaught exception in " + t, e);
				}
			});
			workerThread.setPriority(Thread.NORM_PRIORITY);
			return workerThread;
		}
	});

	@Override
	public void beep() {
		try {
			// try the awt toolkit flavor
			for (int i = 0; i < 4; i++) {
				java.awt.Toolkit.getDefaultToolkit().beep();
			}
		} catch (Throwable e) {
			// catch any exception created because of toolkit failure
		}
	}

	protected ExecutorService getExecutorServiceWorker() {
		return executorServiceWorker;
	}

	@Override
	public void invokeInUI(Runnable runnable) {
		invokeNow(runnable);
	}

	@Override
	public void invokeLater(Runnable runnable) {
		getExecutorServiceWorker().submit(runnable);
	}

	@Override
	public void invokeNow(Runnable runnable) {
		if (Thread.currentThread() == workerThread) {
			// avoid deadlocks when calling recursive
			runnable.run();
			return;
		}
		Future result = null;
		// requests are executed sequentially
		result = getExecutorServiceWorker().submit(runnable);
		// wait for the processing to be done
		ExceptionTools.futureSimpleGet(result);
	}

	@Override
	public void invokeUpdate(Runnable runnable) {
		invokeNow(runnable);
	}

	@Override
	public boolean isSilent() {
		return true;
	}

}
