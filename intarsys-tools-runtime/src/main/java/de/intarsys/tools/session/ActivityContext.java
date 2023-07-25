package de.intarsys.tools.session;

import de.intarsys.tools.component.Singleton;
import de.intarsys.tools.concurrent.ForwardedThreadLocal;

/**
 * A VM singleton to access an {@link IActivityContext}.
 * 
 */
@Singleton
public final class ActivityContext {

	private static final ThreadLocal<IActivityContext> CONTEXT = new ForwardedThreadLocal() {
		@Override
		protected Object initialValue() {
			return new StandardActivityContext();
		}
	};

	/**
	 * The current activity context. This will never return null.
	 * 
	 * @return
	 */
	public static IActivityContext get() {
		return CONTEXT.get();
	}

	/**
	 * Assign the current activity context.
	 */
	public static void set(IActivityContext value) {
		if (value == null) {
			CONTEXT.remove();
		} else {
			CONTEXT.set(value);
		}
	}

	private ActivityContext() {
	}

}
