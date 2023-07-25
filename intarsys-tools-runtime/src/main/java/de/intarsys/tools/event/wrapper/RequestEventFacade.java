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

import de.intarsys.tools.event.EventType;

/**
 * 
 */
public class RequestEventFacade extends AbstractEventFacade {

	public RequestEventFacade(RequestEvent event) {
		super(event);
	}

	public void consume() {
		((RequestEvent) object).consume();
	}

	public Object getArgs() {
		return ((RequestEvent) object).getArgs();
	}

	public EventType getEventType() {
		return ((RequestEvent) object).getEventType();
	}

	public String getName() {
		return ((RequestEvent) object).getName();
	}

	@Override
	public boolean getRc() {
		return ((RequestEvent) object).getRc();
	}

	@Override
	public Object getSource() {
		return createFacade(((RequestEvent) object).getSource());
	}

	@Override
	public Object getTarget() {
		return createFacade(((RequestEvent) object).getTarget());
	}

	@Override
	public String getType() {
		return ((RequestEvent) object).getEventType().getName();
	}

	@Override
	public Object getValue() {
		return createFacade(((RequestEvent) object).getValue());
	}

	@Override
	public boolean getVeto() {
		return ((RequestEvent) object).isVetoed();
	}

	public boolean isConsumed() {
		return ((RequestEvent) object).isConsumed();
	}

	public boolean isVetoed() {
		return ((RequestEvent) object).isVetoed();
	}

	@Override
	public void setRc(boolean value) {
		((RequestEvent) object).setRc(value);
	}

	public void setTarget(Object target) {
		((RequestEvent) object).setTarget(target);
	}

	@Override
	public void setValue(Object value) {
		((RequestEvent) object).setValue(value);
	}

	@Override
	public void setVeto(boolean value) {
		((RequestEvent) object).setVeto(value);
	}

	public void veto() {
		((RequestEvent) object).veto();
	}
}
