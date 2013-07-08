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

import java.util.EventObject;

/**
 * Abstract superclass for event implementations.
 * 
 */
abstract public class Event extends EventObject {

	public static final Object ID_ALL = new Object();

	public static final EventType ID = new EventType("Event");

	/** Flag if the event is already handled. */
	protected boolean consumed = false;

	/**
	 * Flag if execution of the action that is announced by the event is
	 * interrupted by one of the listeners.
	 */
	protected boolean veto = false;

	/**
	 * Create a new event object.
	 * 
	 * @param source
	 *            The object that created the event.
	 */
	public Event(Object source) {
		super(source);
	}

	public void consume() {
		consumed = true;
	}

	abstract public EventType getEventType();

	public String getName() {
		return getEventType().getName();
	}

	public boolean getRc() {
		return !isVetoed();
	}

	public boolean isConsumed() {
		return consumed;
	}

	public boolean isVetoed() {
		return veto;
	}

	public void setRc(boolean value) {
		setVeto(!value);
	}

	public void setVeto(boolean value) {
		veto = value;
	}

	public void veto() {
		veto = true;
	}
}
