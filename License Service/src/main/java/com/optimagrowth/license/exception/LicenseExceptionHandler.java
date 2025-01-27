package com.optimagrowth.license.exception;

import com.github.fge.jsonpatch.JsonPatchException;
import com.mongodb.MongoException;
import com.optimagrowth.commonlibrary.api.component.ProblemBuilder;
import com.optimagrowth.license.exception.runtine.LicenseNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
public class LicenseExceptionHandler extends ResponseEntityExceptionHandler {

    private final ProblemBuilder problemBuilder;

    /**
     * Handles {@link LicenseNotFoundException} by returning a ProblemDetail response.
     *
     * @param exception The LicenseNotFoundException that was thrown.
     * @return A ProblemDetail object containing information about the error.
     *         The HTTP status code is set to NOT_FOUND (404).
     */
    @ExceptionHandler(LicenseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleLicenseNotFoundException(LicenseNotFoundException exception) {

        return problemBuilder.buildGenericProblemDetail(exception.getLocalizedMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link MongoException} by returning a ProblemDetail response.
     *
     * @param exception The MongoException that was thrown.
     * @return A ProblemDetail object containing information about the error.
     *         The HTTP status code is set to SERVICE_UNAVAILABLE (503).
     */
    @ExceptionHandler(MongoException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail handleMongoException(MongoException exception) {

        return problemBuilder.buildRuntimeProblemDetail(exception.getLocalizedMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles {@link JsonPatchException} by returning a ProblemDetail response.
     *
     * @param exception The JsonPatchException that was thrown.
     * @return A ProblemDetail object containing information about the error.
     *         The HTTP status code is set to BAD_REQUEST (400).
     */
    @ExceptionHandler(JsonPatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleJsonPatchException(JsonPatchException exception) {

        return problemBuilder.buildGenericProblemDetail(exception.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }
}
