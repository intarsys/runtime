package de.intarsys.tools.zones;

import java.util.ArrayDeque;
import java.util.Deque;

import de.intarsys.tools.component.Singleton;
import de.intarsys.tools.concurrent.ForwardedThreadLocal;

/**
 * A VM singleton to access an {@link IZone}.
 * 
 */
@Singleton
public final class Zone {

	private static final ThreadLocal<Deque<IZone>> CURRENT = new ForwardedThreadLocal() {
		@Override
		protected Object initialValue() {
			return new ArrayDeque<>();
		}
	};

	private static final StandardZone ROOT_ZONE = new StandardZone("root", null);

	/**
	 * The current {@link IZone}. This will never return null.
	 * 
	 * @return
	 */
	public static IZone getCurrent() {
		Deque<IZone> zones = CURRENT.get();
		IZone temp = zones.peek();
		if (temp == null) {
			temp = ROOT_ZONE;
		}
		return temp;
	}

	/**
	 * The overall root zone.
	 * 
	 * @return
	 */
	public static IZone getRoot() {
		return ROOT_ZONE;
	}

	static IZone peek() {
		return CURRENT.get().peek();
	}

	protected static void pop() {
		Deque<IZone> stack = CURRENT.get();
		stack.pop();
		if (stack.isEmpty()) {
			CURRENT.remove();
		}
	}

	static void push(IZone zone) {
		CURRENT.get().push(zone);
	}

	private Zone() {
	}

}
