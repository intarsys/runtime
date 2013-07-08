/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.intarsys.tools.component.IStartStop;

/**
 * A tool for intercepting and handling lifecycle related events.
 * 
 */
abstract public class LifecycleWatchdog implements IStartStop {

	final private INotificationSupport object;

	final private List armedObjects = new ArrayList();

	private boolean started = false;

	private String label;

	/**
	 * A listener for object lifecycle events
	 */
	final protected INotificationListener listenObjectLifecycle = new INotificationListener() {
		public void handleEvent(Event event) {
			onObjectLifecycleEvent(event);
		}

		@Override
		public String toString() {
			return LifecycleWatchdog.this.toString();
		}
	};

	/**
	 * A listener for factory lifecycle events
	 */
	final protected INotificationListener listenFactoryLifecycle = new INotificationListener() {
		public void handleEvent(Event event) {
			onFactoryLifecycleEvent(event);
		}

		@Override
		public String toString() {
			return LifecycleWatchdog.this.toString();
		}
	};

	public LifecycleWatchdog(INotificationSupport factory) {
		super();
		if (factory == null) {
			throw new NullPointerException("watchdog must have a factory");
		}
		this.object = factory;
	}

	protected void arm(INotificationSupport object) {
		//
		synchronized (armedObjects) {
			armedObjects.add(object);
		}
		//
		object.addNotificationListener(StartedEvent.ID, listenObjectLifecycle);
		object
				.addNotificationListener(ActivatedEvent.ID,
						listenObjectLifecycle);
		object.addNotificationListener(DeactivatedEvent.ID,
				listenObjectLifecycle);
		object.addNotificationListener(StopRequestedEvent.ID,
				listenObjectLifecycle);
		object.addNotificationListener(StoppedEvent.ID, listenObjectLifecycle);
		object
				.addNotificationListener(DestroyedEvent.ID,
						listenObjectLifecycle);
	}

	protected void disarm(INotificationSupport object) {
		//
		synchronized (armedObjects) {
			armedObjects.remove(object);
		}
		//
		object.removeNotificationListener(StartedEvent.ID,
				listenObjectLifecycle);
		object.removeNotificationListener(ActivatedEvent.ID,
				listenObjectLifecycle);
		object.removeNotificationListener(DeactivatedEvent.ID,
				listenObjectLifecycle);
		object.removeNotificationListener(StopRequestedEvent.ID,
				listenObjectLifecycle);
		object.removeNotificationListener(StoppedEvent.ID,
				listenObjectLifecycle);
		object.removeNotificationListener(DestroyedEvent.ID,
				listenObjectLifecycle);
	}

	public String getLabel() {
		return label;
	}

	public INotificationSupport getObject() {
		return object;
	}

	public boolean isStarted() {
		return started;
	}

	final protected void myFactoryStarted(Event event) {
		start();
	}

	final protected void myFactoryStopped(Event event) {
		stop();
	}

	/**
	 * A previously armed IObject is activated.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectActivated(Object object) {
		objectActivated(object);
	}

	/**
	 * A new IObject is created and armed for further notification.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectCreated(Object object) {
		if (!supports(object)) {
			return;
		}
		arm((INotificationSupport) object);
		objectCreated(object);
	}

	/**
	 * A previously armed IObject is deactivated.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectDeactivated(Object object) {
		objectDeactivated(object);
	}

	/**
	 * A previously armed IObject is destroyed.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectDestroyed(Object object) {
		disarm((INotificationSupport) object);
		objectDestroyed(object);
	}

	/**
	 * A previously armed IObject is started.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectStarted(Object object) {
		objectStarted(object);
	}

	/**
	 * A previously armed IObject is stopped. All notification listeners are
	 * removed.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	final protected void myObjectStopped(Object object) {
		disarm((INotificationSupport) object);
		objectStopped(object);
	}

	/**
	 * A previously armed IObject is requested to stop.
	 * 
	 * @param event
	 *            The event instance
	 */
	final protected boolean myObjectStopRequested(Object object) {
		return objectStopRequested(object);
	}

