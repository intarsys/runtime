package de.intarsys.tools.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

public interface ICompletable<R> extends Future<R> {

	default void cancel() {
		fail(new CancellationException());
	}

	void fail(Throwable t);

	void finish();

}
