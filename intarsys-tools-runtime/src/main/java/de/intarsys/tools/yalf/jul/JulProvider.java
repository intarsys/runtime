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

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.yalf.api.IHandler;
import de.intarsys.tools.yalf.api.IHandlerFactory;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.IMDC;
import de.intarsys.tools.yalf.api.IYalfProvider;
import de.intarsys.tools.yalf.api.Level;
import de.intarsys.tools.yalf.common.MDC;
import de.intarsys.tools.yalf.handler.IFileHandler;
import de.intarsys.tools.yalf.handler.IMemoryHandler;
import de.intarsys.tools.yalf.handler.NullHandlerFactory;

public class JulProvider implements IYalfProvider<LogRecord> {

	public static final java.util.logging.Level toLevelJul(de.intarsys.tools.yalf.api.Level level) {
		if (level == null) {
			return null;
		}
		if (level == de.intarsys.tools.yalf.api.Level.SEVERE) {
			return java.util.logging.Level.SEVERE;
		}
		if (level == de.intarsys.tools.yalf.api.Level.WARN) {
			return java.util.logging.Level.WARNING;
		}
		if (level == de.intarsys.tools.yalf.api.Level.INFO) {
			return java.util.logging.Level.INFO;
		}
		if (level == Level.DEBUG) {
			return java.util.logging.Level.FINE;
		}
		if (level == Level.TRACE) {
			return java.util.logging.Level.FINEST;
		}
		return java.util.logging.Level.INFO;
	}

	public static final de.intarsys.tools.yalf.api.Level toLevelYalf(java.util.logging.Level level) {
		if (level == java.util.logging.Level.SEVERE) {
			return de.intarsys.tools.yalf.api.Level.SEVERE;
		}
		if (level == java.util.logging.Level.WARNING) {
			return de.intarsys.tools.yalf.api.Level.WARN;
		}
		if (level == java.util.logging.Level.INFO) {
			return de.intarsys.tools.yalf.api.Level.INFO;
		}
		if (level == java.util.logging.Level.FINE) {
			return Level.DEBUG;
		}
		if (level == java.util.logging.Level.FINER) {
			return Level.DEBUG;
		}
		if (level == java.util.logging.Level.FINEST) {
			return Level.TRACE;
		}
		return de.intarsys.tools.yalf.api.Level.INFO;
	}

	private final MDC mdc = new MDC();

	@Override
	public void configure(ILocator locator) {
		InputStream is = null;
		try {
			is = locator.getInputStream();
			if (is != null) {
				LogManager.getLogManager().readConfiguration(is);
			}
		} catch (Exception e) {
			//
		} finally {
			StreamTools.close(is);
		}
		// hack for java.util.logging reconfiguration bug, force level init
		try {
			is = locator.getInputStream();
			if (is != null) {
				Properties p = new Properties();
				p.load(is);
				for (Map.Entry entry : p.entrySet()) {
					String key = (String) entry.getKey();
					if (key.endsWith(".level")) {
						String logger = key.substring(0, key.length() - 6);
						// just touch logger
						Logger.getLogger(logger);
					}
				}
			}
		} catch (Exception e) {
			//
		} finally {
			StreamTools.close(is);
		}
	}

	@Override
	public String getDefaultConfigurationName() {
		return "logging.properties";
	}

	@Override
	public <H extends IHandler<LogRecord>> IHandlerFactory<LogRecord, H> getFactory(Class<H> clazz) {
		if (IFileHandler.class.equals(clazz)) {
			return (IHandlerFactory<LogRecord, H>) new JulFileHandlerFactory();
		}
		if (IMemoryHandler.class.equals(clazz)) {
			return (IHandlerFactory<LogRecord, H>) new JulMemoryHandlerFactory();
		}
		return new NullHandlerFactory();
	}

	@Override
	public ILogger getLogger(String name) {
		if ("ROOT".equals(name)) {
			name = "";
		}
		Logger logger = Logger.getLogger(name);
		return new JulLogger(logger);
	}

	@Override
	public IMDC getMDC() {
		return mdc;
	}

	@Override
	public boolean isConfigured() {
		String configFileName = System.getProperty("java.util.logging.config.file");
		if (configFileName == null) {
			// may not be configured
			return false;
		}
		File configFile = new File(configFileName);
		return configFile.exists() && configFile.isFile();
	}
}
