package de.intarsys.tools.validation;

abstract public class CommonValidator<T> implements IValidator<T> {

	abstract protected void basicValidate(ValidationResult result, T target);

	@Override
	public ValidationResult validate(T target) {
		ValidationResult result = new ValidationResult(target);
		basicValidate(result, target);
		return result;
	}

}
