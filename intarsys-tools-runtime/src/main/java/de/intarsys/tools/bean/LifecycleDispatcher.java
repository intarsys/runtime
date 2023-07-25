package de.intarsys.tools.bean;

import java.util.Set;

import de.intarsys.tools.component.IStartStop;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.functor.FunctorCall;
import de.intarsys.tools.functor.IFunctor;
import de.intarsys.tools.yalf.api.ILogger;

/**
 * 
 * This tool is intended to facilitate the propagation of lifecycle events to
 * any bean carrying bean role annotation with the requested role.
 * 
 */
public class LifecycleDispatcher {

	private static final ILogger Log = PACKAGE.Log;

	private final String role;

	public LifecycleDispatcher(String role) {
		super();
		this.role = role;
	}

	public String getRole() {
		return role;
	}

	public void start() {
		for (Object object : BeanContainer.get().lookupBeans(getRole(), Object.class)) {
			try {
				if (object instanceof IStartStop) {
					((IStartStop) object).start();
				} else if (object instanceof IFunctor) {
					((IFunctor) object).perform(FunctorCall.create(null));
				} else if (object instanceof Runnable) {
					((Runnable) object).run();
				}
			} catch (Exception e) {
				Log.warn("{}.start has encountered exception {} ", getRole(), ExceptionTools.getMessage(e), e);
			}
		}
	}

	public void stop() {
		for (Object object : BeanContainer.get().lookupBeans(getRole(), Object.class)) {
			try {
				if (object instanceof IStartStop) {
					((IStartStop) object).stop();
				}
			} catch (Exception e) {
				Log.warn("{}.stop has encountered exception {} ", getRole(), ExceptionTools.getMessage(e), e);
			}
		}
	}

	public boolean stopRequested(Set visited) {
		for (Object object : BeanContainer.get().lookupBeans(getRole(), Object.class)) {
			try {
				if (object instanceof IStartStop) {
					if (!((IStartStop) object).stopRequested(visited)) {
						return false;
					}
				}
			} catch (Exception e) {
				Log.warn("{}.stopRequested has encountered exception {} ", getRole(), ExceptionTools.getMessage(e), e);
			}
		}
		return true;
	}

}
