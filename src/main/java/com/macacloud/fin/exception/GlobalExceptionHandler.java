package com.macacloud.fin.exception;

import com.macacloud.fin.model.CommonResponse;
import com.macacloud.fin.util.ResponseUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Common Exception Handler
 *
 * @author Emmett
 * @since 2025/01/16
 */
@Slf4j
@Provider
@ApplicationScoped
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        // Define the default status code and customize based on exception type
        Response.Status httpStatus = Response.Status.OK;
        Response.Status responseStatus;
        String message = null;

        // Handle specific exceptions
        switch (exception) {
            case LoginRequiredException ignored -> {
                httpStatus = Response.Status.UNAUTHORIZED;
                responseStatus = Response.Status.UNAUTHORIZED;
            }
            case ArgumentNotValidException e -> {
                responseStatus = Response.Status.BAD_REQUEST;
                message = e.getFieldNameList().toString() + " " + exception.getMessage();
            }
            case DataNotFoundException ignored -> responseStatus = Response.Status.NOT_FOUND;
            case GlobalRuntimeException ignored -> responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
            case WebApplicationException e -> responseStatus = Response.Status.fromStatusCode(e.getResponse().getStatus());
            default -> {
                // Unexpected Error, log and return.
                log.error("Unknown exception caught: ", exception);
                httpStatus = Response.Status.INTERNAL_SERVER_ERROR;
                responseStatus = Response.Status.INTERNAL_SERVER_ERROR;
            }
        }

        // Prepare response entity.
        message = Optional.ofNullable(message).orElse(exception.getMessage());
        CommonResponse<Void> response = ResponseUtil.success(responseStatus, message);
        return Response.status(httpStatus).entity(response).type(MediaType.APPLICATION_JSON).build();
    }
}
