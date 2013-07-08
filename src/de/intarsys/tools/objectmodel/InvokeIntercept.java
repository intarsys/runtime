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
package de.intarsys.tools.objectmodel;

import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.functor.IArgs;
import de.intarsys.tools.reflect.MethodInvocationException;

/**
 * An event indicating the upcoming invocation of a method for a receiver.
 * 
 */
public class InvokeIntercept extends Event {
	public static final EventType ID = new EventType(InvokeIntercept.class
			.getName());

	final private IArgs args;

	final private INotificationListener dispatcher;

	final private IMethod method;

	private Object result;

	public InvokeIntercept(Object receiver, INotificationListener dispatcher,
			IMethod method, IArgs args) {
		super(receiver);
		this.dispatcher = dispatcher;
		this.method = method;
		this.args = args;
	}

	public IArgs getArgs() {
		return args;
	}

	public INotificationListener getDispatcher() {
		return dispatcher;
	}

	@Override
	public EventType getEventType() {
		return ID;
	}

	public IMethod getMethod() {
		return method;
	}

	public Object getReceiver() {
		return getSource();
	}

	public Object getResult() {
		return result;
	}

	public Object resume() throws MethodInvocationException {
		return ObjectModelTools.invokeInterceptResume(getReceiver(),
				getDispatcher(), getMethod(), getArgs());
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
