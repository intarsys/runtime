package de.intarsys.tools.concurrent;

/**
 * The most simple {@link IPromise} implementation.
 * 
 * @param <V>
 */
public class Promise<V> extends AbstractPromise<V> {

	public static Promise newFailed(String label, Throwable t) {
		Promise result = new Promise<>(label);
		result.fail(t);
		return result;
	}

	public static Promise newFailed(Throwable t) {
		Promise result = new Promise<>();
		result.fail(t);
		return result;
	}

	public static <V> Promise<V> newFinished(String label, V value) {
		Promise<V> result = new Promise<>(label);
		result.finish(value);
		return result;
	}

	public static <V> Promise<V> newFinished(V value) {
		Promise<V> result = new Promise<>();
		result.finish(value);
		return result;
	}

	public Promise() {
		super();
		created();
	}

	public Promise(String label) {
		super(label);
		created();
	}

}
