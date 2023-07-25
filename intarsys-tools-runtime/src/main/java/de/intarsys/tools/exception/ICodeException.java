package de.intarsys.tools.exception;

public interface ICodeException {

	/**
	 * The client input value to an operation was not suitable
	 */
	public static final String EX_INVALID_ARGUMENT = "InvalidArgument";

	/**
	 * The process initiated ended with a failure state.
	 */
	public static final String EX_PROCESS_FAILED = "ProcessFailed";

	String getCode();

	String getMessage();

	int getStatus();

}
