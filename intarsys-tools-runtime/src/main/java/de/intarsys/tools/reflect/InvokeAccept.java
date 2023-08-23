/*
 * Copyright (c) 2008, intarsys GmbH
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

import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.functor.IArgs;

/**
 * An event indicating a successfully terminated invocation.
 * 
 */
public class InvokeAccept extends Event {

	public static final EventType ID = new EventType(InvokeAccept.class.getName());

	private transient Object result;

	private final transient IArgs args;

	private final transient String name;

	private Throwable throwable;

	/**
	 * @param receiver
	 *            The original receiver of the call
	 * @param name
	 *            The method name
	 * @param args
	 *            The method arguments
	 * @param result
	 *            The optional result
	 * @param t
	 *            The optional exception
	 */
	public InvokeAccept(Object receiver, String name, IArgs args, Object result, Throwable t) {
		super(receiver);
		this.name = name;
		this.args = args;
	}

	public IArgs getArgs() {
		return args;
	}

	@Override
	public EventType getEventType() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

	public Object getReceiver() {
		return getSource();
	}

	public Object getResult() {
		return result;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
