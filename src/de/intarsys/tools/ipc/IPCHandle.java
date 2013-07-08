/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.tools.ipc;

/**
 * The representation of an object that lives remote to the local VM.
 * <p>
 * {@link IPCHandle} instances are managed by an {@link IIPCScope}.
 * 
 */
public class IPCHandle {

	final private String id;

	final private StandardIPCScope scope;

	private Object object;

	private static final Object UNDEFINED = new Object();

	protected IPCHandle(StandardIPCScope heap, Object object, String id) {
		super();
		this.scope = heap;
		this.object = object;
		this.id = id;
	}

	protected IPCHandle(StandardIPCScope heap, String id) {
		super();
		this.scope = heap;
		this.object = UNDEFINED;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Object getObject() {
		return object;
	}

	protected StandardIPCScope getScope() {
		return scope;
	}

	public boolean isResolved() {
		return object != UNDEFINED;
	}

	protected void resolve(Object pObject) {
		if (object != UNDEFINED) {
			throw new IllegalStateException("IPC handle is already resolved");
		}
		this.object = pObject;
		getScope().resolve(this);
	}

	@Override
	public String toString() {
		return getId();
	}

}
