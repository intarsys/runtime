package de.intarsys.tools.concurrent;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import de.intarsys.tools.component.SingletonProvider;
import de.intarsys.tools.servicelocator.ServiceLocator;
import de.intarsys.tools.yalf.api.IMDC;
import de.intarsys.tools.yalf.api.Yalf;

@SingletonProvider
public class ExecutorEnvironment {

	static {
		Runtime.getRuntime().addShutdownHook(new Thread("ExecutorEnvironment shutdown") {
			@Override
			public void run() {
				get().destroy();
			}
		});
	}

	public static synchronized ExecutorEnvironment get() {
		return ServiceLocator.get().get(ExecutorEnvironment.class);
	}

	private static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
		LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
		/*
		 * would have liked to use ScheduledThreadPoolExcecutor, so we can extract and
		 * reuse the class, but that doesn't have those constructor parameters
		 */
		return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, workQueue, threadFactory) {

			@Override
			public void execute(Runnable command) {
				Map<String, String> contextMap = Yalf.get().getMDC().getCopyOfContextMap();
				super.execute(() -> {
					IMDC mdc = Yalf.get().getMDC();
					Map<String, String> previousContextMap = mdc.getCopyOfContextMap();
					if (contextMap == null) {
						mdc.clear();
					} else {
						mdc.setContextMap(contextMap);
					}
					try {
						command.run();
					} finally {
						if (previousContextMap == null) {
							mdc.clear();
						} else {
							mdc.setContextMap(previousContextMap);
						}
					}
				});
			}
		};
	}

	private ExecutorService service;

	protected ExecutorService createService() {
		return newFixedThreadPool(1, ThreadTools.newThreadFactoryDaemon("ExecutorEnvironment service"));
	}

	@PreDestroy
	public void destroy() {
		if (service != null) {
			service.shutdownNow();
		}
	}

	public synchronized ExecutorService getService() {
		if (service == null) {
			service = createService();
		}
		return service;
	}

	public synchronized void setService(ExecutorService service) {
		this.service = service;
	}
}
