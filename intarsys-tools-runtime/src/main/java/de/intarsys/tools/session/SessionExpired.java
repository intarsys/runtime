package de.intarsys.tools.session;

public class SessionExpired extends RuntimeException {

	public SessionExpired() {
		super();
	}

	public SessionExpired(String message) {
		super(message);
	}

}
