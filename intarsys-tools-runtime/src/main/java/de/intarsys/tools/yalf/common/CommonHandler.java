/*
 * Copyright (c) 2014, intarsys GmbH
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
package de.intarsys.tools.yalf.common;

import java.util.Objects;

import de.intarsys.tools.yalf.api.IFilter;
import de.intarsys.tools.yalf.api.IHandler;

/**
 * A common superclass for implementing {@link IHandler}.
 * 
 */
public abstract class CommonHandler<I, R> implements IHandler<R> {

	private I implementation;

	private IFilter<R> filter;

	protected CommonHandler() {
	}

	protected CommonHandler(I implementation) {
		super();
		Objects.requireNonNull(implementation, "the handler cannot be null");
		this.implementation = implementation;
	}

	protected abstract void basicPublish(R event);

	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	public IFilter<R> getFilter() {
		return filter;
	}

	public I getImplementation() {
		return implementation;
	}

	@Override
	public final void publish(R event) {
		if (getFilter() != null) {
			if (!getFilter().isLoggable(event)) {
				return;
			}
		}
		basicPublish(event);
	}

	public void setFilter(IFilter<R> filter) {
		this.filter = filter;
	}

	public void setImplementation(I implementation) {
		this.implementation = implementation;
	}

}
