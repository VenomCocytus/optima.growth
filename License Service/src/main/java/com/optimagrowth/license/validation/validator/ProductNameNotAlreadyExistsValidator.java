package com.optimagrowth.license.validation.validator;

import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.validation.annotation.ProductNameNotAlreadyExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class ProductNameNotAlreadyExistsValidator implements ConstraintValidator<ProductNameNotAlreadyExists, String> {

    private final LicenseRepository licenseRepository;
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.hasText(value)) {
            return !licenseRepository.existsByProductName(value);
        }

        return true;
    }
}
