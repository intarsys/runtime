/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.dom;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.intarsys.tools.stream.StreamTools;

public class LoggingErrorHandler implements ErrorHandler {

	private final Logger log;

	private static int filesCounter = 1;
	private int fileNumber = -1;
	private InputStream fileInputStream;

	public LoggingErrorHandler() {
		this(null);
	}

	public LoggingErrorHandler(Logger pLog) {
		log = pLog == null ? PACKAGE.Log : pLog;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		log.log(Level.SEVERE, e.getLocalizedMessage());
		logFile();
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		log.log(Level.SEVERE, e.getLocalizedMessage());
		logFile();
	}

	private String getFileContent() {
		try {
			return StreamTools.toString(getFileInputStream(), "UTF-8"); //$NON-NLS-1$
		} catch (IOException e) {
			return e.getLocalizedMessage();
		}
	}

	public Level getFileContentLogLevel() {
		return Level.FINE;
	}

	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	@SuppressWarnings("nls")
	private void logFile() {
		if (log.isLoggable(getFileContentLogLevel()) && fileInputStream != null) {
			if (fileNumber != -1) {
				log.log(getFileContentLogLevel(), "file number #" + fileNumber
						+ " was logged above");
			} else {
				fileNumber = filesCounter++;
				log.log(getFileContentLogLevel(), "file number #" + fileNumber
						+ ", related to the error above:\n" + getFileContent());
			}
		}
	}

	public void setFileInputStream(InputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		log.log(Level.WARNING, e.getLocalizedMessage());
	}
}