package de.intarsys.tools.exception;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

public class EncodedException extends Exception implements ICodeException {

	public static EncodedException create(IArgs args) {
		String code = ArgTools.getString(args, "error.code", "unknown");
		String message = ArgTools.getString(args, "error.message", "unexpected failure");
		return new EncodedException(code, message);
	}

	private final String code;

	private final int status;

	public EncodedException(int status, String code) {
		super();
		this.code = code;
		this.status = status;
	}

	public EncodedException(int status, String code, String message) {
		super(message);
		this.code = code;
		this.status = status;
	}

	public EncodedException(int status, String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.status = status;
	}

	public EncodedException(int status, String code, Throwable cause) {
		super(cause);
		this.code = code;
		this.status = status;
	}

	public EncodedException(String code) {
		super();
		this.code = code;
		this.status = 500;
	}

	public EncodedException(String code, String message) {
		super(message);
		this.code = code;
		this.status = 500;
	}

	public EncodedException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.status = 500;
	}

	public EncodedException(String code, Throwable cause) {
		super(cause);
		this.code = code;
		this.status = 500;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public int getStatus() {
		return status;
	}

}
