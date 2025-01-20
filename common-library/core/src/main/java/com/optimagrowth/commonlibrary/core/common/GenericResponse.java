package com.optimagrowth.commonlibrary.core.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GenericResponse<T> {

    private LocalDateTime timestamp;
    private boolean success;
    private String message;
    private T data;

    public static <T> GenericResponse<T> empty(){
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message("")
                .build();
    }

    public static <T> GenericResponse<T> success(T data){
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> success(String message) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .build();
    }

    public static <T> GenericResponse<T> success(T data,
                                                 String message) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> error(T data){
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message("error")
                .data(data)
                .build();
    }

    public static <T> GenericResponse<T> error(String message){
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .build();
    }

    public static <T> GenericResponse<T> error(T data,
                                               String message) {
        return GenericResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
