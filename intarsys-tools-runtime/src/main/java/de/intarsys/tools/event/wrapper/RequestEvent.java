/*
 * Copyright (c) 2007, intarsys GmbH
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
package de.intarsys.tools.event.wrapper;

import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.facade.IFacade;
import de.intarsys.tools.facade.IFacadeSupport;
import de.intarsys.tools.string.StringTools;

/**
 * 
 */
public class RequestEvent extends Event implements IRequestEvent, IFacadeSupport {

	public static final EventType TYPE = new EventType("Generic"); //$NON-NLS-1$

	private transient EventType eventType;

	private String name;

	private transient Object target;

	private transient Object value;

	private transient Object args;

	private transient IFacade facade;

	public RequestEvent(Object source) {
		this(source, TYPE, StringTools.EMPTY);
	}

	/**
	 * @param source
	 * @param type
	 * @param name
	 */
	public RequestEvent(Object source, EventType type, String name) {
		super(source);
		this.eventType = type;
		this.name = name;
	}

	@Override
	public IFacade createFacade() {
		if (facade == null) {
			facade = new RequestEventFacade(this);
		}
		return facade;
	}

	public Object getArgs() {
		return args;
	}

	@Override
	public EventType getEventType() {
		return eventType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setArgs(Object args) {
		this.args = args;
	}

	@Override
	public void setRc(boolean value) {
		super.setRc(value);
		setValue(Boolean.valueOf(value));
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}
}
