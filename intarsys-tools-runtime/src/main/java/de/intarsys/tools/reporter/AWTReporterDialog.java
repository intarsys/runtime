package de.intarsys.tools.reporter;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class AWTReporterDialog extends JDialog {

	private JProgressBar cProgress;

	private JLabel cLabel;

	public AWTReporterDialog(Dialog dialog, String title, String message) {
		super(dialog, title, true);
		init(message);
	}

	public AWTReporterDialog(Frame frame, String title, String message) {
		super(frame, title, true);
		init(message);
	}

	private void init(String message) {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (getContentPane() instanceof JComponent) {
			JComponent component = (JPanel) getContentPane();
			component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		}

		setUndecorated(true);
		setAlwaysOnTop(true);
		setModal(false);

		setLayout(new BorderLayout(10, 10));

		Icon infoIcon = UIManager.getIcon("OptionPane.informationIcon"); //$NON-NLS-1$
		JLabel iconLabel = new JLabel(infoIcon);
		add(iconLabel, BorderLayout.LINE_START);

		cLabel = new JLabel(message);
		add(cLabel, BorderLayout.CENTER);

		cProgress = new JProgressBar();
		cProgress.setIndeterminate(true);
		add(cProgress, BorderLayout.PAGE_END);

		pack();

		Dimension d = getSize();
		setLocation(-(d.width / 2), -(d.height / 2));
		setLocationRelativeTo(getParent());
	}

	public void setMessage(String message) {
		cLabel.setText(message);
	}

	public void setProgress(int progress) {
		cProgress.setValue(progress);
	}

}
