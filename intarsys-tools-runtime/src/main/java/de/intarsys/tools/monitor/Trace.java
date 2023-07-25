package de.intarsys.tools.monitor;

import de.intarsys.tools.component.Singleton;

/**
 * Tool class to access all currently active {@link ITrace} instances.
 */
@Singleton
public class Trace {

	private static final ThreadLocal<MultiTrace> TRACE = ThreadLocal.withInitial(() -> new MultiTrace());

	public static ITrace get() {
		return TRACE.get();
	}

	public static void registerTrace(ITrace pTrace) {
		TRACE.get().registerTrace(pTrace);
	}

	public static void unregisterTrace(ITrace pTrace) {
		MultiTrace trace = TRACE.get();
		trace.unregisterTrace(pTrace);
		if (trace.isEmpty()) {
			TRACE.remove();
		}
	}

	private Trace() {
	}
}
