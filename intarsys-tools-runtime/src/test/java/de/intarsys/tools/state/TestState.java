package de.intarsys.tools.state;

import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.message.IMessageBundle;
import de.intarsys.tools.message.MessageTools;
import de.intarsys.tools.message.PrefixedMessageBundle;
import de.intarsys.tools.presentation.IPresentationSupport;
import de.intarsys.tools.state.ConcurrentState.ConcurrentStateHolder;
import de.intarsys.tools.valueholder.ObjectHolder;
import junit.framework.TestCase;

public class TestState extends TestCase {

	protected void checkChain(IStateHolder holder) {
		if (holder instanceof AtomicStateHolder) {
			return;
		} else if (holder instanceof ComplexStateHolder) {
			ComplexStateHolder myHolder = (ComplexStateHolder) holder;
			StateVector states = myHolder.getStateVector();
			for (ConcurrentState state : states.getStates()) {
				ConcurrentState childState = state;
				checkChain(holder, childState.getContextState());
			}
		} else if (holder instanceof ComplexState) {
			ComplexState myHolder = (ComplexState) holder;
			StateVector states = myHolder.getStateVector();
			for (ConcurrentState state : states.getStates()) {
				ConcurrentState childState = state;
				checkChain(holder, childState);
			}
		}
	}

	protected void checkChain(IStateHolder rootHolder, IStateHolder childHolder) {
		if (childHolder instanceof ComplexState) {
			ComplexState myState = (ComplexState) childHolder;
			ConcurrentStateHolder concurrentHolder = (ConcurrentStateHolder) (((ComplexState) childHolder)
					.getStateHolder());
			assertTrue(concurrentHolder.getStateHolder() == rootHolder);
		}
	}

	public void testAtomic() {
		Object owner = new Object();
		IStateHolder stateHolder;
		AtomicState rootState;
		//
		stateHolder = new AtomicStateHolder(owner, null);
		assertTrue(stateHolder.getState().isInitial());
		assertTrue(AtomicState.NEW == stateHolder.getState());
		assertTrue(AtomicState.NEW == stateHolder.getState().getRootState());
		assertTrue("new".equals(stateHolder.getState().getId()));
		assertTrue(null == stateHolder.getState().getState());
		checkChain(stateHolder);
		//
		stateHolder = new AtomicStateHolder(owner, null);
		stateHolder.enterState(AtomicState.OK);
		assertTrue(stateHolder.getState().isFinal());
		assertTrue(AtomicState.OK == stateHolder.getState());
		assertTrue(AtomicState.OK == stateHolder.getState().getRootState());
		assertTrue("ok".equals(stateHolder.getState().getId()));
		assertTrue(AtomicState.OK.isAncestorOf(stateHolder.getState()));
		assertTrue(null == stateHolder.getState().getState());
		checkChain(stateHolder);
		//
		rootState = AtomicState.create("root");
		stateHolder = new AtomicStateHolder(owner, null);
		stateHolder.enterState(rootState);
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		assertTrue(rootState == stateHolder.getState());
		assertTrue(rootState == stateHolder.getState().getRootState());
		assertTrue("root".equals(stateHolder.getState().getId()));
		assertTrue(null == stateHolder.getState().getState());
		checkChain(stateHolder);
		//
		rootState = AtomicState.create("root");
		stateHolder = new AtomicStateHolder(owner, null);
		stateHolder.enterState(rootState);
		try {
			stateHolder.getState().enterState(AtomicState.create("test"));
			fail("can not enter substate");
		} catch (Exception e) {
			//
		}
	}

