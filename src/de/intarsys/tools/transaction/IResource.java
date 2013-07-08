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
package de.intarsys.tools.transaction;

/**
 * A resource involved in a transactional task.
 * <p>
 * 
 */
public interface IResource {

	/**
	 * begin is performed upon insertion of a resource in an active
	 * {@link ITransaction} or upon start of an {@link ITransaction} already
	 * containing the {@link IResource}.
	 * 
	 * @throws ResourceException
	 */
	public void begin() throws ResourceException;

	/**
	 * commit is performed when the {@link ITransaction} containing the
	 * {@link IResource} is committed.
	 * 
	 * @throws ResourceException
	 */
	public void commit() throws ResourceException;

	/**
	 * If this resource supports hierarchies, the optional parent is returned.
	 * 
	 * @return The optional parent resource.
	 */
	public IResource getParent();

	/**
	 * The type that created this resource.
	 * 
	 * @return The resource type.
	 */
	public IResourceType getType();

	/**
	 * resume is performed when the {@link ITransaction} containing the
	 * {@link IResource} is resumed.
	 * 
	 * @throws ResourceException
	 */
	public void resume();

	/**
	 * rollback is performed when the {@link ITransaction} containing the
	 * {@link IResource} is rolled back.
	 * 
	 * @throws ResourceException
	 */
	public void rollback() throws ResourceException;

	/**
	 * suspend is performed when the {@link ITransaction} containing the
	 * {@link IResource} is suspended.
	 * 
	 * @throws ResourceException
	 */
	public void suspend();
}
