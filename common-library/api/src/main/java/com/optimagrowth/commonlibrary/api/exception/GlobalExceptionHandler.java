package com.optimagrowth.commonlibrary.api.exception;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.commonlibrary.api.component.ProblemBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNullApi;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nullable;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.getStackTraceAsString;
import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ProblemBuilder problemBuilder;
    private final Map<String, List<String>> errorMessagesMap = new HashMap<>();

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleThrowable(Throwable throwable) {

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
    public ProblemDetail handleRuntime(String errorMessage) {

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

    @Override
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                               HttpHeaders headers,
                                                               HttpStatusCode statusCode,
                                                               WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), exception.getMessage(), BAD_REQUEST);
        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    @Override
    @ExceptionHandler(ServletRequestBindingException.class)
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exception,
                                                                       HttpHeaders headers,
                                                                       HttpStatusCode statusCode,
                                                                       WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        getStackTraceAsString(exception), exception.getLocalizedMessage(), BAD_REQUEST);
        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ProblemDetail handleUnauthorizedException(HttpClientErrorException.Unauthorized exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        getStackTraceAsString(exception), exception.getLocalizedMessage(), UNAUTHORIZED);
    }

    @Override
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                         HttpHeaders headers,
                                                                      HttpStatusCode statusCode,
                                                                      WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), METHOD_NOT_ALLOWED);

        return handleExceptionInternal(exception, problemDetail, headers, METHOD_NOT_ALLOWED, request);
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode statusCode,
                                                                      WebRequest request) {

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
                        exception.getLocalizedMessage(), BAD_REQUEST, errorMessagesMap);
        problemDetail.setInstance(exception.getBody().getInstance());

        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    @Override
    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException exception,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode statusCode,
                                                                     WebRequest request) {
        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(),
                        translate("exception.missing.servlet.request.part",
                                exception.getRequestPartName()),
                        BAD_REQUEST);

        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {

        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();

        constraintViolations.forEach((constraintViolation -> {
            String fieldName =  String.format("%s", constraintViolation.getPropertyPath());
            String errorMessage = constraintViolation.getMessage();
            errorMessagesMap.computeIfAbsent(
                    fieldName, k -> new ArrayList<>()).add(errorMessage);
        }));

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), BAD_REQUEST, errorMessagesMap);
    }

    @ExceptionHandler(JoranException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleJoranException(JoranException exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {

        return problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), FORBIDDEN);
    }

    @ExceptionHandler({JsonProcessingException.class, JsonPatchException.class})
    @ResponseStatus(BAD_REQUEST)
    public ProblemDetail handleJsonProcessingException(Exception exception) {

        return problemBuilder
                .buildRuntimeProblemDetail(
                        exception.getLocalizedMessage(), BAD_REQUEST);
    }
}
