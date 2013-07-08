package de.intarsys.tools.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.intarsys.tools.attachment.Attachment;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.LocatorTools;
import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.string.StringTools;

/**
 * A java logging {@link Handler} that checks for parameters that come along
 * with the log record that should be "dumped" in a directory.
 * 
 * Simple set up the {@link FileDumpHandler} and write code like
 * 
 * <pre>
 * ...
 * Log.log(Level.INFO, "input data", myFile);
 * ...
 * </pre>
 * 
 * This result in "myFile" content copied to the file dump directory.
 * <p>
 * This implementation supports parameters of type
 * <ul>
 * <li> {@link ILocator}</li>
 * <li> {@link ILocatorSupport}</li>
 * <li> {@link File}</li>
 * <li> {@link InputStream}</li> The input stream is closed!
 * <li>byte[]</li>
 * </ul>
 * 
 */
public class FileDumpHandler extends Handler {

	final private static Logger Log = LogTools.getLogger(FileDumpHandler.class);

	static public void dump(File logDir, Object target) throws IOException,
			FileNotFoundException {
		FileTools.checkDirectory(logDir, true, true, true);
		if (target instanceof Attachment) {
			target = ((Attachment) target).getAttached();
		}
		if (target instanceof ILocator) {
			LocatorTools.save((ILocator) target, logDir);
		} else if (target instanceof ILocatorSupport) {
			LocatorTools.save(((ILocatorSupport) target).getLocator(), logDir);
		} else if (target instanceof File) {
			FileTools.copyFile((File) target, logDir);
		} else if (target instanceof InputStream) {
			try {
				File tempFile = File.createTempFile("dump", ".bytes", logDir);
				OutputStream os = new FileOutputStream(tempFile);
				StreamTools.copyStream((InputStream) target, false, os, true);
			} finally {
				StreamTools.close((InputStream) target);
			}
		} else if (target instanceof byte[]) {
			File tempFile = File.createTempFile("dump", ".bytes", logDir);
			OutputStream os = null;
			try {
				os = new FileOutputStream(tempFile);
				os.write((byte[]) target);
			} finally {
				StreamTools.close(os);
			}
		} else {
			// ?
		}
	}

	private File directory;

	public FileDumpHandler() {
		init();
	}

	@Override
	public void close() throws SecurityException {
	}

	protected void dumpFile(Object target) throws IOException {
		File logDir = getDirectory();
		dump(logDir, target);
	}

	@Override
	public void flush() {
	}

	public File getDirectory() {
		return directory;
	}

	private void init() {
		LogManager manager = LogManager.getLogManager();
		String cname = getClass().getName();
		String tempValue;
		tempValue = manager.getProperty(cname + ".directory");
		if (StringTools.isEmpty(tempValue)) {
			tempValue = "${environment.profiledir}/dumplog.${system.uniquetime:d}";
			try {
				tempValue = (String) TemplateEvaluator.get().evaluate(
						tempValue, Args.create());
			} catch (EvaluationException ignore) {
				//
			}
			tempValue = FileTools.trimPath(tempValue);
			directory = new java.io.File(tempValue);
		}
		tempValue = manager.getProperty(cname + ".level");
		if (tempValue == null) {
			setLevel(Level.ALL);
		} else {
			try {
				setLevel(Level.parse(tempValue.trim()));
			} catch (Exception ex) {
				setLevel(Level.ALL);
			}
		}
	}

	@Override
	public void publish(LogRecord record) {
		Object[] parameters = record.getParameters();
		if (parameters != null && parameters.length > 0) {
			if (!isLoggable(record)) {
				return;
			}
			try {
				FileTools.checkDirectory(directory, true, true, true);
				for (Object target : parameters) {
					dumpFile(target);
				}
			} catch (IOException e) {
				//
			}
		}
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

}
