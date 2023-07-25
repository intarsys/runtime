package de.intarsys.tools.activity.ui;

import de.intarsys.tools.activity.ReportStatus;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.state.IState;
import de.intarsys.tools.string.StringTools;

public class ReportStatusUpdater {

	private class AttributeChangedListener implements INotificationListener<AttributeChangedEvent> {
		@Override
		public void handleEvent(AttributeChangedEvent event) {
			if (event.getAttribute().equals(IState.ATTR_STATE) && ((IState) event.getNewValue()).isFinal()) {
				reportStatus.removeNotificationListener(AttributeChangedEvent.ID, this);
				ui.clearStatus(ReportStatusUpdater.this);
			} else {
				updateReportStatus();
			}
		}
	}

	ReportStatus<?> reportStatus;
	IReportStatusUI ui;

	public ReportStatusUpdater(ReportStatus<?> reportStatus, IReportStatusUI ui) {
		this.reportStatus = reportStatus;
		this.ui = ui;
		this.reportStatus.addNotificationListener(AttributeChangedEvent.ID, new AttributeChangedListener());
	}

	public void updateReportStatus() {
		/**
		 * if the report status has a task name it should also have a "worked".
		 * Otherwise we assume indefinite progress. Would prefer to rely on
		 * state but taskName is not set yet when "active" state is reported
		 */
		String taskName = reportStatus.getTaskName();
		String subTaskName = reportStatus.getSubTaskName();
		if (StringTools.isEmpty(taskName) && StringTools.isEmpty(subTaskName)) {
			ui.updateStatus(this, reportStatus.getMessage().getString());
		} else {
			ui.updateStatus(this, taskName, subTaskName, reportStatus.getWorkedPercent());
		}
	}
}
