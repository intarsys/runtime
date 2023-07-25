package de.intarsys.tools.exception;

import de.intarsys.tools.functor.ArgTools;
import de.intarsys.tools.functor.IArgs;

public class EncodedRuntimeException extends RuntimeException implements ICodeException {

	public static EncodedRuntimeException create(IArgs args) {
		String code = ArgTools.getString(args, "error.code", "unknown");
		String message = ArgTools.getString(args, "error.message", "unexpected failure");
		return new EncodedRuntimeException(code, message);
	}

	private final String code;

	private final int status;

	public EncodedRuntimeException(int status, String code) {
		super();
		this.code = code;
		this.status = status;
	}

	public EncodedRuntimeException(int status, String code, String message) {
		super(message);
		this.code = code;
		this.status = status;
	}

	public EncodedRuntimeException(int status, String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.status = status;
	}

	public EncodedRuntimeException(int status, String code, Throwable cause) {
		super(cause);
		this.code = code;
		this.status = status;
	}

	public EncodedRuntimeException(String code) {
		super();
		this.code = code;
		this.status = 500;
	}

	public EncodedRuntimeException(String code, String message) {
		super(message);
		this.code = code;
		this.status = 500;
	}

	public EncodedRuntimeException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.status = 500;
	}

	public EncodedRuntimeException(String code, Throwable cause) {
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