	/**
	 * Redefine to get informed when an IObject is activated.
	 * 
	 * @param object
	 *            The object that was activated.
	 */
	protected void objectActivated(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is created.
	 * 
	 * @param object
	 *            The object that was created,
	 */
	protected void objectCreated(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is deactivated.
	 * 
	 * @param object
	 *            the object that was deactivated.
	 */
	protected void objectDeactivated(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is destroyed.
	 * 
	 * @param object
	 *            The object that was destroyed.
	 */
	protected void objectDestroyed(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is started.
	 * 
	 * @param object
	 *            The object that was started.
	 */
	protected void objectStarted(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is requested to stop.
	 * 
	 * @param object
	 *            The object that was stopped.
	 */
	protected void objectStopped(Object object) {
		// redefine
	}

	/**
	 * Redefine to get informed when an IObject is stopped.
	 * 
	 * @param object
	 *            The object that should be stopped.
	 */
	protected boolean objectStopRequested(Object object) {
		// redefine
		return true;
	}

	/**
	 * A previously armed object is activated.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	protected void onFactoryLifecycleEvent(Event event) {
		if (event instanceof StartedEvent) {
			myFactoryStarted(event);
		} else if (event instanceof StoppedEvent) {
			myFactoryStopped(event);
		} else {
			// ??
		}
	}

	/**
	 * A previously armed object is activated.
	 * 
	 * @param event
	 *            The activation event instance
	 */
	protected void onObjectLifecycleEvent(Event event) {
		if (event instanceof CreatedEvent) {
			CreatedEvent cpe = (CreatedEvent) event;
			myObjectCreated(cpe.getInstance());
		} else if (event instanceof DestroyedEvent) {
			myObjectDestroyed(event.getSource());
		} else if (event instanceof StartedEvent) {
			myObjectStarted(event.getSource());
		} else if (event instanceof ActivatedEvent) {
			myObjectActivated(event.getSource());
		} else if (event instanceof DeactivatedEvent) {
			myObjectDeactivated(event.getSource());
		} else if (event instanceof StopRequestedEvent) {
			event.setRc(myObjectStopRequested(event.getSource()));
		} else if (event instanceof StoppedEvent) {
			myObjectStopped(event.getSource());
		} else {
			// ??
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Arm the watchdog itself. From now it will listen to the creation of new
	 * instances.
	 * 
	 * @see de.intarsys.tools.component.IStartStop#start()
	 */
	public void start() {
		object.addNotificationListener(CreatedEvent.ID, listenObjectLifecycle);
		started = true;
	}

	/**
	 * Arm the watchdog itself. From now it will listen to the creation of new
	 * instances.
	 * 
	 * @see de.intarsys.tools.component.IStartStop#start()
	 */
	public void startAuto() {
		object.addNotificationListener(StartedEvent.ID, listenFactoryLifecycle);
	}

	/**
	 * Disarm the watchdog itself. From now it will not longer listen to the
	 * creation of new instances.
	 * 
	 * @see de.intarsys.tools.component.IStartStop#stop()
	 */
	public void stop() {
		object.removeNotificationListener(CreatedEvent.ID,
				listenObjectLifecycle);
		// disarm all already armed IObject instances, use copy to
		// avoid concurrent modification
		List toBeDisarmed;
		synchronized (armedObjects) {
			toBeDisarmed = new ArrayList(armedObjects);
		}
		for (Iterator it = toBeDisarmed.iterator(); it.hasNext();) {
			INotificationSupport object = (INotificationSupport) it.next();
			disarm(object);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.IStartStop#stopRequested()
	 */
	public boolean stopRequested(Set visited) {
		return true;
	}

	/**
	 * Answer <code>true</code> if we are interested in the <code>object</code>
	 * instance. If we are interested the watchdog will register itself for the
	 * lifecycle events.
	 * 
	 * @param object
	 *            The newly created object.
	 * @return Answer <code>true</code> if we are interested in the
	 *         <code>object</code> instance.
	 */
	protected boolean supports(Object object) {
		return true;
	}

	@Override
	public String toString() {
		return getLabel();
	}
}
