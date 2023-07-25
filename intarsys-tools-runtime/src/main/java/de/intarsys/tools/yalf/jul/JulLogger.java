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
package de.intarsys.tools.yalf.jul;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.intarsys.tools.format.Format;
import de.intarsys.tools.format.LogTuple;
import de.intarsys.tools.yalf.api.IHandler;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.CommonHandler;
import de.intarsys.tools.yalf.common.CommonLogger;

public class JulLogger extends CommonLogger {

	class HandlerAdapter extends Handler {

		private final IHandler handler;

		HandlerAdapter(IHandler handler) {
			super();
			this.handler = handler;
		}

		@Override
		public void close() throws SecurityException {
			handler.close();
		}

		@Override
		public void flush() {
			handler.flush();
		}

		protected Object getHandler() {
			return handler;
		}

		@Override
		public void publish(LogRecord event) {
			handler.publish(event);
		}

	}

	private Logger logger;

	public JulLogger(Logger logger) {
		super();
		this.logger = logger;
	}

	@Override
	public void addHandler(IHandler handler) {
		if (handler instanceof CommonHandler) {
			Handler implementation = (Handler) ((CommonHandler) handler).getImplementation();
			if (implementation == null) {
				implementation = new HandlerAdapter(handler);
				((CommonHandler) handler).setImplementation(implementation);
			}
			logger.addHandler(implementation);
		} else {
			throw new IllegalArgumentException("CommonHandler expected");
		}
	}

	@Override
	public void debug(String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(java.util.logging.Level.FINE, ft.getMessage(), ft.getThrowable());
	}

	@Override
	public List<IHandler> getHandlers() {
		List<IHandler> handlers = new ArrayList<>();
		for (Handler handler : logger.getHandlers()) {
			if (handler instanceof FileHandler) {
				handlers.add(new JulFileHandler((FileHandler) handler));
			} else {
				handlers.add(new JulOtherHandler(handler));
			}
		}
		return handlers;
	}

	@Override
	public Level getLevel() {
		return JulProvider.toLevelYalf(logger.getLevel());
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public void info(String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(java.util.logging.Level.INFO, ft.getMessage(), ft.getThrowable());
	}

	@Override
	public boolean isLoggable(Level level) {
		java.util.logging.Level julLevel = JulProvider.toLevelJul(level);
		return logger.isLoggable(julLevel);
	}

	@Override
	public void log(Level level, String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(JulProvider.toLevelJul(level), ft.getMessage(), ft.getThrowable());
	}

	@Override
	public void log(Object event) {
		logger.log((LogRecord) event);
	}

	@Override
	public void removeHandler(IHandler handler) {
		if (handler instanceof CommonHandler) {
			logger.removeHandler((Handler) ((CommonHandler) handler).getImplementation());
		} else {
			throw new IllegalArgumentException("CommonHandler expected");
		}
	}

	@Override
	public void setLevel(Level level) {
		java.util.logging.Level julLevel = JulProvider.toLevelJul(level);
		logger.setLevel(julLevel);
		for (Handler handler : logger.getHandlers()) {
			handler.setLevel(java.util.logging.Level.ALL);
		}
	}

	@Override
	public void severe(String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(java.util.logging.Level.SEVERE, ft.getMessage(), ft.getThrowable());
	}

	@Override
	public void trace(String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(java.util.logging.Level.FINEST, ft.getMessage(), ft.getThrowable());
	}

	@Override
	public void warn(String pattern, Object... arg) {
		LogTuple ft = Format.log(pattern, arg);
		logger.log(java.util.logging.Level.WARNING, ft.getMessage(), ft.getThrowable());
	}

}
