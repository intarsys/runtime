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
package de.intarsys.tools.event;

/**
 * An event representing an objects state change.
 */
public class AttributeChangedEvent extends Event {
	public static final EventType ID = new EventType(
			AttributeChangedEvent.class.getName());

	private Object attribute;

	private Object oldValue;

	private Object newValue;

	public AttributeChangedEvent(Object source, Object attribute,
			Object oldValue, Object newValue) {
		super(source);
		this.attribute = attribute;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * The attribute slot that has changed.
	 * 
	 * @return The attribute slot that has changed.
	 */
	public Object getAttribute() {
		return attribute;
	}

	@Override
	public EventType getEventType() {
		return ID;
	}

	/**
	 * The new value of the attribute.
	 * 
	 * @return The new value of the attribute.
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * The previous value of the attribute.
	 * 
	 * @return The previous value of the attribute.
	 */
	public Object getOldValue() {
		return oldValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("attribute '");
		sb.append(attribute);
		sb.append("' changed on '");
		sb.append(getSource());
		sb.append("'");
		return sb.toString();
	}
}
