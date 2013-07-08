/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.logging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.intarsys.tools.stream.StreamTools;

/**
 * Tool methods to ease life with java.util.logging.
 * 
 */
public class LogTools {

	private static IDumpObject DumpObject;

	public static final String INDENT = "    "; //$NON-NLS-1$

	static {
		DumpObject = new CommonDumpObject();
	}

	/**
	 * Closes a fileLogger.
	 */
	public static void closeFileLogger(Logger logger) {
		Handler[] handlers = logger.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].close();
		}
	}

	/**
	 * Creates a fileLogger.
	 * 
	 * @return A Logger named with 'id'.
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static Logger createFileLogger(String id, String filePattern,
			boolean append) throws SecurityException, IOException {
		Logger logger = Logger.getLogger(id);
		Handler handler = new FileHandler(filePattern, append);
		logger.addHandler(handler);
		return logger;
	}

	/**
	 * This method tries to create a dump of all active log files. For this to
	 * work you need a {@link FileDumpHandler} attached to the root logger.
	 */
	static public void dumpLogFiles() {
		File[] files = LogTools.getFilesThreadLocal();
		for (File file : files) {
			if (!file.exists()) {
				continue;
			}
			Logger.getLogger("").log(Level.WARNING,
					"dump " + file.getAbsolutePath(), file);
		}
	}

	static public List<String> dumpObject(String prefix, Object object) {
		return dumpObject(prefix, object, DumpObject);
	}

	static public List<String> dumpObject(String prefix, Object object,
			IDumpObject details) {
		return DumpObject.dump(prefix, object, details);
	}

	public static File[] getFiles(Handler handler) {
		try {
			handler.flush();
			Field field = FileHandler.class.getDeclaredField("files");
			field.setAccessible(true);
			return (File[]) field.get(handler);
		} catch (Exception e) {
			return new File[0];
		}
	}

	static public File[] getFilesThreadLocal() {
		List<File> files = new ArrayList<File>();
		Logger logger = Logger.getLogger("");
		Handler[] handlers = logger.getHandlers();
		getFilesThreadLocal(handlers, files);
		return files.toArray(new File[files.size()]);
	}

	protected static void getFilesThreadLocal(Handler handler, List<File> files) {
		if (handler == null) {
			return;
		}
		Filter filter = handler.getFilter();
		if (filter instanceof ThreadFilter) {
			if (!((ThreadFilter) filter).isActive()) {
				return;
			}
		}
		if (handler instanceof FileHandler) {
			files.addAll(Arrays.asList(getFiles(handler)));
		} else if (handler instanceof CompositeHandler) {
			getFilesThreadLocal(((CompositeHandler) handler).getHandlers(),
					files);
		} else if (handler instanceof DelegatingHandler) {
			getFilesThreadLocal(((DelegatingHandler) handler).getBaseHandler(),
					files);
		} else {
			//
		}
	}

	protected static void getFilesThreadLocal(Handler[] handlers,
			List<File> files) {
		for (Handler handler : handlers) {
			getFilesThreadLocal(handler, files);
		}
	}

	/**
	 * Get a logger that corresponds to <code>clazz</code>.
	 * 
	 * @param clazz
	 *            The class object we want a logger for.
	 * 
	 * @return A Logger that corresponds to clazz.
	 */
	public static Logger getLogger(Class<?> clazz) {
		Logger logger = Logger.getLogger(toLoggerName(clazz));
		LogConfigurator.get().configure(logger);
		return logger;
	}

	/**
	 * Get a logger for name <code>name</code>.
	 * 
	 * @param name
	 *            The logger name/category
	 * 
	 * @return Get a logger for name <code>name</code>.
	 */
	public static Logger getLogger(String name) {
		Logger logger = Logger.getLogger(name);
		LogConfigurator.get().configure(logger);
		return logger;
	}

	static public void reconfigure(ClassLoader classloader, String resource) {
		InputStream is = null;
		try {
			is = classloader.getResourceAsStream(resource);
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
			is = classloader.getResourceAsStream(resource);
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

	protected static void setFileHandlerLevel(Handler handler, Level newLevel) {
		if (handler instanceof FileHandler
				|| handler instanceof de.intarsys.tools.logging.FileHandler) {
			handler.setLevel(newLevel);
		} else if (handler instanceof CompositeHandler) {
			setFileHandlerLevel(((CompositeHandler) handler).getHandlers(),
					newLevel);
		} else if (handler instanceof DelegatingHandler) {
			setFileHandlerLevel(((DelegatingHandler) handler).getBaseHandler(),
					newLevel);
		} else {
			//
		}
	}

	protected static void setFileHandlerLevel(Handler[] handlers, Level newLevel) {
		for (Handler handler : handlers) {
			setFileHandlerLevel(handler, newLevel);
		}
	}

	static public void setFileHandlerLevel(Logger logger, Level newLevel) {
		logger.setLevel(newLevel);
		Handler[] handlers = logger.getHandlers();
		setFileHandlerLevel(handlers, newLevel);
	}

	/**
	 * The name we want to use for a logger used in <code>clazz</code>. This
	 * implementation uses the package name.
	 * 
	 * @param clazz
	 *            The class object we want a name for.
	 * 
	 * @return The name we want to use for a logger used in <code>clazz</code>.
	 */
	public static String toLoggerName(Class<?> clazz) {
		String result = clazz.getName();
		int index = result.lastIndexOf('.');
		if (index > -1) {
			result = result.substring(0, index);
		}
		return result;
	}

	/**
	 * 
	 */
	private LogTools() {
		// tool class
	}

}
