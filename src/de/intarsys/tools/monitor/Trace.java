package de.intarsys.tools.monitor;

/**
 * Tool class to access all currently active {@link ITrace} instances.
 */
public class Trace {

	private static ThreadLocal<MultiTrace> trace = new ThreadLocal<MultiTrace>() {
		@Override
		protected MultiTrace initialValue() {
			return new MultiTrace();
		};
	};

	static public ITrace get() {
		return trace.get();
	}

	static public void registerTrace(ITrace pTrace) {
		trace.get().registerTrace(pTrace);
	}

	static public void unregisterTrace(ITrace pTrace) {
		trace.get().unregisterTrace(pTrace);
	}
}
