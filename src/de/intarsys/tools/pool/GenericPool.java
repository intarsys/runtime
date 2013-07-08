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
package de.intarsys.tools.pool;

import java.util.Stack;

/**
 * A very simple implementation of a generic {@link IPool}.
 * 
 */
public class GenericPool implements IPool {

	final private IPoolObjectFactory objectFactory;

	final private Stack<Object> objects;

	private boolean closed;

	public GenericPool(IPoolObjectFactory objectFactory) {
		super();
		this.objectFactory = objectFactory;
		this.objects = new Stack<Object>();
		this.closed = false;
	}

	public void checkin(Object object) throws Exception {
		deactivateObject(object);
		synchronized (this) {
			assertOpen();
			objects.push(object);
		}
	}

	protected void deactivateObject(Object object) throws Exception {
		if (objectFactory == null) {
			return;
		}
		objectFactory.deactivateObject(object);
	}

	protected void destroyObject(Object object) throws Exception {
		if (objectFactory == null) {
			return;
		}
		objectFactory.destroyObject(object);
	}

	protected boolean isClosed() {
		return closed;
	}

	protected void assertOpen() {
		if (closed) {
			throw new IllegalStateException("pool closed");
		}
	}

	public Object checkout(long timeout) throws Exception {
		Object result = null;
		synchronized (this) {
			assertOpen();
			if (objects.isEmpty()) {
				result = createObject();
			} else {
				result = objects.pop();
			}
		}
		activateObject(result);
		return result;
	}

	protected void activateObject(Object object) throws Exception {
		if (objectFactory == null) {
			return;
		}
		objectFactory.activateObject(object);
	}

	protected Object createObject() throws Exception {
		if (objectFactory == null) {
			throw new IllegalStateException("can not create new object");
		}
		Object result = objectFactory.createObject();
		if (result == null) {
			throw new IllegalStateException("new object can't be null");
		}
		return result;
	}

	public void close() throws Exception {
		closed = true;
	}

	public void destroy(Object object) throws Exception {
		destroyObject(object);
	}
}
