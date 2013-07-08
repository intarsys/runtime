package de.intarsys.tools.validation;

import java.util.ArrayList;
import java.util.List;

public class CompositeValidator<T> extends CommonValidator<T> {

	final private List<IValidator<T>> validators = new ArrayList<IValidator<T>>();

	public void addValidator(IValidator<T> validator) {
		validators.add(validator);
	}

	@Override
	protected void basicValidate(ValidationResult result, T target) {
		for (IValidator<T> validator : validators) {
			ValidationResult tempResult = validator.validate(target);
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
