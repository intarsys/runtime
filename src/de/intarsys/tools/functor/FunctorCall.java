/*
 * Copyright (c) 2007, intarsys consulting GmbH
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
package de.intarsys.tools.functor;

/**
 * Generic implementation of {@link IFunctorCall}.
 * 
 */
public class FunctorCall implements IFunctorCall {

	/**
	 * Create an {@link IFunctorCall} with <code>receiver</code> as the receiver
	 * and the indexed arguments.
	 * 
	 * @param receiver
	 *            The receiver for the call.
	 * @param arguments
	 *            The indexed arguments for the call.
	 * @return The new {@link IFunctorCall}
	 */
	public static FunctorCall create(Object receiver, Object... arguments) {
		Args args = new Args(arguments);
		return new FunctorCall(receiver, args);
	}

	/**
	 * Create an {@link IFunctorCall} with <code>receiver</code> as the receiver
	 * and no arguments.
	 * 
	 * @param receiver
	 *            The receiver for the call.
	 * @return The new {@link IFunctorCall}
	 */
	public static FunctorCall noargs(Object receiver) {
		return new FunctorCall(receiver, Args.create());
	}

	private IArgs currentArgs;

	private Object receiver;

	public FunctorCall(Object receiver, IArgs args) {
		super();
		this.currentArgs = args;
		this.receiver = receiver;
	}

	public IArgs getArgs() {
		return currentArgs;
	}

	public Object getReceiver() {
		return receiver;
	}

	public void setArgs(IArgs args) {
		currentArgs = args;
	}

	public void setReceiver(Object receiver) {
		this.receiver = receiver;
	}
}
