package de.intarsys.tools.event;

/**
 * An object that observes another.
 * 
 * @param <M>
 *            The model object observed
 */
public interface INotificationObserver<M extends INotificationSupport> {

	/**
	 * The observed object.
	 * 
	 * @return The observed object.
	 */
	public M getObservable();

	/**
	 * Assign the object to be observed.
	 * 
	 * @param observable
	 */
	public M setObservable(M observable);
}
