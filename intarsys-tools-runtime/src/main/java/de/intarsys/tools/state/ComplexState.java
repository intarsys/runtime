package de.intarsys.tools.state;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.IMessageBundleSupport;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.string.StringTools;

/**
 * The {@link IState} workhorse. This implementations handles compound and
 * concurrent state representations. This implementation is prepared for
 * handling update propagation on behalf of the object where it is plugged in.
 * 
 */
public class ComplexState extends CommonState implements IPresentationSupport, IMessageBundleSupport {

	static class UnassignedHolder implements IStateHolder {

		@Override
		public void enterState(IState state) {
			//
		}

		@Override
		public IState getState() {
			return null;
		}

		@Override
		public String toString() {
			return "<unassigned>";
		}
	}

	protected static final IStateHolder UNASSIGNED = new UnassignedHolder();

	protected static ComplexState create(IState rootState, StateVector subStates) {
		ComplexState newState = new ComplexState(rootState, subStates);
		return newState;
	}

	protected static ComplexState wrap(IState state) {
		if (state == null) {
			return null;
		}
		if (!(state instanceof ComplexState)) {
			return ComplexState.create(state, null);
		}
		return (ComplexState) state;
	}

	private final IState rootState;

	private final StateVector stateVector;

	private IStateHolder stateHolder = UNASSIGNED;

	private String label;

	protected ComplexState(IState rootState, StateVector subStates) {
		super();
		if (rootState == null) {
			throw new NullPointerException();
		}
		this.rootState = rootState;
		if (subStates != null) {
			this.stateVector = (StateVector) subStates.attach(this);
		} else {
			this.stateVector = null;
		}
	}

	@Override
	public final IState attach(IStateHolder stateHolder) {
		if (stateHolder == null) {
			this.stateHolder = stateHolder;
			return null;
		}
		if (this.stateHolder == UNASSIGNED) {
			this.stateHolder = stateHolder;
			return this;
		}
		ComplexState result = new ComplexState(rootState, stateVector);
		return result.attach(stateHolder);
	}

	protected String createLabel() {
		IMessageBundle bundle = getMessageBundle();
		if (bundle == null) {
			return "{" + getId() + "}";
		}
		return createLabel(bundle, new ArrayList<>());
	}

	protected String createLabel(IMessageBundle bundle, List<String> path) {
		path.add(rootState.getId());
		if (stateVector == null) {
			String pattern = getLabelPattern(bundle, new ArrayList<>(path));
			if (pattern == null) {
				String id = StringTools.join(path, "/");
				return bundle.getString(id);
			}
			return bundle.format(pattern);
		} else {
			return stateVector.createLabel(bundle, new ArrayList<>(path));
		}
	}

	@Override
	public void enterState(IState pState) {
		if (pState == null) {
			throw new NullPointerException();
		}
		if (!(pState instanceof CommonState)) {
			throw new IllegalArgumentException();
		}
		ConcurrentState cState = ConcurrentState.create((CommonState) pState);
		StateVector newSubStates = null;
		if (stateVector == null) {
			newSubStates = new StateVector();
			if (cState.getContextState() != AtomicState.DISPOSED) {
				newSubStates.addState(cState);
			}
		} else {
			newSubStates = stateVector.putState(cState);
		}
		if (newSubStates.isEmpty()) {
			newSubStates = null;
		}
		ComplexState newState = new ComplexState(getRootState(), newSubStates);
		getStateHolder().enterState(newState);
	}

	@Override
	public String getDescription() {
		return getTip();
	}

	@Override
	public String getIconName() {
		return null;
	}

	@Override
	public String getId() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRootState().getId());
		if (stateVector != null && !stateVector.isEmpty()) {
			sb.append("/");
			sb.append(stateVector.getId());
		}
		return sb.toString();
	}

	@Override
	public String getLabel() {
		if (label == null) {
			label = createLabel();
		}
		return label;
	}

	protected String getLabelPattern(IMessageBundle bundle, List<String> path) {
		String pattern = null;
		while (true) {
			String id = StringTools.join(path, "/");
			pattern = bundle.getPattern("state." + id + ".label"); //$NON-NLS-1$ //$NON-NLS-2$
			if (pattern != null) {
				return pattern;
			}
			if (path.size() == 1) {
				return null;
			}
			path.remove(path.size() - 1);
		}
	}

	@Override
	public IMessageBundle getMessageBundle() {
		if (getStateHolder() instanceof IMessageBundleSupport) {
			return ((IMessageBundleSupport) getStateHolder()).getMessageBundle();
		}
		return null;
	}

	@Override
	protected String getPrefix() {
		if (getStateHolder() == UNASSIGNED) {
			return "(x-)";
		} else {
			return "(x)";
		}
	}

	@Override
	public IState getRootState() {
		return rootState;
	}

	@Override
	public IState getState() {
		if (stateVector == null) {
			return null;
		}
		return stateVector.unwrap();
	}

	public IStateHolder getStateHolder() {
		return stateHolder;
	}

	protected StateVector getStateVector() {
		return stateVector;
	}

	@Override
	public String getTip() {
		return getLabel();
	}

	@Override
	public boolean isAncestorOf(IState state) {
		return false;
	}

	@Override
	public boolean isFinal() {
		return getRootState().isFinal();
	}

	@Override
	public boolean isInitial() {
		return getRootState().isInitial();
	}

}
