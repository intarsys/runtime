package de.intarsys.tools.file;

import java.io.File;

import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.functor.IArgs;

/**
 * Publish specific file properties.
 * 
 */
public class FilenameResolver implements IStringEvaluator {

	private final File file;

	private final String extensionPrefix;

	public FilenameResolver(File file) {
		super();
		this.file = file;
		this.extensionPrefix = null;
	}

	public FilenameResolver(File file, String extensionPrefix) {
		super();
		this.file = file;
		this.extensionPrefix = extensionPrefix;
	}

	@Override
	public Object evaluate(String expression, IArgs args) throws EvaluationException {
		if ("path".equals(expression)) {
			return getPathName();
		} else if ("basename".equals(expression)) {
			return getBaseName();
		} else if ("extension".equals(expression)) {
			return getExtension();
		} else if ("name".equals(expression)) {
			return getFileName();
		} else if ("filename".equals(expression)) {
			return getFileName();
		}
		throw new EvaluationException("can't evaluate '" + expression + "'");
	}

	public String getBaseName() {
		return PathTools.getBaseName(file.getName(), extensionPrefix, "");
	}

	public String getExtension() {
		return PathTools.getExtension(file.getName(), extensionPrefix, "");
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return FileTools.getFileName(file);
	}

	public String getFullName() {
		return file.getAbsolutePath();
	}

	public String getPathName() {
		return FileTools.getPathName(file);
	}

}
