package de.intarsys.tools.concurrent;

/**
 * The most simple {@link IPromise} implementation.
 * 
 * @param <V>
 */
public abstract class AbstractPromise<V> extends AbstractFuture<V> implements IPromise<V> {

	protected AbstractPromise() {
		super();
	}

	protected AbstractPromise(String label) {
		super(label);
	}

	@Override
	public void fail(Throwable t) {
		try {
			setException(t);
		} catch (Exception e) {
			// as it is common that a "fail" is followed by more failures, this
			// is not considered an error
		}
	}

	@Deprecated
	public void failed(Throwable t) {
		fail(t);
	}

	@Override
	public void finish(V value) {
		setResult(value);
	}

	@Deprecated
	public void success(V value) {
		finish(value);
	}
}
