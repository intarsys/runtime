package de.intarsys.tools.logging;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * A tool class that provides the mechanics for a thread context sensitive log
 * handler attached to a dedicated {@link Logger}. By default, the handler is
 * attached to the root logger.
 * 
 */
public class LogHandlerSwitcher {

	private Handler handler;

	private boolean handlerPerAttach = false;

	private IHandlerFactory handlerFactory;

	private String loggerName = ""; //$NON-NLS-1$

	final private Handler handlerAttached = new DelegatingHandler() {

		@Override
		public Handler getBaseHandler() {
			return handler;
		};

		@Override
		public java.util.logging.Filter getFilter() {
			return filter;
		};

		@Override
		public void publish(LogRecord record) {
			basicPublishAttached(record);
		}
	};

	final private Handler handlerDetached = new DelegatingHandler() {

		@Override
		public Handler getBaseHandler() {
			return handler;
		};

		@Override
		public void publish(LogRecord record) {
			basicPublishDetached(record);
		}
	};

	private ThreadFilter filter = new ThreadFilter(false);

	private int attachCounter = 0;

	/**
	 * Attach the handler to the current thread.
	 */
	public void attach() {
		getFilter().activate();
		if (attachCounter++ == 0) {
			if (isHandlerPerAttach() || handler == null) {
				switchLog();
			}
			Logger.getLogger(getLoggerName()).addHandler(getHandlerAttached());
		}
	}

	protected void basicPublishAttached(LogRecord record) {
		if (handler == null) {
			return;
		}
		if (getFilter().isActive()) {
			handler.publish(record);
		}
	}

	protected void basicPublishDetached(LogRecord record) {
		if (handler == null) {
			return;
		}
		if (!getFilter().isActive()) {
			handler.publish(record);
		}
	}

	protected Handler createLogHandler() throws IOException {
		if (handlerFactory == null) {
			return null;
		}
		return handlerFactory.createLogHandler();
	}

	public void destroy() {
		if (handler != null) {
			handler.close();
			handler = null;
		}
	}

	/**
	 * Detach the handler from the current thread.
	 */
	public void detach() {
		getFilter().deactivate();
		if (--attachCounter == 0) {
			Logger.getLogger(getLoggerName()).removeHandler(
					getHandlerAttached());
		}
	}

	protected ThreadFilter getFilter() {
		return filter;
	}

	public Handler getHandler() {
		return handler;
	}

	public Handler getHandlerAttached() {
		return handlerAttached;
	}

	public Handler getHandlerDetached() {
		return handlerDetached;
	}

	public IHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void init() throws IOException {
		if (!isHandlerPerAttach()) {
			switchLog();
		}
	}

	public boolean isHandlerPerAttach() {
		return handlerPerAttach;
	}

	public void setHandlerFactory(IHandlerFactory logHandlerFactory) {
		this.handlerFactory = logHandlerFactory;
	}

	public void setHandlerPerAttach(boolean logPerRequest) {
		this.handlerPerAttach = logPerRequest;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	protected void switchLog() {
		if (handler != null) {
			handler.close();
		}
		try {
			handler = createLogHandler();
		} catch (IOException e) {
			//
		}
	}

}
