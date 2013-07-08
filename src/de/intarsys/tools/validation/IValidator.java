package de.intarsys.tools.validation;

/**
 * A functor object that can validate a target.
 * 
 */
public interface IValidator<T> {

	/**
	 * Perform the validation operation on the target and return the result.
	 * 
	 * @param target
	 * @return The validation result.
	 */
	public ValidationResult validate(T target);
}
