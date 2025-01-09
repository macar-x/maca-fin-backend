package com.macacloud.fin.util;

import com.macacloud.fin.model.CommonResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;

/**
 * REST Response Utility.
 *
 * @author Emmett
 * @since 2025/01/08
 */
public class ResponseUtil {

    public static <T> CommonResponse<T> success(T body) {
        return CommonResponse.<T>builder()
                .code(String.valueOf(HttpResponseStatus.OK.code()))
                .message(StringUtil.EMPTY_STRING)
                .body(body).build();
    }
}
