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
	public Object evaluate(String expression, IArgs args)
			throws EvaluationException {
		if ("path".equals(expression)) {
			return FileTools.getPathName(file);
		} else if ("basename".equals(expression)) {
			return FileTools.getBaseName(file.getName(), extensionPrefix, "");
		} else if ("extension".equals(expression)) {
			return FileTools.getExtension(file.getName(), extensionPrefix, "");
		} else if ("name".equals(expression)) {
			return FileTools.getFileName(file);
		}
		throw new EvaluationException("can't evaluate '" + expression + "'");
	}

}
