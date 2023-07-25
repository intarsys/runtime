package de.intarsys.tools.progress;

import de.intarsys.tools.exception.InvalidRequestException;
import de.intarsys.tools.string.StringTools;

/**
 * A simple pluggable implementation for {@link IProgressMonitor} that handles
 * state and some arithmetics.
 * 
 */
public class BaseProgressMonitor implements IProgressMonitor {

	private static final int HUNDRED_PERCENT = 100;

	private volatile String taskName;

	private volatile String subTaskName;

	private volatile float work = -1;

	private volatile boolean cancelled;

	private volatile float worked;

	private volatile boolean started;

	private volatile boolean atEnd;

	@Override
	public void begin(String name, float totalWork) {
		if (started) {
			throw new InvalidRequestException("monitor already started");
		}
		this.started = true;
		this.taskName = name;
		this.work = totalWork;
		this.worked = 0;
	}

	@Override
	public void end() {
		if (work > worked) {
			// we still have some work - go all the way
			worked(work - worked);
		}
		// avoid rounding errors
		atEnd = true;
		started = false;
		worked = work;
		subTaskName = null;
	}

	/**
	 * A "full name", built from the task name and the optional subtask name.
	 * 
	 * @return a full name
	 */
	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		if (!StringTools.isEmpty(getTaskName())) {
			sb.append(getTaskName());
		}
		if (!StringTools.isEmpty(getSubTaskName())) {
			sb.append(" - ");
			sb.append(getSubTaskName());
		}
		return sb.toString();
	}

	/**
	 * The current subtask name.
	 * 
	 * This is set using the {@link #subTask(String)} call and may be null.
	 * 
	 * @return the current subtask name.
	 */
	public String getSubTaskName() {
		return subTaskName;
	}

	/**
	 * The current task name.
	 * 
	 * This is set using the {@link #begin(String, float)} call.
	 * 
	 * @return the current task name.
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * The total amount of work to do.
	 * 
	 * This is set by the {@link #begin(String, float)} call.
	 * 
	 * @return
	 */
	public float getWork() {
		return work;
	}

	/**
	 * The amount of work done so far.
	 * 
	 * This is accumulated from {@link #worked(float)} calls.
	 * 
	 * @return
	 */
	public float getWorked() {
		return worked;
	}

	/**
	 * The amount worked so far as a percentile.
	 * 
	 * @return
	 */
	public float getWorkedPercent() {
		float tmpWork = getWork();
		float tmpWorked = getWorked();
		if (tmpWork == 0) {
			return 0;
		}
		return (tmpWorked * HUNDRED_PERCENT) / tmpWork;
	}

	/**
	 * True if this monitor is at its end.
	 * 
	 * This is true if either the client calls {@link #end()} or the amount of
	 * work reported via {@link #worked(float)} is larger than the amount of
	 * total work reported via {@link #begin(String, float)}.
	 * 
	 * @return
	 */
	public boolean isAtEnd() {
		return atEnd;
	}

	@Override
	public synchronized boolean isCancelled() {
		return cancelled;
	}

	/**
	 * True after {@link #begin(String, float)} and before {@link #end()}.
	 * 
	 * @return
	 */
	public boolean isStarted() {
		return started;
	}

	public synchronized void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public IProgressMonitor spawn(float work) {
		return new SubProgressMonitor(this, work);
	}

	@Override
	public void subTask(String name) {
		this.subTaskName = name;
	}

	@Override
	public void worked(float amount) {
		if (!started) {
			throw new InvalidRequestException("monitor not started");
		}
		worked += amount;
		if (worked >= work) {
			worked = work;
			atEnd = true;
		}
		if (worked < 0) {
			worked = 0;
		}
	}

}
