/*
 * Copyright (c) 2008, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.tools.reflect;

/**
 * An exception in the execution of a method.
 */
public class MethodInvocationException extends MethodException {

	public MethodInvocationException(Class clazz, String name) {
		super(clazz, name);
	}

	public MethodInvocationException(Class clazz, String name, String message) {
		super(clazz, name, message);
	}

	public MethodInvocationException(Class clazz, String name, String message,
			Throwable cause) {
		super(clazz, name, message, cause);
	}

	public MethodInvocationException(Class clazz, String name, Throwable cause) {
		super(clazz, name, cause.getLocalizedMessage(), cause);
	}

	public MethodInvocationException(String name) {
		super(null, name);
	}

	public MethodInvocationException(String name, String message) {
		super(null, name, message);
	}

	public MethodInvocationException(String name, String message,
			Throwable cause) {
		super(null, name, message, cause);
	}

	public MethodInvocationException(String name, Throwable cause) {
		super(null, name, cause.getLocalizedMessage(), cause);
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("method '");
		sb.append(getName());
		sb.append("'");
		if (getTargetClass() != null) {
			sb.append(" in ");
			sb.append(getTargetClass().getName());
		}
		sb.append(" invocation exception");
		if (getCause() != null) {
			sb.append(" (");
			sb.append(getCause());
			sb.append(")");
		}
		return sb.toString();
	}
}
