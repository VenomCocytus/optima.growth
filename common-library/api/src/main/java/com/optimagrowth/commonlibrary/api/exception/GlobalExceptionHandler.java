package com.optimagrowth.commonlibrary.api.exception;

import ch.qos.logback.core.joran.spi.JoranException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.optimagrowth.commonlibrary.api.component.ProblemBuilder;
import io.micrometer.common.lang.NonNullApi;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.getStackTraceAsString;
import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;
import static org.springframework.http.HttpStatus.*;

@NonNullApi
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ProblemBuilder problemBuilder;

    /**
     * Handles {@link HttpMessageNotReadableException}, which occurs when the body of an HTTP request cannot be read or parsed.
     * This typically happens due to invalid JSON or other data format issues in the request body.
     *
     * @param exception The exception that was thrown when the request body could not be read.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that encapsulates error information.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception,
                                                               HttpHeaders headers,
                                                               HttpStatusCode statusCode,
                                                               WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), exception.getMessage(), BAD_REQUEST);
        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    /**
     * Handles {@link ServletRequestBindingException}, which occurs when there is an issue binding the request parameters to the method arguments.
     * This can happen due to type mismatches, missing parameters, or other binding-related problems.
     *
     * @param exception The exception that was thrown when the request parameters could not be bound.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that encapsulates error information.
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException exception,
                                                                       HttpHeaders headers,
                                                                       HttpStatusCode statusCode,
                                                                       WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), getStackTraceAsString(exception), BAD_REQUEST);
        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    /**
     * Handles {@link HttpClientErrorException.Unauthorized} exceptions, which occur when the client lacks proper authorization to access a resource.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information for unauthorized access attempts.
     *
     * @param exception The exception that was thrown due to unauthorized access.
     *
     * @return A {@link ProblemDetail} object containing detailed error information, including the stack trace and localized message.
     */
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<Object> handleUnauthorizedException(HttpClientErrorException.Unauthorized exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), getStackTraceAsString(exception), UNAUTHORIZED);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), UNAUTHORIZED, request);
    }

    /**
     * Handles {@link HttpRequestMethodNotSupportedException}, which occurs when an HTTP request is made using a method that is not supported for the requested resource.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 405 status code.
     *
     * @param exception The exception that was thrown due to an unsupported HTTP method.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#METHOD_NOT_ALLOWED}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the unsupported method and supported methods.
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                                                         HttpHeaders headers,
                                                                      HttpStatusCode statusCode,
                                                                      WebRequest request) {

        String supportedMethods = String
                .join(", ", exception.getSupportedHttpMethods().stream()
                        .map(HttpMethod::name)
                        .toList());

        String errorMessage = translate("exception.http.request.method.not.supported",
                exception.getMethod(), supportedMethods);

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), errorMessage, METHOD_NOT_ALLOWED);

        return handleExceptionInternal(exception, problemDetail, headers, METHOD_NOT_ALLOWED, request);
    }

    /**
     * Handles {@link HttpMediaTypeNotSupportedException}, which occurs when an HTTP request contains a media type that is not supported by the server.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 415 status code.
     *
     * @param exception The exception that was thrown due to an unsupported media type.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#UNSUPPORTED_MEDIA_TYPE}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the unsupported media type and supported media types.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException  exception,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode statusCode,
                                                                         WebRequest request) {

        String supportedMedia = String
                .join(", ", exception.getSupportedMediaTypes().stream()
                        .map(MediaType::getType)
                        .toList());

        String errorMessage = translate("exception.http.request.media.type.not.supported",
                exception.getContentType(), supportedMedia);

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), errorMessage, UNSUPPORTED_MEDIA_TYPE);

        return handleExceptionInternal(exception, problemDetail, headers, UNSUPPORTED_MEDIA_TYPE, request);
    }

    /**
     * Handles {@link MethodArgumentNotValidException}, which occurs when the validation of method arguments fails.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 400 status code.
     *
     * @param exception The exception that was thrown due to invalid method arguments.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the validation errors.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                      HttpHeaders headers,
                                                                      HttpStatusCode statusCode,
                                                                      WebRequest request) {

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
                        translate("exception.method.argument.not.valid"), BAD_REQUEST, errorMessagesMap);
        problemDetail.setInstance(exception.getBody().getInstance());

        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    /**
     * Handles {@link MissingServletRequestPartException}, which occurs when a required part of the HTTP request is missing.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 400 status code.
     *
     * @param exception The exception that was thrown due to a missing request part.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the missing request part.
     */
    @Override
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

    /**
     * Handles {@link MissingServletRequestParameterException}, which occurs when a required parameter is missing from the HTTP request.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 400 status code.
     *
     * @param exception The exception that was thrown due to a missing request parameter.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the missing request parameter.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException exception,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode statusCode,
                                                                     WebRequest request) {
        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(),
                        translate("exception.missing.servlet.request.parameter",
                                exception.getParameterName()),
                        BAD_REQUEST);

        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    /**
     * Handles {@link TypeMismatchException}, which occurs when a request parameter cannot be converted to the expected type.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 400 status code.
     *
     * @param exception The exception that was thrown due to a type mismatch.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#BAD_REQUEST}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the type mismatch.
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException exception,
                                                        HttpHeaders headers,
                                                        HttpStatusCode statusCode,
                                                        WebRequest request) {
        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(),
                        translate("exception.type.mismatch",
                                exception.getValue(), exception.getPropertyName(), exception.getRequiredType()),
                        BAD_REQUEST);

        return handleExceptionInternal(exception, problemDetail, headers, BAD_REQUEST, request);
    }

    /**
     * Handles {@link NoHandlerFoundException}, which occurs when no handler method is found for the requested URL and HTTP method.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns a {@link ResponseEntity} with a 404 status code.
     *
     * @param exception The exception that was thrown due to no handler being found.
     * @param headers The HTTP headers of the request.
     * @param statusCode The HTTP status code to be used in the response. In this case, it is always {@link HttpStatus#NOT_FOUND}.
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that includes details about the missing handler.
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException exception,
                                                                   HttpHeaders headers,
                                                                   HttpStatusCode statusCode,
                                                                   WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(),
                        translate("exception.no.handler.found",
                                exception.getHttpMethod(), exception.getRequestURL()),
                        NOT_FOUND);

        return handleExceptionInternal(exception, problemDetail, headers, NOT_FOUND, request);
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException}, which occurs when a method argument cannot be converted to the expected type.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 400 status code.
     *
     * @param exception The exception that was thrown due to a type mismatch in a method argument.
     *
     * @return A {@link ProblemDetail} object containing details about the type mismatch.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentMismatch(MethodArgumentTypeMismatchException exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(),
                        translate("exception.method.argument.type.mismatch",
                                exception.getName(), exception.getRequiredType().getName()),
                        BAD_REQUEST);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), BAD_REQUEST, request);
    }

    /**
     * Handles {@link ConstraintViolationException}, which occurs when constraints on method arguments or request bodies are violated.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 400 status code.
     *
     * @param exception The exception that was thrown due to constraint violations.
     * @param request The current web request.
     *
     * @return A {@link ProblemDetail} object containing details about the constraint violations.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception,
                                                            WebRequest request) {

        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        Map<String, List<String>> errorMessagesMap = new HashMap<>();

        constraintViolations.forEach((constraintViolation -> {
            String fieldName =  String.format("%s", constraintViolation.getPropertyPath());
            String errorMessage = constraintViolation.getMessage();
            errorMessagesMap.computeIfAbsent(
                    fieldName, k -> new ArrayList<>()).add(errorMessage);
        }));

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        translate("exception.method.argument.not.valid"), BAD_REQUEST, errorMessagesMap);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), BAD_REQUEST, request);
    }

    /**
     * Handles {@link JoranException}, which typically occurs due to issues with XML configuration parsing.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 422 status code.
     *
     * @param exception The exception that was thrown due to a Joran-related issue.
     *
     * @return A {@link ProblemDetail} object containing details about the Joran exception.
     */
    @ExceptionHandler(JoranException.class)
    public ResponseEntity<Object> handleJoranException(JoranException exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), UNPROCESSABLE_ENTITY);
        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), UNPROCESSABLE_ENTITY, request);
    }

    /**
     * Handles {@link AccessDeniedException}, which occurs when a user lacks the necessary permissions to access a resource.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 403 status code.
     *
     * @param exception The exception that was thrown due to an access denial.
     *
     * @return A {@link ProblemDetail} object containing details about the access denial.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildGenericProblemDetail(
                        exception.getLocalizedMessage(), FORBIDDEN);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), FORBIDDEN, request);
    }

    /**
     * Handles both {@link JsonProcessingException} and {@link JsonPatchException}, which occur during JSON processing or patching operations.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 400 status code.
     *
     * @param exception The exception that was thrown due to a JSON processing or patching issue.
     *
     * @return A {@link ProblemDetail} object containing details about the JSON processing error.
     */
    @ExceptionHandler({JsonProcessingException.class, JsonPatchException.class})
    public ResponseEntity<Object> handleJsonProcessingException(Exception exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildRuntimeProblemDetail(
                        exception.getLocalizedMessage(), BAD_REQUEST);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), BAD_REQUEST, request);
    }

    /**
     * Handles any uncaught {@link Throwable} instances, providing a generic error handling mechanism for unexpected server-side errors.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 500 status code.
     *
     * @param throwable The uncaught exception or error that was thrown.
     *
     * @return A {@link ProblemDetail} object containing details about the server error.
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable throwable, WebRequest request) {

        /*
            Handle specific exception for closed socket
            When closed no response can be returned
        */
        if(throwable instanceof ClientAbortException)
            return createResponseEntity(
                    ProblemDetail.forStatus(INTERNAL_SERVER_ERROR), new HttpHeaders(), INTERNAL_SERVER_ERROR, request);

        ProblemDetail problemDetail = problemBuilder
                .buildProblemDetail(
                        throwable.getMessage(), INTERNAL_SERVER_ERROR, false);

        return createResponseEntity(
                problemDetail, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles any uncaught {@link Exception} instances, providing a generic error handling mechanism for unexpected server-side errors.
     * This method constructs a {@link ProblemDetail} object to encapsulate error information and returns it with a 500 status code.
     *
     * @param exception The uncaught exception that was thrown.
     *
     * @return A {@link ProblemDetail} object containing details about the server error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception exception) {

        return problemBuilder
                .buildRuntimeProblemDetail(
                        exception.getLocalizedMessage(), getStackTraceAsString(exception), INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles internal server errors by catching specific runtime exceptions and returning a standardized error response.
     *
     * @param exception The runtime exception that occurred, which can be one of the following:
     *                  <ul>
     *                      <li>{@link NullPointerException} - Thrown when an application attempts to use an object reference that has a null value.</li>
     *                      <li>{@link IllegalArgumentException} - Thrown to indicate that a method has been passed an argument that is not valid for that method.</li>
     *                      <li>{@link IllegalStateException} - Thrown when a method is invoked at an inappropriate time.</li>
     *                  </ul>
     * @param request The current web request.
     *
     * @return A {@link ResponseEntity} containing a {@link ProblemDetail} object that describes the error, along with an HTTP status code indicating an internal server error (500).
     *
     * @see #handleExceptionInternal(Exception, Object, HttpHeaders, HttpStatus, WebRequest)
     * @see ProblemDetail
     */
    @ExceptionHandler({
            NullPointerException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternal(RuntimeException exception, WebRequest request) {

        ProblemDetail problemDetail = problemBuilder
                .buildRuntimeProblemDetail(
                        exception.getLocalizedMessage(), getStackTraceAsString(exception), INTERNAL_SERVER_ERROR);

        return handleExceptionInternal(exception, problemDetail, new HttpHeaders(), INTERNAL_SERVER_ERROR, request);
    }
}
