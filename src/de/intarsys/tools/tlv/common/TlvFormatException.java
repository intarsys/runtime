package de.intarsys.tools.tlv.common;

public class TlvFormatException extends IllegalArgumentException {

	public TlvFormatException() {
		super();
	}

	public TlvFormatException(String s) {
		super(s);
	}

	public TlvFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public TlvFormatException(Throwable cause) {
		super(cause);
	}

}
