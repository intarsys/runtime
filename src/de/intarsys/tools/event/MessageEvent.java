package de.intarsys.tools.event;

import de.intarsys.tools.functor.IArgs;

/**
 * An event indicating a one way message to an observer.
 * 
 */
public class MessageEvent extends Event {

	public static final EventType ID = new EventType(
			MessageEvent.class.getName());

	final private String name;

	final private IArgs args;

	public MessageEvent(Object source, String name, IArgs args) {
		super(source);
		this.name = name;
		this.args = args;
	}

	public IArgs getArgs() {
		return args;
	}

	@Override
	public EventType getEventType() {
		return ID;
	}

	@Override
	public String getName() {
		return name;
	}

}
