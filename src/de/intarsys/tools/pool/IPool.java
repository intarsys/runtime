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

/**
 * A pool of objects.
 * 
 */
public interface IPool {

	/**
	 * Add an object to the pool.
	 * 
	 * @param object
	 *            The object to be added to the pool.
	 * @throws Exception
	 */
	public void checkin(Object object) throws Exception;

	/**
	 * Get an object from the pool.
	 * <p>
	 * This may be a reused object or a new one, up to the pool strategy and
	 * size.
	 * 
	 * @param timeout
	 *            The maximum time to wait for an instance to be available in
	 *            milliseconds. -1 will wait indefinitely, 0 will not wait.
	 * 
	 * @return A new object from the pool.
	 * @throws Exception
	 */
	public Object checkout(long timeout) throws Exception;

	/**
	 * Close the pool.
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception;

	/**
	 * Destroy an object previously allocated from the pool.
	 * 
	 * @param object
	 *            The object to be destroyed.
	 * @throws Exception
	 */
	public void destroy(Object object) throws Exception;
}
