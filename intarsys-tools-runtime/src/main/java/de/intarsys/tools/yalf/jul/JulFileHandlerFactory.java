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
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.StringEvaluatorTools;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.logging.jul.SimpleFormatter;
import de.intarsys.tools.string.StringTools;
import de.intarsys.tools.yalf.handler.IFileHandlerFactory;

public class JulFileHandlerFactory extends JulHandlerFactory<JulFileHandler>
		implements IFileHandlerFactory<LogRecord, JulFileHandler> {

	public static final String DEFAULT_FILENAME = "log.%u.%g.log"; //$NON-NLS-1$

	private String filename = DEFAULT_FILENAME;

	private int limit = 1000000;

	private int count = 5;

	private boolean append;

	private boolean useProfileDir = true;

	private String encoding;

	@Override
	protected JulFileHandler basicCreateHandler() throws IOException {
		FileHandler handler = new FileHandler(createFile().getPath(), getLimit(), getCount(), isAppend());
		if (!StringTools.isEmpty(getEncoding())) {
			handler.setEncoding(getEncoding());
		}
		handler.setLevel(Level.ALL);
		SimpleFormatter formatter = SimpleFormatter.parse(getPattern());
		if (formatter != null) {
			handler.setFormatter(formatter);
		}
		return new JulFileHandler(handler);
	}

	protected File createFile() throws IOException {
		String tempName = getFilename();
		if (StringTools.isEmpty(tempName)) {
			tempName = DEFAULT_FILENAME;
		}
		tempName = StringEvaluatorTools.evaluateString(getTemplateEvaluator(), tempName);
		if (tempName.startsWith("%h")) {
			/*
			 * we could return here because this will resolve to an absolute
			 * path in JUL file handler, but we want the mkdirs() below. Let's
			 * do the replacement ourselves
			 */
			tempName = tempName.replace("%h", System.getProperty("user.home"));
		}
		tempName = FileTools.trimPath(tempName);
		File parent = isUseProfileDir() ? FileEnvironment.get().getProfileDir() : FileEnvironment.get().getBaseDir();
		File tempFile = FileTools.resolvePath(parent, tempName);
		if (tempFile.getParentFile() != null) {
			FileTools.mkdirs(tempFile.getParentFile());
		}
		return tempFile;
	}

	@Override
	public int getCount() {
		return count;
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public int getLimit() {
		return limit;
	}

	public boolean isAppend() {
		return append;
	}

	@Override
	public boolean isUseProfileDir() {
		return useProfileDir;
	}

	@Override
	public void setAppend(boolean append) {
		this.append = append;
	}

	@Override
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void setFilename(String filename) {
		// Java Plugin later 1.7.51 will filter params holding % !!
		filename = filename.replace("${u}", "%u");
		filename = filename.replace("${g}", "%g");
		filename = filename.replace("${h}", "%h");
		this.filename = filename;
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public void setUseProfileDir(boolean useProfileDir) {
		this.useProfileDir = useProfileDir;
	}

}
