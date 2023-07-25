/*
 * Copyright (c) 2014, intarsys GmbH
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - Neither the name of intarsys nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific
 * prior written permission.
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
package de.intarsys.tools.state;

/**
 * A state representation for a stateful object. This representation already
 * includes the concept of "compound states", therefore one can query the "root"
 * state part, in addition any state acts as an {@link IStateHolder}.
 * 
 */
public interface IState extends IStateHolder {

	public static final String ATTR_STATE = "state"; //$NON-NLS-1$

	/**
	 * A unique id for the state.
	 * 
	 * This may be a "basic" state, like "new" or "active". Compound states are
	 * expressed using a path like "active/reading". Concurrent states show up
	 * like "active/{reading,writing}".
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * The root state of the receiver. For a simple state (like "new") this is
	 * the receiver itself. Compound states will return the first element in
	 * their state path.
	 * 
	 * @return
	 */
	public IState getRootState();

	/**
	 * True if the receiver is some nested state below "state".
	 * 
	 * @param state
	 * @return
	 */
	public boolean isAncestorOf(IState state);

	/**
	 * True if this is a final state.
	 * 
	 * @return
	 */
	public boolean isFinal();

	/**
	 * True if this is an initial state.
	 * 
	 * @return
	 */
	public boolean isInitial();

}
