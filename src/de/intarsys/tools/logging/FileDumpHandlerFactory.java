package de.intarsys.tools.logging;

import java.io.File;
import java.util.logging.Handler;

import de.intarsys.tools.environment.file.FileEnvironment;
import de.intarsys.tools.expression.EvaluationException;
import de.intarsys.tools.expression.IStringEvaluator;
import de.intarsys.tools.expression.TemplateEvaluator;
import de.intarsys.tools.file.FileTools;
import de.intarsys.tools.functor.Args;
import de.intarsys.tools.string.StringTools;

/**
 * An {@link IHandlerFactory} for {@link FileDumpHandler} instances.
 * 
 */
public class FileDumpHandlerFactory extends CommonHandlerFactory {

	private String directoryName = "dumplog.${system.uniquetime:d}";

	private IStringEvaluator templateEvaluator;

	@Override
	protected Handler basicCreateHandler() {
		FileDumpHandler tempHandler = new FileDumpHandler();
		tempHandler.setDirectory(getDirectory());
		return tempHandler;
	}

	public File getDirectory() {
		String tempName = getDirectoryName();
		if (StringTools.isEmpty(tempName)) {
			tempName = "dumplog.${system.uniquetime:d}";
		}
		try {
			tempName = (String) getTemplateEvaluator().evaluate(tempName,
					Args.create());
		} catch (EvaluationException ignore) {
			//
		}
		tempName = FileTools.trimPath(tempName);
		File parent = FileEnvironment.get().getProfileDir();
		return FileTools.resolvePath(parent, tempName);
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public IStringEvaluator getTemplateEvaluator() {
		if (templateEvaluator == null) {
			return TemplateEvaluator.get();
		}
		return templateEvaluator;
	}

	public void setDirectoryName(String fileDumpHandlerDirName) {
		this.directoryName = fileDumpHandlerDirName;
	}

	public void setTemplateEvaluator(IStringEvaluator stringEvaluator) {
		this.templateEvaluator = stringEvaluator;
	}
}
