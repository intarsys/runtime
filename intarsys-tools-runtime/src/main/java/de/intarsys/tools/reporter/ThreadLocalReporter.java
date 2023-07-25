package de.intarsys.tools.reporter;

import de.intarsys.tools.activity.ReportStatus;
import de.intarsys.tools.component.Singleton;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.progress.IProgressMonitor;

@Singleton
public class ThreadLocalReporter implements IReporter {

	private static final ThreadLocalReporter UNIQUE = new ThreadLocalReporter();

	public static final ThreadLocalReporter get() {
		return UNIQUE;
	}

	private InheritableThreadLocal<IReporter> reporter = new InheritableThreadLocal<>();

	public void attach(IReporter pReporter) {
		reporter.set(pReporter);
	}

	@Override
	public IProgressMonitor reportActivityStart(IMessage message, int style) {
		IReporter tempReporter = reporter.get();
		if (tempReporter == null) {
			ReportStatus activity = new ReportStatus(null);
			activity.setMessage(message);
			activity.enter();
			return activity;
		}
		return tempReporter.reportActivityStart(message, style);
	}

	@Override
	public void reportError(String title, String message, Throwable t, int style) {
		IReporter tempReporter = reporter.get();
		if (tempReporter == null) {
			return;
		}
		tempReporter.reportError(title, message, t, style);
	}

	@Override
	public void reportMessage(String title, String message, int style) {
		IReporter tempReporter = reporter.get();
		if (tempReporter == null) {
			return;
		}
		tempReporter.reportMessage(title, message, style);
	}

	@Override
	public void reportStatus(String message, int style) {
		IReporter tempReporter = reporter.get();
		if (tempReporter == null) {
			return;
		}
		tempReporter.reportStatus(message, style);
	}

}
