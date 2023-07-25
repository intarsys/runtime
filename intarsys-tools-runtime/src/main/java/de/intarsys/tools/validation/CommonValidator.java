package de.intarsys.tools.validation;

public abstract class CommonValidator<T> implements IValidator<T> {

	protected abstract void basicValidate(ValidationResult result, T target);

	@Override
	public IValidationResult validate(T target) {
		ValidationResult result = new ValidationResult(target);
		basicValidate(result, target);
		return result;
	}

}
