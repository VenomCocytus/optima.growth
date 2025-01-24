package com.optimagrowth.commonlibrary.api.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.optimagrowth.commonlibrary.utils.utils.Utils.translate;

@Component
public class ProblemBuilder {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String serverPort;

    private final static String DETAIL = "detail";
    private final static String GENERIC = "Generic";
    private final static String RUNTIME = "Runtime";
    private final static String TIMESTAMP = "timestamp";
    private final static String ERROR_CATEGORY = "errorCategory";

    private ProblemDetail createProblemDetail(String title, String detail, HttpStatus httpStatus, String errorCategory) {

        String errorUri = String.format(
                "https://%s:%s/errors/%s", applicationName, serverPort, httpStatus.name().toLowerCase());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setType(URI.create(errorUri));
        problemDetail.setTitle(title);
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CATEGORY, errorCategory);

        return problemDetail;
    }

    public ProblemDetail buildProblemDetail(String detail, HttpStatus httpStatus, boolean generic) {
        String title = translate("exception.generic.title");
        String errorCategory = generic ? GENERIC : RUNTIME;

        return createProblemDetail(title, detail, httpStatus, errorCategory);
    }

    public ProblemDetail buildRuntimeProblemDetail(String detail, HttpStatus httpStatus) {
        return createProblemDetail(translate("exception.generic.title"), detail, httpStatus, RUNTIME);
    }

    public ProblemDetail buildRuntimeProblemDetail(String title, String detail, HttpStatus httpStatus) {
        return createProblemDetail(title, detail, httpStatus, RUNTIME);
    }

    public ProblemDetail buildGenericProblemDetail(String detail, HttpStatus httpStatus) {
        return createProblemDetail(translate("exception.generic.title"), detail, httpStatus, GENERIC);
    }

    public ProblemDetail buildGenericProblemDetail(String title, String detail, HttpStatus httpStatus) {
        return createProblemDetail(title, detail, httpStatus, GENERIC);
    }

    public ProblemDetail buildGenericProblemDetail(String title, HttpStatus httpStatus, Map<String, List<String>> errorMessagesMap) {

        String errorUri = String.format(
                "https://%s:%s/errors/%s", applicationName, serverPort, httpStatus.name());

        ProblemDetail problemDetail = ProblemDetail.forStatus(httpStatus);
        problemDetail.setType(URI.create(errorUri));
        problemDetail.setTitle(title);
        problemDetail.setProperty(DETAIL, errorMessagesMap);
        problemDetail.setProperty(TIMESTAMP, Instant.now());
        problemDetail.setProperty(ERROR_CATEGORY, GENERIC);

        return problemDetail;
    }
}
