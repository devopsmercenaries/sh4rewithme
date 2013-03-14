package me.sh4rewith.utils.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

@Pattern(regexp = ".+@.+", message = "{0} must contain a valid email")
@ReportAsSingleViolation
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface Email {
	String message() default "{Email}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
