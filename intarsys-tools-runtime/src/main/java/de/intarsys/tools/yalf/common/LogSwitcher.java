/*
 * Copyright (c) 2014, intarsys GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.yalf.common;

import java.io.IOException;

import de.intarsys.tools.yalf.api.IHandler;
import de.intarsys.tools.yalf.api.IHandlerFactory;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.handler.DelegatingHandler;

/**
 * A tool class that provides the mechanics for a thread context sensitive log
 * handler attached to a dedicated logger. By default, the handler is attached
 * to the root logger.
 * 
 * {@link LogSwitcher} handles the logic of adding/removing an {@link IHandler}
 * to the {@link ILogger}.
 * 
 * <pre>
 * "attach" will add the {@link IHandler} we control to the {@link ILogger}
 * "detach" will remove the {@link IHandler} we control from the {@link ILogger}
 * </pre>
 */
public class LogSwitcher {

	private IHandler handler;

	private boolean handlerPerAttach;

	private IHandlerFactory handlerFactory;

	private String loggerName = ""; //$NON-NLS-1$

	private final IHandler handlerAttached = new DelegatingHandler() {
		@Override
		protected void basicPublish(Object event) {
			if (getHandler() == null) {
				return;
			}
			if (LogSwitcher.this.getFilter().isLoggable(event)) {
				getHandler().publish(event);
			}
		}

		@Override
		public IHandler getBaseHandler() {
			return getHandler();
		}
	};

	private final IHandler handlerDetached = new DelegatingHandler() {
		@Override
		protected void basicPublish(Object event) {
			if (getHandler() == null) {
				return;
			}
			if (!LogSwitcher.this.getFilter().isLoggable(event)) {
				getHandler().publish(event);
			}
		}

		@Override
		public IHandler getBaseHandler() {
			return getHandler();
		}
	};

	private final ThreadFilter filter = new ThreadFilter(false);

	private int attachCounter;

	/**
	 * Attach my {@link IHandler} to the {@link ILogger}, accepting log events
	 * from the current thread.
	 */
	public void attach() {
		synchronized (this) {
			// attachCounter is a global counter for all thread attachments
			getFilter().activate();
			if (attachCounter++ == 0) {
				if (isHandlerPerAttach()) {
					close();
				}
				// lazy open
				try {
					open();
				} catch (IOException e) {
					//
				}
				LogTools.getLogger(getLoggerName()).addHandler(getHandlerAttached());
			}
		}
	}

	/**
	 * Close the {@link IHandler} if available.
	 * 
	 * This method may not interfere with attach/detach semantics. Its solely
	 * purpose is to free resources on the handler in order to potentially
	 * switch them.
	 */
	public void close() {
		synchronized (this) {
			if (handler != null) {
				handler.close();
				handler = null;
			}
		}
	}

	protected IHandler createHandler() throws IOException {
		if (handlerFactory == null) {
			return null;
		}
		return handlerFactory.createHandler();
	}

	/**
	 * Detach my {@link IHandler} from the {@link ILogger}, no longer accepting
	 * log events from the current thread.
	 */
	public void detach() {
		synchronized (this) {
			getFilter().deactivate();
			if (--attachCounter == 0) {
				LogTools.getLogger(getLoggerName()).removeHandler(getHandlerAttached());
			}
		}
	}

	protected ThreadFilter getFilter() {
		return filter;
	}

	/**
	 * This is the plain handler that is created by our {@link IHandlerFactory}.
	 * 
	 * @return
	 */
	public IHandler getHandler() {
		return handler;
	}

	/**
	 * This is a special {@link IHandler} that is configured only to log records
	 * that are issued from the thread that called "attach". This one is
	 * attached to our {@link ILogger}.
	 * 
	 * @return
	 */
	public IHandler getHandlerAttached() {
		return handlerAttached;
	}

	/**
	 * This is a special {@link IHandler} that is configured only to log records
	 * to our {@link IHandler} that are issued from any thread that is not
	 * attached.
	 * 
	 * You can use this one to attach to a dedicated logger that should always
	 * log his events to the {@link IHandler}. If you did use the plain basic
	 * "getHandler", some events may get logged twice if the "attached" logger
	 * is added to a {@link ILogger} instance way up in the heirarchy, e.g. the
	 * ROOT logger.
	 * 
	 * @return
	 */
	public IHandler getHandlerDetached() {
		return handlerDetached;
	}

	/**
	 * The {@link IHandlerFactory} that will be used to create (initially or
	 * upon each attach) a new {@link IHandler}.
	 * 
	 * @return
	 */
	public IHandlerFactory getHandlerFactory() {
		return handlerFactory;
	}

	/**
	 * The name of the {@link ILogger} we want to add our {@link IHandler} to.
	 * 
	 * @return
	 */
	public String getLoggerName() {
		return loggerName;
	}

	public boolean isHandlerPerAttach() {
		return handlerPerAttach;
	}

	public boolean open() throws IOException {
		synchronized (this) {
			if (handler != null) {
				return false;
			}
			handler = createHandler();
			return true;
		}
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

}
