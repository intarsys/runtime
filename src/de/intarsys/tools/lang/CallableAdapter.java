package de.intarsys.tools.lang;

import java.util.concurrent.Callable;

/**
 * This adapter eases life when an API supports a {@link Runnable} parameter but
 * you need to return a result.
 * 
 * Example
 * 
 * <code>
 * CallableAdapter ret = new CallableAdapter() {
 * 	public Result call() {
 * 		return "cheers";
 * 	}
 * };
 * Display.getDefault().syncExec(ret);
 * result = ret.getResultUnchecked();
 * </code>
 * 
 * @param <T>
 */
abstract public class CallableAdapter<T> implements Runnable, Callable<T> {

	private T result = null;

	private Exception exception;

	/**
	 * Get the exception if one occured when "run" is executed.
	 * 
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Get the result of the computation after "run" is executed. If execution
	 * has thrown an exception, the exception is thrown here.
	 * 
	 * @return
	 * @throws Exception
	 */
	public T getResult() throws Exception {
		if (exception != null) {
			throw exception;
		}
		return result;
	}

	/**
	 * Get the result of the computation after "run" is executed. If execution
	 * has thrown an exception, null is returned.
	 * 
	 * @return
	 */
	public T getResultUnchecked() {
		if (exception != null) {
			return null;
		}
		return result;
	}

	@Override
	public void run() {
		try {
			setResult(call());
		} catch (Exception e) {
			exception = e;
		}
	}

	protected void setResult(T result) {
		this.result = result;
	}
}
