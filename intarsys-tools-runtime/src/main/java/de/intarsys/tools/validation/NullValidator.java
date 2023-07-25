package de.intarsys.tools.validation;

public class NullValidator implements IValidator {

	@Override
	public IValidationResult validate(Object target) {
		return new ValidationResult(target);
	}
}
