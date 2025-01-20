package com.optimagrowth.license.validation.annotation;

import com.optimagrowth.license.validation.validator.LicenseIdPathVariableExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {LicenseIdPathVariableExistsValidator.class})
public @interface LicenseIdPathVariableExists {

    String message() default "{default.license.id.path.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
