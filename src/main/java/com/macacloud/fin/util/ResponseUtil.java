package com.macacloud.fin.util;

import com.macacloud.fin.model.CommonResponse;
import io.netty.util.internal.StringUtil;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

/**
 * REST Response Utility.
 *
 * @author Emmett
 * @since 2025/01/08
 */
public class ResponseUtil {

    public static <T> CommonResponse<T> success(Response.Status status) {
        return success(status, null);
    }

    public static <T> CommonResponse<T> success(T body) {
        return success(StringUtil.EMPTY_STRING, body);
    }

    public static <T> CommonResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> CommonResponse<T> success(Response.Status status, String message) {
        return compose(status, message, null);
    }

    public static <T> CommonResponse<T> success(Response.Status status, T body) {
        return compose(status, StringUtil.EMPTY_STRING, body);
    }

    public static <T> CommonResponse<T> success(String message, T body) {
        return compose(Response.Status.OK, message, body);
    }

    public static <T> CommonResponse<T> failed(String message) {
        return failed(message, null);
    }

    public static <T> CommonResponse<T> failed(String message, T body) {
        return compose(Response.Status.INTERNAL_SERVER_ERROR, message, body);
    }

    public static <T> CommonResponse<T> compose(Response.Status status, String message, T body) {
        return CommonResponse.<T>builder()
                .code(status.getStatusCode())
                .message(message)
                .body(body)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
