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

import java.io.Serializable;

/**
 * A common implementation for the {@link IResource} abstraction.
 * 
 * @param <R>
 * @param <T>
 */
abstract public class CommonResource<R extends IResource, T extends IResourceType<R>>
		implements IResource, Serializable {

	final private T type;

	final private R parent;

	private boolean active = false;

	private static int Counter = 0;

	public static int getCounter() {
		return Counter;
	}

	public static void setCounter(int counter) {
		Counter = counter;
	}

	final private int id = Counter++;

	protected CommonResource(T type, R parent) {
		super();
		this.type = type;
		this.parent = parent;
	}

	public void begin() throws ResourceException {
		setActive(true);
	}

	public void commit() throws ResourceException {
	}

	public int getId() {
		return id;
	}

	public R getParent() {
		return parent;
	}

	public T getType() {
		return type;
	}

	protected boolean isActive() {
		return active;
	}

	public void resume() {
		setActive(true);
	}

	public void rollback() throws ResourceException {
	}

	protected void setActive(boolean active) {
		this.active = active;
	}

	public void suspend() {
		setActive(false);
	}

}
