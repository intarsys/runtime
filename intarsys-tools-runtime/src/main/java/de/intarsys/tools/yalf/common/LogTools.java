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

import java.io.File;
import java.util.Collections;
import java.util.List;

import de.intarsys.tools.logging.CommonDumpObject;
import de.intarsys.tools.logging.IDumpObject;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.api.IHandler;
import de.intarsys.tools.yalf.api.ILogger;
import de.intarsys.tools.yalf.api.Yalf;
import de.intarsys.tools.yalf.handler.IFileHandler;

/**
 * YALF related tool methods.
 * 
 */
public class LogTools {

	private static IDumpObject DumpObject;

	static {
		DumpObject = new CommonDumpObject();
	}

	public static final String LOGGER_ROOT = ""; //$NON-NLS-1$

	public static final String KEY_CORR = "corr";

	public static void addCorrelation(String id) {
		if (StringTools.isEmpty(id)) {
			return;
		}
		String previous = Yalf.get().getMDC().get(KEY_CORR);
		String tag = createCorrelationTag(id);
		if (StringTools.isEmpty(previous)) {
			Yalf.get().getMDC().put(KEY_CORR, tag);
		} else if (!previous.contains(tag)) {
			Yalf.get().getMDC().put(KEY_CORR, previous + tag);
		}
	}

	protected static String createCorrelationTag(String id) {
		String tag = "(" + id + ")";
		return tag;
	}

	public static List<String> dumpObject(String prefix, Object object) {
		return dumpObject(prefix, object, DumpObject);
	}

	public static List<String> dumpObject(String prefix, Object object, IDumpObject details) {
		return DumpObject.dump(prefix, object, details);
	}

	/**
	 * Ensure MDC is clean.
	 */
	public static void endCorrelation() {
		Yalf.get().getMDC().remove(KEY_CORR);
	}

	/**
	 * Try to derive the "main" directory where the system is logging to or
	 * null. This is mapped to the directory of the first {@link IFileHandler}
	 * of the ROOT {@link ILogger}.
	 * 
	 * @return
	 */
	public static File getLogDirectory() {
		ILogger logger = Yalf.get().<IHandler> getLogger("");
		for (IHandler handler : logger.getHandlers()) {
			if (handler instanceof IFileHandler) {
				return ((IFileHandler) handler).getDirectory();
			}
		}
		return null;
	}

	/**
	 * Try to derive the most recent log files where the system is logging to or
	 * an empty list. This is mapped to the files used of the first
	 * {@link IFileHandler} of the ROOT {@link ILogger}.
	 * 
	 * @return
	 */
	public static List<File> getLogFiles() {
		ILogger logger = Yalf.get().<IHandler> getLogger("");
		for (IHandler handler : logger.getHandlers()) {
			if (handler instanceof IFileHandler) {
				return ((IFileHandler) handler).getFiles();
			}
		}
		return Collections.emptyList();
	}

	public static ILogger getLogger(Class clazz) {
		return Yalf.get().<IHandler> getLogger(toLoggerName(clazz));
	}

	public static ILogger getLogger(String name) {
		return Yalf.get().<IHandler> getLogger(name);
	}

	public static void removeCorrelation(String id) {
		if (StringTools.isEmpty(id)) {
			return;
		}
		String previous = Yalf.get().getMDC().get(KEY_CORR);
		if (!StringTools.isEmpty(previous)) {
			String tag = createCorrelationTag(id);
			String replace = previous.replace(tag, "");
			Yalf.get().getMDC().put(KEY_CORR, replace);
		}
	}

	public static String toLoggerName(Class clazz) {
		String result = clazz.getName();
		int index = result.lastIndexOf('.');
		if (index > -1) {
			result = result.substring(0, index);
		}
		return result;
	}

	private LogTools() {
	}

}
