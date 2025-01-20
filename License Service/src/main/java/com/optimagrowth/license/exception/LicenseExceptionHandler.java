package com.optimagrowth.license.exception;

import com.mongodb.MongoException;
import com.optimagrowth.commonlibrary.api.component.ProblemBuilder;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class LicenseExceptionHandler {

    private final ProblemBuilder problemBuilder;

    @ExceptionHandler(LicenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleLicenseNotFoundException(LicenseNotFoundException exception) {
        return problemBuilder.buildGenericProblemDetail(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MongoException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail handleMongoException(MongoException exception) {
        return problemBuilder.buildRuntimeProblemDetail(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
