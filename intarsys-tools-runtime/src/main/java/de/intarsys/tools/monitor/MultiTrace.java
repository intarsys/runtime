package de.intarsys.tools.monitor;

import java.util.List;

import de.intarsys.tools.collection.ListTools;
import de.intarsys.tools.yalf.api.Level;

/**
 * A simple {@link ITrace} implementation that can compose a collection of other
 * {@link ITrace} instances.
 * <p>
 * This class is not thread safe as its usage is filtered by a ThreadLocal and
 * as such is access from a single thread only.
 */
public class MultiTrace implements ITrace {

	private Object trace;

	public boolean isEmpty() {
		return trace == null;
	}

	public void registerTrace(ITrace pTrace) {
		if (trace == null) {
			trace = pTrace;
		} else {
			if (trace instanceof List) {
				((List) trace).add(pTrace);
			} else {
				trace = ListTools.with((ITrace) trace, pTrace);
			}
		}
	}

	@Override
	public ISample sample(Level level, String description) {
		if (trace == null) {
			return null;
		} else {
			if (trace instanceof List) {
				for (ITrace childTrace : (List<ITrace>) trace) {
					childTrace.sample(level, description);
				}
				return null;
			} else {
				return ((ITrace) trace).sample(level, description);
			}
		}
	}

	@Override
	public void stop() {
		if (trace != null) {
			if (trace instanceof List) {
				for (ITrace childTrace : (List<ITrace>) trace) {
					childTrace.stop();
				}
			} else {
				((ITrace) trace).stop();
			}
		}
	}

	@Override
	public void tag(String key, Object tag) {
		if (trace != null) {
			if (trace instanceof List) {
				for (ITrace childTrace : (List<ITrace>) trace) {
					childTrace.tag(key, tag);
				}
			} else {
				((ITrace) trace).tag(key, tag);
			}
		}
	}

	public void unregisterTrace(ITrace pTrace) {
		if (trace != null) {
			if (trace instanceof List) {
				((List) trace).remove(pTrace);
				if (((List) trace).isEmpty()) {
					trace = null;
				}
			} else if (trace == pTrace) {
				trace = null;
			}
		}
	}

}
