package me.sh4rewith.domain;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public abstract class ValidatedImmutableBuilderBase<B> {

	private static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
	private Validator validator = DEFAULT_VALIDATOR_FACTORY.getValidator();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void validate(ValidatedImmutableBuilderBase<B> builtObject) {
		Set<ConstraintViolation<ValidatedImmutableBuilderBase<B>>> violations = validator.validate(this);
		if (violations.size() > 0) {
			throw new ConstraintViolationException("Invalid object:", (Set) violations);
		}
	}

	public B build() {
		validate(this);
		B builtObject = doBuild();
		return builtObject;
	}

	protected abstract B doBuild();
}
