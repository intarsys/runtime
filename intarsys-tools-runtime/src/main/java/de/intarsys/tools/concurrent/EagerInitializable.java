package de.intarsys.tools.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.annotation.PostConstruct;

import de.intarsys.tools.yalf.api.ILogger;

/**
 * A common superclass for implementing objects that have some eager initialization task to be done.
 * 
 * These objects are picked from the bean container after start and injected into an executor for
 * initialization.
 * 
 * @param <R>
 */
public abstract class EagerInitializable<R> {

	private static final ILogger Log = PACKAGE.Log;

	private Future<R> initializer;

	private final Object lock = new Object();

	protected FutureTask<R> createNullInitializer() {
		return new PrematureFutureTask<>(() -> null);
	}

	protected FutureTask<R> createSynchronousInitializer() {
		if (isInitializationRequired()) {
			return new PrematureFutureTask<>(() -> initializeValue());
		} else {
			return createNullInitializer();
		}
	}

	/**
	 * This method *ensures* that the initialization routine is triggered. If not already present,
	 * a corresponding task will be created and *run synchronously*.
	 * 
	 * Call this if the initialization is required before proper execution of the object.
	 * 
	 */
	protected void ensureInitialized() {
		synchronized (lock) {
			if (initializer == null) {
				initializer = createSynchronousInitializer();
			}
		}
		/*
		 * "get" out of synchronized section
		 */
		try {
			initializer.get();
			Log.debug("{} eager initialization finished", getClass().getSimpleName());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Log.debug("{} eager initialization interrupted", getClass().getSimpleName());
		} catch (ExecutionException e) {
			Log.warn("{} eager initialization failed", getClass().getSimpleName(), e);
		}
	}

	protected R getInitializedValue() throws InterruptedException, ExecutionException {
		ensureInitialized();
		return initializer.get();
	}

	@PostConstruct
	public void init() {
		initialize(EagerInitializer.get());
	}

	/**
	 * Request the initialization task in the executor.
	 * 
	 * It is safe to request initialization multiple times.
	 * 
	 * @param executorService
	 */
	public Future<R> initialize(ExecutorService executorService) {
		synchronized (lock) {
			if (initializer == null) {
				if (isEagerInitializable()) {
					initializer = executorService.submit(() -> initializeValue());
				} else {
					initializer = createSynchronousInitializer();
				}
			}
			return initializer;
		}
	}

	/**
	 * Overwrite this method with whatever is required to do the initialization task.
	 * 
	 * @return
	 */
	protected abstract R initializeValue();

	/**
	 * Redefine this method to determine at runtime if eager initialization is to be performed.
	 * 
	 * @return
	 */
	protected boolean isEagerInitializable() {
		return true;
	}

	/**
	 * Redefine this method to determine at runtime if initialization routine must be executed in any case before object
	 * can be used or if this only pure optimization and object will work anyway.
	 * 
	 * @return
	 */
	protected boolean isInitializationRequired() {
		return false;
	}

	/**
	 * This method *waits* that the initialization routine is finished if it was already triggered. If initialization
	 * was not already initiated by some component it is *omitted* completely.
	 * 
	 * Call this if the initialization is optional before proper usage of the object and you are not interested
	 * in the real outcome, only want to prevent double execution.
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * 
	 */
	protected void waitInitialized() {
		synchronized (lock) {
			if (initializer == null) {
				/*
				 * inhibit further initialization
				 */
				initializer = createNullInitializer();
			}
		}
		/*
		 * "get" out of synchronized section
		 */
		try {
			initializer.get();
			Log.debug("{} eager initialization finished", getClass().getSimpleName());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Log.debug("{} eager initialization interrupted", getClass().getSimpleName());
		} catch (ExecutionException e) {
			Log.warn("{} eager initialization failed", getClass().getSimpleName(), e);
		}
	}
}
