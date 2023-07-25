package de.intarsys.tools.session;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An {@link ISessionProvider} that delegates to a thread specific
 * {@link ISessionProvider}.
 * 
 */
public class ThreadSessionProvider implements ISessionProvider {

	private static final ThreadLocal<Deque<ISessionProvider>> CONTEXTS = ThreadLocal.withInitial(
			() -> new ArrayDeque<>());

	public void attach(ISessionProvider provider) {
		Deque<ISessionProvider> stack = CONTEXTS.get();
		stack.push(provider);
	}

	public void detach() {
		Deque<ISessionProvider> stack = CONTEXTS.get();
		stack.pop();
		if (stack.isEmpty()) {
			CONTEXTS.remove();
		}
	}

	@Override
	public ISession getSession() {
		Deque<ISessionProvider> stack = CONTEXTS.get();
		ISessionProvider temp = stack.peek();
		return temp == null ? null : temp.getSession();
	}
}
