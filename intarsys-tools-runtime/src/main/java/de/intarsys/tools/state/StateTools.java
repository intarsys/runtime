package de.intarsys.tools.state;

/**
 * Tool class for handling states.
 * 
 */
public final class StateTools {

	public static void enterThreadState(IStateHolder holder, String id) {
		holder.enterState(ConcurrentState.create(Thread.currentThread(), id));

	}

	public static void leaveThreadState(IStateHolder holder) {
		holder.enterState(ConcurrentState.create(Thread.currentThread(), AtomicState.DISPOSED));

	}

	private StateTools() {
	}
}
