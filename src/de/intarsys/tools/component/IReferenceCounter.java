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
package de.intarsys.tools.component;

import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.DestroyedEvent;
import de.intarsys.tools.event.INotificationSupport;

/**
 * An object that is aware of its referents.
 * <p>
 * The object implements a reference counting mechanism that should behave like
 * this:
 * 
 * <ul>
 * <li>the object should keep a counter that increments when acquired and
 * decrements when released</li>
 * <li>upon creation the object should be acquired by the constructor or factory
 * method.</li>
 * <li>if the client code is not aware if a new object is created (or taken from
 * a registry for example), the object published by the factory method should
 * always be acquired.</li>
 * <li>the object should not accept method calls before it is acquired</li>
 * <li>when the counter is zero after release, the object should no longer
 * accept any calls</li>
 * </ul>
 * <p>
 * If combined with the {@link INotificationSupport}, the object MAY trigger an
 * {@link AttributeChangedEvent} upon acquire/release. In this case the
 * <code>ATTR_REFERENCECOUNT</code> should be used to indicate the attribute.
 * <p>
 * If combined with the {@link INotificationSupport}, the object MAY trigger a
 * {@link DestroyedEvent} when the reference count reaches 0.
 */
public interface IReferenceCounter {

	public static final Attribute ATTR_REFERENCECOUNT = new Attribute(
			"referenceCount");

	/**
	 * Indicate usage of the object. If valid, the reference count should
	 * increase and the method returns an opaque handle representing the
	 * "usage claim". This handle must be returned upon calling "release".
	 * 
	 * @return An opaque usage token
	 */
	public Object acquire();

	/**
	 * The current "usage level". This is the number of unreleased
	 * "usage claims" via acquire.
	 * 
	 * @return The current "usage level".
	 */
	public int getReferenceCount();

	/**
	 * Old style release method
	 * 
	 * @deprecated use release(handle)
	 */
	@Deprecated
	public void release();

	/**
	 * The object is no longer needed. The reference count is decreased and the
	 * usage marked with the opaque handle is disposed.
	 * 
	 * @param handle
	 */
	public void release(Object handle);
}
