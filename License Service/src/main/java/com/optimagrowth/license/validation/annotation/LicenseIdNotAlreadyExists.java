package com.optimagrowth.license.validation.annotation;

import com.optimagrowth.license.validation.validator.LicenseIdNotAlreadyExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy ={LicenseIdNotAlreadyExistsValidator.class} )
public @interface LicenseIdNotAlreadyExists {

    String message() default "{default.license.id.already.exists}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
