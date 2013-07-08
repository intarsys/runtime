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
 * All exported/imported objects for the IPC mechanism are held and looked up in
 * the {@link IIPCScope}.
 * <p>
 * An {@link IIPCScope} can be looked up via the {@link IPCScope} singleton.
 * <p>
 * An {@link IIPCScope} is provided (and removed) in the {@link IPCScope}
 * singleton for example by the service container framework. This way the
 * {@link IIPCScope} is confidentially contained within the session.
 * 
 * 
 */
public interface IIPCScope {

	/**
	 * Make a VM local POJO addressable in the {@link IIPCScope}. The handle
	 * returned is the unique representation for the object and may be used for
	 * interprocess communication. A marshaling layer may send the handle id and
	 * later on retrieve the object using the handle id.
	 * 
	 * @param object
	 * @return
	 */
	public IPCHandle exportObject(Object object);

	/**
	 * Make an id retrieved from some marshaling layer available as a handle to
	 * the remote system in the local VM.
	 * <p>
	 * Immediately after import, "getObject" will return null for the
	 * {@link IPCHandle} returned. A higher level marshaling may replace this
	 * with a more full blown local representation, thereby allowing object
	 * identity for the remote.
	 * 
	 * @param id
	 * @return
	 */
	public IPCHandle importHandle(String id);

	/**
	 * The number of objects in the scope.
	 * 
	 * @return The number of objects in the scope.
	 */
	public int size();

}
