package com.optimagrowth.license.exception.runtine;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@EqualsAndHashCode(callSuper = true)
public class LicenseNotFoundException extends RuntimeException {
    public LicenseNotFoundException(String message) {
        super(message);
    }
}
