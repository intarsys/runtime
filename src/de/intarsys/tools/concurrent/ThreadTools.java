package de.intarsys.tools.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tool methods for dealing with threads.
 * 
 */
public class ThreadTools {

	private static final Logger Log = Logger
			.getLogger("de.intarsys.tools.concurrent.ThreadTools");

	public static Thread newThread(Runnable r, String name) {
		return newThread(r, name, Thread.NORM_PRIORITY);
	}

	public static Thread newThread(Runnable r, String name, int priority) {
		Thread thread = new Thread(r, name);
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Log.log(Level.WARNING, "uncaught exception in " + t, e);
			}
		});
		thread.setPriority(priority);
		return thread;
	}

	public static Thread newThreadDaemon(Runnable r, String name) {
		return newThreadDaemon(r, name, Thread.NORM_PRIORITY);
	}

	public static Thread newThreadDaemon(Runnable r, String name, int priority) {
		Thread thread = new Thread(r, name);
		thread.setDaemon(true);
		thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				Log.log(Level.WARNING, "uncaught exception in " + t, e);
			}
		});
		thread.setPriority(priority);
		return thread;
	}

	public static ThreadFactory newThreadFactory(final String name) {
		return newThreadFactory(name, Thread.NORM_PRIORITY);
	}

	public static ThreadFactory newThreadFactory(final String name,
			final int priority) {
		return new ThreadFactory() {
			private int counter = 0;

			@Override
			public Thread newThread(Runnable r) {
				String tempName = name;
				if (counter != 0) {
					tempName = name + "-" + counter;
				}
				counter++;
				return ThreadTools.newThread(r, tempName, priority);
			}
		};
	}

	public static ThreadFactory newThreadFactoryDaemon(final String name) {
		return newThreadFactoryDaemon(name, Thread.NORM_PRIORITY);
	}

	public static ThreadFactory newThreadFactoryDaemon(final String name,
			final int priority) {
		return new ThreadFactory() {
			private int counter = 0;

			@Override
			public Thread newThread(Runnable r) {
				String tempName = name;
				if (counter != 0) {
					tempName = name + "-" + counter;
				}
				counter++;
				return ThreadTools.newThreadDaemon(r, tempName, priority);
			}
		};
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// don't care
		}
	}

}
