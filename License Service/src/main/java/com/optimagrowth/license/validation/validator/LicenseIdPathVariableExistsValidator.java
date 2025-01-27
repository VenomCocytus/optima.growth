package com.optimagrowth.license.validation.validator;

import com.optimagrowth.license.repository.LicenseRepository;
import com.optimagrowth.license.validation.annotation.LicenseIdPathVariableExists;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

@RequiredArgsConstructor
public class LicenseIdPathVariableExistsValidator implements ConstraintValidator<LicenseIdPathVariableExists, String> {

    private final LicenseRepository licenseRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(StringUtils.hasText(value)) {
            return licenseRepository.existsByLicenseId(value);
        }

        return true;
    }
}
