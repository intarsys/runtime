/*
 * Copyright (c) 2012, intarsys consulting GmbH
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
package de.intarsys.claptz.impl;

import de.intarsys.claptz.IInstrument;
import de.intarsys.claptz.IInstrumentPrerequisite;
import de.intarsys.claptz.State;

/**
 * The declaration of a prerequisite relationship between instruments.
 * <p>
 * This relationship defines a dependency in time and namespace. A dependent
 * instrument is always loaded after the prerequisiste and the dependent
 * instrument uses the prerequisite instruments classloader as its parent class
 * loader. This means a dependent instrument sees all classes and resources of
 * the prerequisite, but not vice versa.
 */
public class StandardInstrumentPrerequisite {

	/**
	 * The public portion of the InstrumentPrerequisite, which itself is never
	 * leaked to client code.
	 * 
	 */
	public class Facade implements IInstrumentPrerequisite {
		public String getAbsentAction() {
			return StandardInstrumentPrerequisite.this.getAbsentAction();
		}

		public IInstrument getInstrument() {
			StandardInstrument tempInstrument = StandardInstrumentPrerequisite.this
					.getInstrument();
			return tempInstrument == null ? null : tempInstrument.getFacade();
		}

		public String getInstrumentId() {
			return StandardInstrumentPrerequisite.this.getInstrumentId();
		}

		protected StandardInstrumentPrerequisite getOwner() {
			return StandardInstrumentPrerequisite.this;
		}

	}

	private String ifdef;

	private String ifnotdef;

	private String absentAction = null;

	private StandardInstrument dependentInstrument;

	private IInstrumentPrerequisite facade = new Facade();

	private String prerequisiteId;

	private StandardInstrument prerequisiteInstrument;

	private State state;

	public StandardInstrumentPrerequisite(StandardInstrument dependent) {
		super();
		this.dependentInstrument = dependent;
	}

	public String getAbsentAction() {
		return absentAction;
	}

	public IInstrumentPrerequisite getFacade() {
		return facade;
	}

	public String getIfdef() {
		return ifdef;
	}

	public String getIfnotdef() {
		return ifnotdef;
	}

	protected StandardInstrument getInstrument() {
		if (prerequisiteInstrument == null) {
			prerequisiteInstrument = dependentInstrument
					.getInstrumentRegistry()
					.lookupInstrument(getInstrumentId());
		}
		return prerequisiteInstrument;
	}

	public String getInstrumentId() {
		return prerequisiteId;
	}

	protected State getState() {
		return state;
	}

	public void setAbsentAction(String absentAction) {
		this.absentAction = absentAction;
	}

	public void setIfdef(String ifdef) {
		this.ifdef = ifdef;
	}

	public void setIfnotdef(String ifnotdef) {
		this.ifnotdef = ifnotdef;
	}

	public void setInstrumentId(String prerequisiteId) {
		this.prerequisiteId = prerequisiteId;
		prerequisiteInstrument = null;
	}

	protected void setState(State state) {
		this.state = state;
	}

}
