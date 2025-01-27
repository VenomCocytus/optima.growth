//package com.optimagrowth.commonlibrary.api.exception;
//
//
//import ch.qos.logback.core.joran.spi.JoranException;
//import com.optimagrowth.commonlibrary.api.component.ErrorBuilder;
//import com.optimagrowth.commonlibrary.core.common.GenericResponse;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import lombok.RequiredArgsConstructor;
//import org.apache.catalina.connector.ClientAbortException;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.validation.FieldError;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.ServletRequestBindingException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.util.*;
//
//import static com.optimagrowth.commonlibrary.utils.utils.Utils.getStackTraceAsString;
//import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;
//
//@RestControllerAdvice
//@RequiredArgsConstructor
//public class DumpExceptionHandler {
//
//    private final ErrorBuilder errorBuilder;
//
//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<GenericResponse<Object>> handleThrowableException(Throwable throwable){
//
//        // Socket is closed, cannot return any response
//        if(throwable instanceof ClientAbortException)
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(throwable),
//                        translate("exception.general-content")));
//    }
//
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<GenericResponse<Object>> handleRuntimeException(String errorMessage) {
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(errorMessage),
//                        translate("exception.general-content")));
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<GenericResponse<Object>> handleMessageNotReadableException(HttpMessageNotReadableException exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage()),
//                        translate("exception.general-content")
//                ));
//    }
//
//    @ExceptionHandler(ServletRequestBindingException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<GenericResponse<Object>> handleServletRequestBindingException(ServletRequestBindingException exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage()),
//                        getStackTraceAsString(exception)
//                ));
//    }
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ResponseEntity<GenericResponse<Object>> handleException(Exception exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage()),
//                        getStackTraceAsString(exception)
//                ));
//    }
//
//    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseEntity<GenericResponse<Object>> handleUnauthorized(Exception exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage()),
//                        getStackTraceAsString(exception)
//                ));
//    }
//
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
//    public ResponseEntity<GenericResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.METHOD_NOT_ALLOWED)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage())
//                ));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<GenericResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
//
//        Map<String, List<String>> errorMessagesMap = new HashMap<>();
//
//        exception.getBindingResult()
//                .getAllErrors()
//                .forEach((error) -> {
//                    String fieldName;
//
//                    try {
//                        fieldName = ((FieldError) error).getField();
//                    } catch (ClassCastException classCastException) {
//                        fieldName = error.getObjectName();
//                    }
//
//                    String errorMessage = error.getDefaultMessage();
//                    errorMessagesMap.computeIfAbsent(
//                            fieldName, k -> new ArrayList<>()).add(errorMessage);
//                });
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(errorMessagesMap),
//                        translate("exception.general-content")));
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<GenericResponse<Object>> handleConstraintViolationException(ConstraintViolationException exception) {
//
//        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
//        Map<String, List<String>> errorMessagesMap = new HashMap<>();
//
//        constraintViolations.forEach((constraintViolation -> {
//            String fieldName =  String.format("%s", constraintViolation.getPropertyPath());
//            String errorMessage = constraintViolation.getMessage();
//            errorMessagesMap.computeIfAbsent(
//                    fieldName, k -> new ArrayList<>()).add(errorMessage);
//        }));
//
//        return ResponseEntity
//                .status(HttpStatus.BAD_REQUEST)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(errorMessagesMap),
//                        translate("exception.general-content")));
//    }
//
//    @ExceptionHandler(JoranException.class)
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
//    public ResponseEntity<GenericResponse<Object>> handleJoranException(JoranException exception) {
//
//        return ResponseEntity
//                .status(HttpStatus.UNPROCESSABLE_ENTITY)
//                .body(GenericResponse.error(
//                        errorBuilder.createErrorMap(exception.getMessage())
//                ));
//    }
//}
