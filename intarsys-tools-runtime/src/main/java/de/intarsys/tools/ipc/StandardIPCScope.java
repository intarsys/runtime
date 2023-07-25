/*
 * Copyright (c) 2012, intarsys GmbH
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

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import de.intarsys.tools.component.IDisposable;

/**
 * The heap of available {@link IPCHandle} instances.
 * <p>
 * 
 */
public class StandardIPCScope implements IIPCScope, IDisposable {

	private static int Counter;
	private final Map<String, IPCHandle> handlesByKey = new HashMap<>();
	private final Map<Object, IPCHandle> handlesByObject = new IdentityHashMap<>();
	private final int id;
	private final Object lock = new Object();

	public StandardIPCScope() {
		id = Counter++;
	}

	protected IPCHandle createHandle(Object object) {
		IPCHandle handle = new IPCHandle(this, object, createId());
		return handle;
	}

	protected IPCHandle createHandle(String id) {
		IPCHandle handle = new IPCHandle(this, id);
		return handle;
	}

	protected String createId() {
		return "ipc://resolve/" + UUID.randomUUID().toString();
	}

	@Override
	public void dispose() {
		synchronized (lock) {
			handlesByKey.clear();
			handlesByObject.clear();
		}
	}

	@Override
	public IPCHandle exportObject(Object object) {
		synchronized (lock) {
			return handlesByObject.computeIfAbsent(object, (key) -> {
				IPCHandle handle = createHandle(object);
				handlesByKey.put(handle.getId(), handle);
				return handle;
			});
		}
	}

	public int getId() {
		return id;
	}

	@Override
	public IPCHandle importHandle(String id) {
		synchronized (lock) {
			return handlesByKey.computeIfAbsent(id, (key) -> createHandle(id));
		}
	}

	protected void resolve(IPCHandle handle) {
		synchronized (lock) {
			handlesByObject.put(handle.getObject(), handle);
		}
	}

	@Override
	public int size() {
		synchronized (lock) {
			return handlesByKey.size();
		}
	}

}
