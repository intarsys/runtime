package de.intarsys.tools.concurrent;

public class ForwardedThreadLocal<T> extends ThreadLocal<T> {

	public ForwardedThreadLocal() {
		super();
		ThreadLocalSnapshot.register(this);
	}

}
