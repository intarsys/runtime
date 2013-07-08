package de.intarsys.tools.monitor;

import java.util.List;
import java.util.logging.Level;

import de.intarsys.tools.collection.ListTools;

/**
 * A simple {@link ITrace} implementation that can compose a collection of other
 * {@link ITrace} instances.
 * <p>
 * This class is not thread safe as its usage is filtered by a ThreadLocal and
 * as such is access from a single thread only.
 */
public class MultiTrace implements ITrace {

	private Object trace = null;

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

	public void tag(String key, Object tag) {
		if (trace == null) {
			return;
		} else {
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
		if (trace == null) {
			return;
		} else {
			if (trace instanceof List) {
				((List) trace).remove(pTrace);
				if (((List) trace).size() == 0) {
					trace = null;
				}
			} else if (trace == pTrace) {
				trace = null;
			}
		}
	}

}
