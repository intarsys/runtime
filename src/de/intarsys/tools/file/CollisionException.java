package de.intarsys.tools.file;

import java.io.IOException;

/**
 * Indicate a collision when performing a file operation.
 * 
 */
public class CollisionException extends IOException {

	public CollisionException() {
		super();
	}

	public CollisionException(String message) {
		super(message);
	}

	public CollisionException(String message, Throwable cause) {
		super(message, cause);
	}

	public CollisionException(Throwable cause) {
		super(cause);
	}

}
