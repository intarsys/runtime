package de.intarsys.tools.activity;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import de.intarsys.tools.concurrent.ITaskCallback;
import de.intarsys.tools.concurrent.TaskFailed;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * A mixin implementation for monitoring activities and act on their lifecycle
 * events.
 * 
 * Lifecycle events from activities (activityEnter, activityFinished,
 * activityFailed, activityFinally) are monitored. For each event a local method
 * is called via a pluggable {@link Executor}.
 * 
 * The monitor may in addition decide if the spawned task is synchronous or not,
 * allowing the calling activity to synchronize its processing with the activity
 * handler (e.g.the UI).
 *
 */
public abstract class ActivityLifecycleMonitor implements IActivityHandler {

	private static final ILogger Log = PACKAGE.Log;

	private boolean active = true;

	private boolean synchronous;

	protected void activityChanged(IActivity<?> activity, AttributeChangedEvent event) {
		if (!isActive()) {
			return;
		}
		submitActivityChanged(activity, event);
		// this should not be synchronous
	}

	@Override
	public <R> void activityEnter(IActivity<R> activity) {
		if (!isActive()) {
			return;
		}
		if (activity instanceof INotificationSupport) {
			((INotificationSupport) activity).addNotificationListener(AttributeChangedEvent.ID,
					new INotificationListener<AttributeChangedEvent>() {
						@Override
						public void handleEvent(AttributeChangedEvent event) {
							activityChanged(activity, event);
						}
					});
		}
		activity.addTaskCallback(new ITaskCallback<R>() {
			@Override
			public void failed(TaskFailed exception) {
				try {
					activityFailed(activity);
				} finally {
					activityFinally(activity);
				}
			}

			@Override
			public void finished(R result) {
				try {
					activityFinished(activity);
				} finally {
					activityFinally(activity);
				}
			}
		});
		Future task = submitActivityEnter(activity);
		if (task != null && isSynchronous()) {
			ExceptionTools.futureSimpleGet(task);
		}
	}

	protected void activityFailed(IActivity<?> activity) {
		if (!isActive()) {
			return;
		}
		submitActivityFailed(activity);
		// synchronization is done on finally
	}

	protected void activityFinally(IActivity<?> activity) {
		if (!isActive()) {
			return;
		}
		Future task = submitActivityFinally(activity);
		if (task != null && isSynchronous()) {
			ExceptionTools.futureSimpleGet(task);
		}
	}

	protected void activityFinished(IActivity<?> activity) {
		if (!isActive()) {
			return;
		}
		submitActivityFinished(activity);
		// synchronization is done on finally
	}

	protected void doActivityChanged(final IActivity<?> activity, final AttributeChangedEvent event) {
		//
	}

	protected void doActivityEnter(final IActivity<?> activity) {
		//
	}

	protected void doActivityFailed(IActivity<?> activity) {
		//
	}

	protected void doActivityFinally(final IActivity<?> activity) {
		//
	}

	protected void doActivityFinished(IActivity<?> activity) {
		//
	}

	protected String getLogLabel() {
		return "activity monitor " + hashCode();
	}

	public boolean isActive() {
		return active;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	/**
	 * @param r
	 *            The runnable to be submitted
	 * @return
	 */
	protected Future submit(Runnable r) {
		return null;
	}

	protected Future submitActivityChanged(IActivity<?> activity, AttributeChangedEvent event) {
		Future task = submit(new Runnable() {
			@Override
			public void run() {
				if (!isActive()) {
					return;
				}
				Log.trace("{} {}", getLogLabel(), this); //$NON-NLS-1$
				doActivityChanged(activity, event);
			}

			@Override
			public String toString() {
				return "activityChanged " + activity;
			}
		});
		return task;
	}

	protected <R> Future submitActivityEnter(IActivity<R> activity) {
		Future task = submit(new Runnable() {
			@Override
			public void run() {
				if (!isActive()) {
					return;
				}
				Log.trace("{} {}", getLogLabel(), this); //$NON-NLS-1$
				doActivityEnter(activity);
			}

			@Override
			public String toString() {
				return "activityEnter " + activity;
			}
		});
		return task;
	}

	protected Future submitActivityFailed(IActivity<?> activity) {
		Future task = submit(new Runnable() {
			@Override
			public void run() {
				if (!isActive()) {
					return;
				}
				Log.trace("{} {}", getLogLabel(), this); //$NON-NLS-1$
				doActivityFailed(activity);
			}

			@Override
			public String toString() {
				return "activityFailed " + activity;
			}
		});
		return task;
	}

	protected Future submitActivityFinally(IActivity<?> activity) {
		Future task = submit(new Runnable() {
			@Override
			public void run() {
				if (!isActive()) {
					return;
				}
				Log.trace("{} {}", getLogLabel(), this); //$NON-NLS-1$
				doActivityFinally(activity);
			}

			@Override
			public String toString() {
				return "activityFinally " + activity;
			}
		});
		return task;
	}

	protected Future submitActivityFinished(IActivity<?> activity) {
		Future task = submit(new Runnable() {
			@Override
			public void run() {
				if (!isActive()) {
					return;
				}
				Log.trace("{} {}", getLogLabel(), this); //$NON-NLS-1$
				doActivityFinished(activity);
			}

			@Override
			public String toString() {
				return "activityFinished " + activity;
			}
		});
		return task;
	}
}
