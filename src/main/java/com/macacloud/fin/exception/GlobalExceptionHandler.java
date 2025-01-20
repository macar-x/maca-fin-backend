package com.macacloud.fin.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Common Exception Handler
 *
 * @author Emmett
 * @since 2025/01/16
 */
@Slf4j
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    record ErrorResponse(
            String errorCode,
            String message,
            String timestamp
    ) {
    }

    @Override
    public Response toResponse(Exception exception) {

        // Define the default status code and customize based on exception type
        Response.Status status;
        String errorCode;
        String message = null;

        // Handle specific exceptions
        switch (exception) {
            case LoginRequiredException e -> {
                status = Response.Status.UNAUTHORIZED;
                errorCode = Response.Status.UNAUTHORIZED.getReasonPhrase();
                message = e.getMessage();
            }
            case ArgumentNotValidException e -> {
                status = Response.Status.BAD_REQUEST;
                errorCode = "ARGUMENT_NOT_VALID";
                message = e.getFieldNameList().toString() + " " + exception.getMessage();
            }
            case GlobalRuntimeException e -> {
                status = Response.Status.OK;
                errorCode = e.getCode();
            }
            default -> {
                status = Response.Status.INTERNAL_SERVER_ERROR;
                errorCode = "SYSTEM_ERROR";
                log.error("Unknown exception caught: ", exception);
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Optional.ofNullable(errorCode).orElse(status.getReasonPhrase()),
                Optional.ofNullable(message).orElse(exception.getMessage()),
                LocalDateTime.now().toString()
        );

        return Response
                .status(status)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
