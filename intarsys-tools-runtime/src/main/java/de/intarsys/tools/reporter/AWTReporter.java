package de.intarsys.tools.reporter;

import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.intarsys.tools.activity.ReportStatus;
import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.message.IMessage;
import de.intarsys.tools.progress.IProgressMonitor;
import de.intarsys.tools.string.StringTools;

/**
 * AWT based {@link IReporter} implementation.
 * 
 * ATTENTION this is untested / unused so far
 * 
 */
public class AWTReporter implements IReporter {

	private static final Attribute ATTR_DIALOG = new Attribute("dialog");

	private boolean standalone;

	protected void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	protected AWTReporterDialog createDialog(final String message) {
		final AWTReporterDialog dialog;
		Frame parent = JOptionPane.getFrameForComponent(null);
		dialog = new AWTReporterDialog(parent, StringTools.EMPTY, message);
		return dialog;
	}

	public boolean isStandalone() {
		return standalone;
	}

	@Override
	public IProgressMonitor reportActivityStart(final IMessage message, final int style) {
		final ReportStatus activity = new ReportStatus(null) {

			@Override
			protected void onFinally() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						AWTReporterDialog dialog = (AWTReporterDialog) getAttribute(ATTR_DIALOG);
						if (dialog != null) {
							dialog.setVisible(false);
							dialog.dispose();
							setAttribute(ATTR_DIALOG, null);
						}
					}
				});
				super.onFinally();
			}

			@Override
			public void worked(final float amount) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						AWTReporterDialog dialog = (AWTReporterDialog) getAttribute(ATTR_DIALOG);
						if (dialog != null) {
							dialog.setMessage(getFullName());
							dialog.setProgress((int) getWorkedPercent());
						}
					}
				});
				super.worked(amount);
			}
		};
		activity.setMessage(message);
		if ((style & IReporter.STYLE_BEEP) != 0) {
			beep();
		}
		activity.enter();
		if (isStandalone() || (style & IReporter.STYLE_STANDALONE) != 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					AWTReporterDialog dialog = createDialog(message.getString());
					activity.setAttribute(ATTR_DIALOG, dialog);
					dialog.setVisible(true);
				}
			});
		}
		return activity;
	}

	@Override
	public void reportError(String title, String message, Throwable t, int style) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void reportMessage(String title, String message, int style) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void reportStatus(String message, int style) {
		// not required
	}

	public void setStandalone(boolean standalone) {
		this.standalone = standalone;
	}

}
