/*
 *   o_
 * in|tarsys GmbH (c)
 *   
 * all rights reserved
 *
 */
package de.intarsys.tools.exception;

import de.intarsys.tools.converter.ConversionException;
import de.intarsys.tools.converter.ConverterRegistry;

public class ErrorObject {

	public static ErrorObject create(Throwable t) {
		try {
			return ConverterRegistry.get().convert(t, ErrorObject.class);
		} catch (ConversionException e1) {
			return ErrorObjectFromThrowableConverter.ACTIVE.convert(t);
		}
	}

	private String code;

	private String message;

	private int status;

	public ErrorObject() {
		super();
	}

	public ErrorObject(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public int getStatus() {
		return status;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "" + code + ":" + message;
	}

}
