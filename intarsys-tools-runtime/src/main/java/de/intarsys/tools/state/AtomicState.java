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
 * An {@link IState} implementation modeling an atomic (non compound) state.
 * 
 */
public class AtomicState extends CommonState {

	public static final AtomicState NONE = AtomicState.createInitial("none"); //$NON-NLS-1$

	public static final AtomicState DISPOSED = AtomicState.createFinal("disposed"); //$NON-NLS-1$

	public static final AtomicState NEW = AtomicState.createInitial("new"); //$NON-NLS-1$

	public static final AtomicState ACTIVE = AtomicState.createInitial("active"); //$NON-NLS-1$

	public static final AtomicState OK = AtomicState.createFinal("ok"); //$NON-NLS-1$

	public static final AtomicState CANCELLED = AtomicState.createFinal("cancelled"); //$NON-NLS-1$

	public static final AtomicState FAILED = AtomicState.createFinal("failed"); //$NON-NLS-1$

	protected static final byte T_INITIAL = 0;

	protected static final byte T_OTHER = 1;

	protected static final byte T_FINAL = (byte) 255;

	public static AtomicState create(String id) {
		checkID(id);
		return new AtomicState(id, T_OTHER);
	}

	public static AtomicState createFinal(String id) {
		checkID(id);
		return new AtomicState(id, T_FINAL);
	}

	public static AtomicState createInitial(String id) {
		checkID(id);
		return new AtomicState(id, T_INITIAL);
	}

	private final String id;

	private final byte type;

	protected AtomicState(String id, byte type) {
		super();
		this.id = id;
		this.type = type;
	}

	@Override
	public IState attach(IStateHolder stateHolder) {
		return ComplexState.create(this, null).attach(stateHolder);
	}

	@Override
	public void enterState(IState state) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	protected String getPrefix() {
		return "(a)"; //$NON-NLS-1$
	}

	@Override
	public IState getRootState() {
		return this;
	}

	@Override
	public IState getState() {
		return null;
	}

	@Override
	public boolean isAncestorOf(IState state) {
		return this == state.getRootState();
	}

	@Override
	public boolean isFinal() {
		return type == T_FINAL;
	}

	@Override
	public boolean isInitial() {
		return type == T_INITIAL;
	}

}
