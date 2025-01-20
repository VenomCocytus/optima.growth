package com.optimagrowth.commonlibrary.utils.utils;

import com.optimagrowth.commonlibrary.core.component.Translator;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

public abstract class Utils {

    /**
     * Util to return a custom message based on his key
     *
     * @param key The key in the properties file
     * @return The message corresponding to the key
     */
    public static String translate(String key) {
        return Translator.toLocale(key);
    }
    public static String translate(String key, Object... args) {
        return Translator.toLocale(key, args);
    }

    public static String getStackTraceAsString(Exception exception) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);

        return stringWriter.toString();
    }

    private String getErrorsDetails(MethodArgumentNotValidException exception) {
        return Optional.of(exception.getDetailMessageArguments())
                .map(args -> Arrays.stream(args)
                        .filter(msg -> !ObjectUtils.isEmpty(msg))
                        .reduce("Please make sure to provide a valid request, ", (a, b) -> a + " " + b)
                )
                .orElse("").toString();
    }
}
