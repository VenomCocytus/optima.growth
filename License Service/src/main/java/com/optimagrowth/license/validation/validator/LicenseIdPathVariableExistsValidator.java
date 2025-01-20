package com.optimagrowth.license.validation.validator;

import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.validation.annotation.LicenseIdNotAlreadyExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class LicenseIdPathVariableExistsValidator implements ConstraintValidator<LicenseIdNotAlreadyExists, String> {

    private final LicenseRepository licenseRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.hasText(value)) {
            return licenseRepository.existsByLicenseId(value);
        }

        return true;
    }
}
