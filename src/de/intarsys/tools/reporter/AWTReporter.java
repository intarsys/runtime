package de.intarsys.tools.reporter;

import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * AWT based {@link IReporter} implementation.
 * 
 */
public class AWTReporter implements IReporter {

	private JDialog optionDialog;

	private JOptionPane optionPane;

	protected void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	protected void closeDialog() {
		if (optionDialog != null) {
			optionDialog.setVisible(false);
			optionDialog.dispose();
			optionDialog = null;
		}
	}

	protected void openDialog() {
		optionPane = new JOptionPane("", JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_OPTION, null, new Object[0], null);
		optionDialog = optionPane.createDialog(null, "");
		optionDialog.setAlwaysOnTop(true);
		optionDialog.setModal(false);
		optionDialog.setVisible(true);
	}

	public void reportActivityEnd() {
		closeDialog();
	}

	public void reportActivityStart(final String message, int style) {
		if ((style & IReporter.STYLE_BEEP) != 0) {
			beep();
		}
		if ((style & IReporter.STYLE_STANDALONE) != 0) {
			openDialog();
			optionPane.setMessage(message);
		}
	}

	public void reportError(String title, String message, Throwable t, int style) {
	}

	public void reportMessage(String title, String message, int style) {
	}

	public void reportProgress(String message, int percent, int style) {
		if ((style & IReporter.STYLE_BEEP) != 0) {
			beep();
		}
		if ((style & IReporter.STYLE_STANDALONE) != 0) {
			if (percent >= 0) {
				if (optionDialog == null) {
					openDialog();
				}
				optionPane.setMessage(message + " (" + percent + "%)");
			} else {
				closeDialog();
			}
		}
	}

	public void reportStatus(String message, int style) {
	}

}
