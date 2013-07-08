package de.intarsys.tools.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IHandlerFactory} for {@link FileHandler} instances.
 * 
 */
public class FileHandlerFactory extends CommonHandlerFactory {

	private File file;

	private String fileName = "log.%u.%g.log";

	private int count = 5;

	private int limit = 1000000;

	private IStringEvaluator templateEvaluator;

	private boolean append = false;

	@Override
	protected Handler basicCreateHandler() throws IOException {
		File logFile = getFile();
		FileHandler tempHandler = new FileHandler(logFile.getAbsolutePath(),
				getLimit(), getCount(), isAppend());
		return tempHandler;
	}

	public int getCount() {
		return count;
	}

	public File getFile() {
		if (file != null) {
			return file;
		}
		String tempName = getFileName();
		if (StringTools.isEmpty(tempName)) {
			tempName = "log.%u.%g.log";
		}
		try {
			tempName = (String) getTemplateEvaluator().evaluate(tempName,
					Args.create());
		} catch (Exception e) {
			//
		}
		tempName = FileTools.trimPath(tempName);
		File parent = FileEnvironment.get().getProfileDir();
		File tempFile = FileTools.resolvePath(parent, tempName);
		if (tempFile.getParentFile() != null) {
			tempFile.getParentFile().mkdirs();
		}
		return tempFile;
	}

	public String getFileName() {
		return fileName;
	}

	public int getLimit() {
		return limit;
	}

	public IStringEvaluator getTemplateEvaluator() {
		if (templateEvaluator == null) {
			return TemplateEvaluator.get();
		}
		return templateEvaluator;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public void setCount(int fileHandlerCount) {
		this.count = fileHandlerCount;
		setSingletonHandler(null);
	}

	public void setFile(File fileHandlerFile) {
		this.file = fileHandlerFile;
		setSingletonHandler(null);
	}

	public void setFileName(String filename) {
		this.fileName = filename;
		setSingletonHandler(null);
	}

	public void setLimit(int fileHandlerLimit) {
		this.limit = fileHandlerLimit;
		setSingletonHandler(null);
	}

	public void setTemplateEvaluator(IStringEvaluator stringEvaluator) {
		this.templateEvaluator = stringEvaluator;
		setSingletonHandler(null);
	}
}
