package de.intarsys.tools.validation;

import de.intarsys.tools.notice.INotice;
import de.intarsys.tools.notice.INoticesSupport;

/**
 * The outcome of an validation algorithm applied to a target object.
 * 
 * The validation result is merely an aggregation of the validation target and a
 * collection of {@link INotice} instances with statements on the target.
 * 
 */
public interface IValidationResult extends INoticesSupport {

	/**
	 * The validation target.
	 * 
	 * @return
	 */
	public Object getTarget();

	/**
	 * True if the result has an {@link INotice} of severity
	 * {@link INotice#SEVERITY_ERROR}.
	 * 
	 * @return
	 */
	public boolean hasError();

	/**
	 * True if the result has an {@link INotice} of severity
	 * {@link INotice#SEVERITY_INFO}.
	 * 
	 * @return
	 */
	public boolean hasInfo();

	/**
	 * True if the result has an {@link INotice} of severity
	 * {@link INotice#SEVERITY_WARNING}.
	 * 
	 * @return
	 */
	public boolean hasWarning();
}
