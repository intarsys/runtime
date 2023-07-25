package de.intarsys.tools.state;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.string.StringTools;

/**
 * A collection of concurrent {@link IState} instances.
 * 
 */
public class StateVector extends CommonState {

	private final List<ConcurrentState> states = new ArrayList<>();

	protected void addState(ConcurrentState entry) {
		states.add(entry);
	}

	@Override
	public IState attach(IStateHolder stateHolder) {
		StateVector result = new StateVector();
		for (ConcurrentState entry : states) {
			result.addState((ConcurrentState) entry.attach(stateHolder));
		}
		return result;
	}

	@Override
	public void enterState(IState state) {
		// @todo
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		StringBuilder sb = new StringBuilder();
		if (states.size() == 1) {
			IState tempState = states.get(0);
			sb.append(tempState.getId());
		} else {
			sb.append("{");
			for (ConcurrentState entry : states) {
				sb.append(entry.getId());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			sb.append("}");
		}
		return sb.toString();
	}

	protected String createLabel(IMessageBundle messageBundle, List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (ConcurrentState entry : states) {
			String label = entry.createLabel(messageBundle, new ArrayList<>(path));
			if (!StringTools.isEmpty(label)) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(label);
			}
		}
		return sb.toString();
	}

	@Override
	protected String getPrefix() {
		return "(v)";
	}

	@Override
	public IState getRootState() {
		return this;
	}

	@Override
	public IState getState() {
		return null;
	}

	protected ConcurrentState getStateEntry(Object context) {
		for (ConcurrentState state : states) {
			if (state.getContext() == context) {
				return state;
			}
		}
		return null;
	}

	public List<ConcurrentState> getStates() {
		return states;
	}

	@Override
	public boolean isAncestorOf(IState state) {
		return false;
	}

	public boolean isEmpty() {
		return states.isEmpty();
	}

	@Override
	public boolean isFinal() {
		for (ConcurrentState state : states) {
			if (!state.isFinal()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isInitial() {
		for (ConcurrentState state : states) {
			if (!state.isInitial()) {
				return false;
			}
		}
		return true;
	}

	public IState lookupState(Object context) {
		ConcurrentState state = getStateEntry(context);
		if (state == null) {
			return null;
		}
		return state.getContextState();
	}

	public StateVector putState(ConcurrentState state) {
		StateVector newState = new StateVector();
		boolean newContext = true;
		for (ConcurrentState entry : states) {
			if (entry.getContext() == state.getContext()) {
				newContext = false;
				if (state.getContextState() != AtomicState.DISPOSED) {
					newState.addState(state);
				}
			} else {
				newState.addState(entry);
			}
		}
		if (state.getContextState() != AtomicState.DISPOSED) {
			if (newContext) {
				newState.addState(state);
			}
		}
		return newState;
	}

	public CommonState unwrap() {
		if (states.size() == 1) {
			return (CommonState) states.get(0).getContextState();
		}
		return this;
	}
}
