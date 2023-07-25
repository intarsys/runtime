package de.intarsys.tools.activity.ui;

public interface IReportStatusUI {

	void clearStatus(ReportStatusUpdater sourceUpdater);

	void updateStatus(ReportStatusUpdater sourceUpdater, String message);

	void updateStatus(ReportStatusUpdater sourceUpdater, String message, String detailMessage, float progress);
}
