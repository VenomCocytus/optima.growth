package com.optimagrowth.license.validation.annotation;

import com.optimagrowth.license.validation.validator.ProductNameNotAlreadyExistsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ProductNameNotAlreadyExistsValidator.class})
public @interface ProductNameNotAlreadyExists {

    String message() default "{default.license.product.name.already.exists}";

    Class<?> [] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
