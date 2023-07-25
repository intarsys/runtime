package de.intarsys.tools.component;

/**
 * An object that may be "attached" (and detached) to the current execution
 * thread.
 * 
 * This brings the execution thread paradigm in parallel to a state context
 * model. The state context is made available as a execution thread bound
 * attribute.
 * 
 * The attach/detach call is issued by the event scheduling framework /
 * container, like servlet or service implementation, UI event thread or timer.
 * 
 */
public interface IAttachable {

	void attach();

	void detach();

}
