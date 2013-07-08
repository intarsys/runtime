package de.intarsys.tools.logging;

/**
 * A "simple device context".
 * <p>
 * With this idiom you can associate a simple tag with the current thread that
 * is referenced from a log formatter when publishing.
 * 
 */
public class SDC {

	final static private ThreadLocal<String> message = new ThreadLocal<String>();

	static public void clear() {
		message.remove();
	}

	static public String get() {
		return message.get();
	}

	static public void set(String value) {
		message.set(value);
	}
}
