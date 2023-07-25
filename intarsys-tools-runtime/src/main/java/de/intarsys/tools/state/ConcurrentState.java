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

import java.util.List;

import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;

/**
 * A {@link IState} wrapper that adds context information for identifying
 * concurrent states. This is used within {@link StateVector} and to signal
 * concurrent state entry in client code calling
 * {@link IState#enterState(IState)}.
 * 
 * The IState contained is handled completely transparently.
 */
public class ConcurrentState extends CommonState {

	class ConcurrentStateHolder implements IStateHolder, IMessageBundleSupport {

		private final IStateHolder stateHolder;

		public ConcurrentStateHolder(IStateHolder stateHolder) {
			super();
			this.stateHolder = stateHolder;
		}

		@Override
		public void enterState(IState ppState) {
			ConcurrentState result = new ConcurrentState(getContext(), (CommonState) ppState);
			getStateHolder().enterState(result);
		}

		@Override
		public IMessageBundle getMessageBundle() {
			if (getStateHolder() instanceof IMessageBundleSupport) {
				return ((IMessageBundleSupport) getStateHolder()).getMessageBundle();
			}
			return null;
		}

		@Override
		public IState getState() {
			return getStateHolder().getState();
		}

		public IStateHolder getStateHolder() {
			return stateHolder;
		}
	}

	public static final String DEFAULT_CONTEXT = "default";

	public static ConcurrentState create(CommonState state) {
		if (state instanceof ConcurrentState) {
			return (ConcurrentState) state;
		}
		return create(ConcurrentState.DEFAULT_CONTEXT, state);
	}

	public static ConcurrentState create(Object context, CommonState state) {
		return new ConcurrentState(context, state);
	}

	public static ConcurrentState create(Object context, String id) {
		return new ConcurrentState(context, AtomicState.create(id));
	}

	public static ConcurrentState createDisposed(Object context) {
		return new ConcurrentState(context, AtomicState.DISPOSED);
	}

	public static ConcurrentState createFinal(Object context, String id) {
		return new ConcurrentState(context, AtomicState.createFinal(id));
	}

	public static ConcurrentState createInitial(Object context, String id) {
		return new ConcurrentState(context, AtomicState.createInitial(id));
	}

	private final CommonState contextState;

	private final Object context;

	protected ConcurrentState(Object context, CommonState pState) {
		super();
		this.context = context;
		this.contextState = pState;
	}

	@Override
	public IState attach(IStateHolder stateHolder) {
		return new ConcurrentState(context, (CommonState) contextState.attach(new ConcurrentStateHolder(stateHolder)));
	}

	protected String createLabel(IMessageBundle messageBundle, List<String> path) {
		if (contextState instanceof ComplexState) {
			return ((ComplexState) contextState).createLabel(messageBundle, path);
		}
		return getId();
	}

	@Override
	public void enterState(IState state) {
		contextState.enterState(state);
	}

	public Object getContext() {
		return context;
	}

	public IState getContextState() {
		return contextState;
	}

	@Override
	public String getId() {
		return contextState.getId();
	}

	@Override
	protected String getPrefix() {
		return "(c)"; //$NON-NLS-1$
	}

	@Override
	public IState getRootState() {
		return contextState.getRootState();
	}

	@Override
	public IState getState() {
		return contextState.getState();
	}

	@Override
	public boolean isAncestorOf(IState state) {
		return state.isAncestorOf(state);
	}

	@Override
	public boolean isFinal() {
		return contextState.isFinal();
	}

	@Override
	public boolean isInitial() {
		return contextState.isInitial();
	}

}
