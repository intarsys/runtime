package de.intarsys.tools.validation;

import java.util.ArrayList;
import java.util.List;

public class CompositeValidator<T> extends CommonValidator<T> {

	private final List<IValidator<T>> validators = new ArrayList<>();

	public void addValidator(IValidator<T> validator) {
		validators.add(validator);
	}

	@Override
	protected void basicValidate(ValidationResult result, T target) {
		for (IValidator<T> validator : validators) {
			IValidationResult tempResult = validator.validate(target);
			result.addNotices(tempResult);
		}
	}

	public List<IValidator<T>> getValidators() {
		return validators;
	}

	public void removeValidator(IValidator<T> validator) {
		validators.remove(validator);
	}
}
