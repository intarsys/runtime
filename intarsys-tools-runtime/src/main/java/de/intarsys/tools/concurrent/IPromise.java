/*
 * Copyright (c) 2014, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.concurrent;

import java.util.concurrent.Future;

/**
 * A promise is an object representing a "to be computed" result. As opposed to
 * the {@link Future}, the outcome of the promise is provided by call from an
 * external client.
 * 
 * @param <R>
 *            The result type for the promise.
 */
public interface IPromise<R> extends Future<R> {

	/**
	 * Indicate failure for the {@link IPromise}.
	 * 
	 * This method should be called only once. With respect to the fact that in
	 * case of error, a client may receive still more failures, this method will
	 * ignore each subsequent invocation.
	 * 
	 * @param t
	 */
	public void fail(Throwable t);

	/**
	 * Indicate success for the {@link IPromise}.
	 * 
	 * This method can be called only once. Only one of "fail" and "finish" can
	 * be called for an {@link IPromise}
	 * 
	 * @param result
	 */
	public void finish(R result);

}
