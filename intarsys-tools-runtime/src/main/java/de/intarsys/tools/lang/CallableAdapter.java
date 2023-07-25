/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.lang;

import java.util.concurrent.Callable;

/**
 * This adapter eases life when an API supports a {@link Runnable} parameter but
 * you need to return a result.
 * 
 * Example
 * 
 * <code>
 * CallableAdapter ret = new CallableAdapter() {
 * 	public Result call() {
 * 		return "cheers";
 * 	}
 * };
 * Display.getDefault().syncExec(ret);
 * result = ret.getResultUnchecked();
 * </code>
 * 
 * @param <T>
 */
public abstract class CallableAdapter<T> implements Runnable, Callable<T> {

	private T result;

	private Exception exception;

	/**
	 * Get the exception if one occured when "run" is executed.
	 * 
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Get the result of the computation after "run" is executed. If execution
	 * has thrown an exception, the exception is thrown here.
	 * 
	 * @return
	 * @throws Exception
	 */
	public T getResult() throws Exception {
		if (exception != null) {
			throw exception;
		}
		return result;
	}

	/**
	 * Get the result of the computation after "run" is executed. If execution
	 * has thrown an exception, null is returned.
	 * 
	 * @return
	 */
	public T getResultUnchecked() {
		if (exception != null) {
			return null;
		}
		return result;
	}

	@Override
	public void run() {
		try {
			setResult(call());
		} catch (Exception e) {
			exception = e;
		}
	}

	protected void setResult(T result) {
		this.result = result;
	}
}
