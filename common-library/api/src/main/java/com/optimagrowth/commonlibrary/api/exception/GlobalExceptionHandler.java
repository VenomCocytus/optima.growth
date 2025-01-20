package com.optimagrowth.commonlibrary.api.exception;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.commonlibrary.api.component.ProblemBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.getStackTraceAsString;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ProblemBuilder problemBuilder;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleThrowableException(Throwable throwable) {

        /*
            Handle specific exception for closed socket
            When closed no response can be returned
        */
        if(throwable instanceof ClientAbortException)
            return ProblemDetail.forStatus(INTERNAL_SERVER_ERROR);

        return problemBuilder
                .buildProblemDetail(
                        throwable.getMessage(), INTERNAL_SERVER_ERROR, false);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleRuntimeException(String errorMessage) {

        return problemBuilder
                .buildRuntimeProblemDetail(
                        errorMessage, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception exception) {

        return problemBuilder
                .buildRuntimeProblemDetail(
                        getStackTraceAsString(exception), exception.getMessage(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleServletRequestBindingException(ServletRequestBindingException exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        getStackTraceAsString(exception), exception.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleUnauthorized(HttpClientErrorException.Unauthorized exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        getStackTraceAsString(exception), exception.getMessage(), UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ProblemDetail handleHttpRequestMethodNotSupportedException(Exception exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getMessage(), METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        Map<String, List<String>> errorMessagesMap = new HashMap<>();

        exception.getBindingResult()
                .getAllErrors()
                .forEach((error) -> {
                    String fieldName;

                    try {
                        fieldName = ((FieldError) error).getField();
                    } catch (ClassCastException classCastException) {
                        fieldName = error.getObjectName();
                    }

                    String errorMessage = error.getDefaultMessage();
                    errorMessagesMap.computeIfAbsent(
                            fieldName, k -> new ArrayList<>()).add(errorMessage);
                });

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        BAD_REQUEST, errorMessagesMap);
        problemDetail.setInstance(exception.getBody().getInstance());

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {

        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        Map<String, List<String>> errorMessagesMap = new HashMap<>();

        constraintViolations.forEach((constraintViolation -> {
            String fieldName =  String.format("%s", constraintViolation.getPropertyPath());
            String errorMessage = constraintViolation.getMessage();
            errorMessagesMap.computeIfAbsent(
                    fieldName, k -> new ArrayList<>()).add(errorMessage);
        }));

        return problemBuilder
                .buildGenericProblemDetail(
                        BAD_REQUEST, errorMessagesMap);
    }

    @ExceptionHandler(JoranException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleJoranException(JoranException exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getMessage(), UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getMessage(), FORBIDDEN);
    }

    @ExceptionHandler({JsonProcessingException.class, JsonPatchException.class})
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleJsonProcessingException(Exception exception) {

        return problemBuilder
                .buildRuntimeProblemDetail(
                        exception.getMessage(), BAD_REQUEST);
    }
}