	public void testCompound() {
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		IState sub1State;
		IState sub2State;
		IState subSubState;
		IState subSubSubState;
		//
		stateHolder = new ComplexStateHolder(owner, null);
		rootState = AtomicState.create("root");
		sub1State = AtomicState.create("sub1");
		sub2State = AtomicState.create("sub2");
		subSubState = AtomicState.create("subsub");
		subSubSubState = AtomicState.create("subsubsub");
		//
		stateHolder.enterState(rootState);
		assertTrue(rootState != stateHolder.getState());
		assertTrue(rootState.getRootState() == stateHolder.getState().getRootState());
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root".equals(stateHolder.getState().getId()));
		assertTrue(null == stateHolder.getState().getState());
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
		//
		stateHolder.getState().enterState(sub1State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/sub1".equals(stateHolder.getState().getId()));
		assertTrue(sub1State != stateHolder.getState().getState());
		assertTrue(sub1State.isAncestorOf(stateHolder.getState().getState()));
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
		//
		stateHolder.getState().getState().enterState(subSubState);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/sub1/subsub".equals(stateHolder.getState().getId()));
		assertTrue(sub1State != stateHolder.getState().getState());
		assertTrue(sub1State == stateHolder.getState().getState().getRootState());
		assertTrue(sub1State.isAncestorOf(stateHolder.getState().getState()));
		assertTrue(subSubState != stateHolder.getState().getState().getState());
		assertTrue(subSubState == stateHolder.getState().getState().getState().getRootState());
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
		//
		stateHolder.getState().getState().getState().enterState(subSubSubState);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/sub1/subsub/subsubsub".equals(stateHolder.getState().getId()));
		assertTrue(sub1State.isAncestorOf(stateHolder.getState().getState()));
		assertTrue(subSubState.isAncestorOf(stateHolder.getState().getState().getState()));
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
		//
		stateHolder.getState().enterState(AtomicState.DISPOSED);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root".equals(stateHolder.getState().getId()));
		assertTrue(null == stateHolder.getState().getState());
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
		//
		stateHolder.getState().enterState(sub2State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/sub2".equals(stateHolder.getState().getId()));
		assertTrue(sub2State != stateHolder.getState().getState());
		assertTrue(sub2State == stateHolder.getState().getState().getRootState());
		assertTrue(sub2State.isAncestorOf(stateHolder.getState().getState()));
		assertTrue(!stateHolder.getState().isInitial());
		assertTrue(!stateHolder.getState().isFinal());
		checkChain(stateHolder);
	}

	public void testConcurrent() {
		final ObjectHolder vh = new ObjectHolder(null);
		INotificationListener listener = new INotificationListener() {
			@Override
			public void handleEvent(Event event) {
				vh.set(event);
			}
		};
		IState oldState;
		IState newState;
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		Object c1;
		IState c1State;
		Object c1c1;
		IState c1c1State;
		Object c1c2;
		IState c1c2State;
		Object c2;
		IState c2State;
		IState c2Off;
		IState c2SubState;
		//
		stateHolder = new ComplexStateHolder(owner, listener);
		rootState = AtomicState.create("root");
		c1 = new Object();
		c1State = ConcurrentState.create(c1, "c1");
		c1c1 = new Object();
		c1c1State = ConcurrentState.create(c1c1, "c1c1");
		c1c2 = new Object();
		c1c2State = ConcurrentState.create(c1c2, "c1c2");
		c2 = new Object();
		c2State = ConcurrentState.create(c2, "c2");
		c2Off = ConcurrentState.createDisposed(c2);
		c2SubState = AtomicState.create("c2sub");
		//
		vh.set(null);
		stateHolder.enterState(rootState);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root".equals(stateHolder.getState().getId()));
		assertTrue(null == stateHolder.getState().getState());
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"new".equals(oldState.getId());
		"root".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(c1State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/c1".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root".equals(oldState.getId());
		"root/c1".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(c2State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/{c1,c2}".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/c1".equals(oldState.getId());
		"root/{c1,c2}".equals(newState.getId());
		//
		vh.set(null);
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c1State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/{c1/c1c1,c2}".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/{c1,c2}".equals(oldState.getId());
		"root/{c1/c1c1,c2}".equals(newState.getId());
		//
		vh.set(null);
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c2State);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/{c1/{c1c1,c1c2},c2}".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/{c1/c1c1,c2}".equals(oldState.getId());
		"root/{c1/{c1c1,c1c2},c2}".equals(newState.getId());
		//
		vh.set(null);
		((StateVector) stateHolder.getState().getState()).lookupState(c2).enterState(c2SubState);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/{c1/{c1c1,c1c2},c2/c2sub}".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/{c1/{c1c1,c1c2},c2}".equals(oldState.getId());
		"root/{c1/{c1c1,c1c2},c2/c2sub}".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(c2Off);
		assertTrue(rootState.isAncestorOf(stateHolder.getState()));
		assertTrue("root/c1/{c1c1,c1c2}".equals(stateHolder.getState().getId()));
		checkChain(stateHolder);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/{c1/{c1c1,c1c2},c2/c2sub}".equals(oldState.getId());
		"root/c1/{c1c1,c1c2}".equals(newState.getId());
	}

	public void testLabelComplete() {
		IState oldState;
		IState newState;
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		Object c1;
		IState c1State;
		Object c1c1;
		IState c1c1State;
		Object c1c2;
		IState c1c2State;
		Object c2;
		IState c2State;
		IState c2Off;
		IState c2SubState;
		IMessageBundle messageBundle;
		String label;
		//
		messageBundle = MessageTools.getMessageBundle(this.getClass());
		stateHolder = new ComplexStateHolder(owner, null);
		stateHolder.setMessageBundle(messageBundle);
		//
		rootState = AtomicState.create("root");
		c1 = new Object();
		c1State = ConcurrentState.create(c1, "c1");
		c1c1 = new Object();
		c1c1State = ConcurrentState.create(c1c1, "c1c1");
		c1c2 = new Object();
		c1c2State = ConcurrentState.create(c1c2, "c1c2");
		c2 = new Object();
		c2State = ConcurrentState.create(c2, "c2");
		c2Off = ConcurrentState.createDisposed(c2);
		c2SubState = AtomicState.create("c2sub");
		//
		stateHolder.enterState(rootState);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestLabel".equals(label));
		//
		stateHolder.getState().enterState(c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestC1Label".equals(label));
		//
		stateHolder.getState().enterState(c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1Label\nTestC2Label".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1C1Label\nTestC2Label".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1C1Label\nTestC1C2Label\nTestC2Label".equals(label));
	}

	public void testLabelCompletePrefix() {
		IState oldState;
		IState newState;
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		Object c1;
		IState c1State;
		Object c1c1;
		IState c1c1State;
		Object c1c2;
		IState c1c2State;
		Object c2;
		IState c2State;
		IState c2Off;
		IState c2SubState;
		IMessageBundle messageBundle;
		String label;
		//
		messageBundle = MessageTools.getMessageBundle(this.getClass());
		messageBundle = new PrefixedMessageBundle(messageBundle, "Prefix");
		stateHolder = new ComplexStateHolder(owner, null);
		stateHolder.setMessageBundle(messageBundle);
		//
		rootState = AtomicState.create("root");
		c1 = new Object();
		c1State = ConcurrentState.create(c1, "c1");
		c1c1 = new Object();
		c1c1State = ConcurrentState.create(c1c1, "c1c1");
		c1c2 = new Object();
		c1c2State = ConcurrentState.create(c1c2, "c1c2");
		c2 = new Object();
		c2State = ConcurrentState.create(c2, "c2");
		c2Off = ConcurrentState.createDisposed(c2);
		c2SubState = AtomicState.create("c2sub");
		//
		stateHolder.enterState(rootState);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestLabel".equals(label));
		//
		stateHolder.getState().enterState(c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestC1Label".equals(label));
		//
		stateHolder.getState().enterState(c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1Label\nTestC2Label".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1C1Label\nTestC2Label".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1C1Label\nTestC1C2Label\nTestC2Label".equals(label));
	}

	public void testLabelNone() {
		IState oldState;
		IState newState;
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		Object c1;
		IState c1State;
		Object c1c1;
		IState c1c1State;
		Object c1c2;
		IState c1c2State;
		Object c2;
		IState c2State;
		IState c2Off;
		IState c2SubState;
		IMessageBundle messageBundle;
		String label;
		//
		messageBundle = MessageTools.getMessageBundle(this.getClass());
		stateHolder = new ComplexStateHolder(owner, null);
		stateHolder.setMessageBundle(messageBundle);
		//
		rootState = AtomicState.create("rootn");
		c1 = new Object();
		c1State = ConcurrentState.create(c1, "c1n");
		c1c1 = new Object();
		c1c1State = ConcurrentState.create(c1c1, "c1c1n");
		c1c2 = new Object();
		c1c2State = ConcurrentState.create(c1c2, "c1c2n");
		c2 = new Object();
		c2State = ConcurrentState.create(c2, "c2n");
		//
		stateHolder.enterState(rootState);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("{rootn}".equals(label));
		//
		stateHolder.getState().enterState(c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("{rootn/c1n}".equals(label));
		//
		stateHolder.getState().enterState(c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "{rootn/c1n}\n{rootn/c2n}".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "{rootn/c1n/c1c1n}\n{rootn/c2n}".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "{rootn/c1n/c1c1n}\n{rootn/c1n/c1c2n}\n{rootn/c2n}".equals(label));
	}

	public void testLabelPart() {
		IState oldState;
		IState newState;
		Object owner = new Object();
		ComplexStateHolder stateHolder;
		IState rootState;
		Object c1;
		IState c1State;
		Object c1c1;
		IState c1c1State;
		Object c1c2;
		IState c1c2State;
		Object c2;
		IState c2State;
		IState c2Off;
		IState c2SubState;
		IMessageBundle messageBundle;
		String label;
		//
		messageBundle = MessageTools.getMessageBundle(this.getClass());
		stateHolder = new ComplexStateHolder(owner, null);
		stateHolder.setMessageBundle(messageBundle);
		//
		rootState = AtomicState.create("rootp");
		c1 = new Object();
		c1State = ConcurrentState.create(c1, "c1p");
		c1c1 = new Object();
		c1c1State = ConcurrentState.create(c1c1, "c1c1p");
		c1c2 = new Object();
		c1c2State = ConcurrentState.create(c1c2, "c1c2p");
		c2 = new Object();
		c2State = ConcurrentState.create(c2, "c2p");
		c2Off = ConcurrentState.createDisposed(c2);
		c2SubState = AtomicState.create("c2sub");
		//
		stateHolder.enterState(rootState);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestLabelP".equals(label));
		//
		stateHolder.getState().enterState(c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("TestC1LabelP".equals(label));
		//
		stateHolder.getState().enterState(c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1LabelP\nTestLabelP".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c1State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1LabelP\nTestLabelP".equals(label));
		//
		((StateVector) stateHolder.getState().getState()).lookupState(c1).enterState(c1c2State);
		label = ((IPresentationSupport) stateHolder.getState()).getLabel();
		assertTrue("" + label + " not expected", "TestC1LabelP\nTestC1LabelP\nTestLabelP".equals(label));
	}

	public void testUpdateCompound() {
		Object owner = new Object();
		final ObjectHolder vh = new ObjectHolder(null);
		INotificationListener listener = new INotificationListener() {
			@Override
			public void handleEvent(Event event) {
				vh.set(event);
			}
		};
		ComplexStateHolder stateHolder;
		IState rootState;
		IState sub1State;
		IState sub2State;
		IState subSubState;
		IState subSubSubState;
		IState cState;
		IState oldState;
		IState newState;
		//
		stateHolder = new ComplexStateHolder(owner, listener);
		rootState = AtomicState.create("root");
		sub1State = AtomicState.create("sub1");
		sub2State = AtomicState.create("sub2");
		subSubState = AtomicState.create("subsub");
		subSubSubState = AtomicState.create("subsubsub");
		//
		vh.set(null);
		stateHolder.enterState(rootState);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"new".equals(oldState.getId());
		"root".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(sub1State);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root".equals(oldState.getId());
		"root/sub1".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().getState().enterState(subSubState);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/sub1".equals(oldState.getId());
		"root/sub1/subsub".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().getState().getState().enterState(subSubSubState);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/sub1/subsub".equals(oldState.getId());
		"root/sub1/subsub/subsubsub".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(AtomicState.DISPOSED);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root/sub1/subsub/subsubsub".equals(oldState.getId());
		"root".equals(newState.getId());
		//
		vh.set(null);
		stateHolder.getState().enterState(sub2State);
		assertTrue(vh.get() instanceof AttributeChangedEvent);
		oldState = (IState) ((AttributeChangedEvent) vh.get()).getOldValue();
		newState = (IState) ((AttributeChangedEvent) vh.get()).getNewValue();
		"root".equals(oldState.getId());
		"root/sub2".equals(newState.getId());
	}
}
