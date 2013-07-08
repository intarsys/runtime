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
package de.intarsys.tools.adapter;

/**
 * An object that is able to be adapted to another type.
 * <p>
 * This can be interpreted as a "dynamic" cast to a type that is not statically
 * declared for the receiver. This pattern allows more freedom in layered /
 * component oriented architectures, as the receiver object is not forced to
 * implement a certain interface at compile time. Moreover, the "instanceof"
 * predicate can be implemented on a per instance base.
 * <p>
 * A generic implementation of this method could use the {@link IAdapterOutlet}
 * singleton to delegate adapter creation to a registered
 * {@link IAdapterFactory}.
 * 
 * <pre>
 * public &lt;T&gt; T getAdapter(Class&lt;T&gt; clazz) {
 * 	return AdapterOutlet.get().getAdapter(this, clazz);
 * }
 * </pre>
 */
public interface IAdapterSupport {

	/**
	 * Return an object of type <code>clazz</code> that represents the receiver.
	 * <p>
	 * This method should return <code>null</code> if adaption is not possible.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return Return an object of type <code>clazz</code> that represents the
	 *         receiver.
	 */
	public <T> T getAdapter(Class<T> clazz);
}
