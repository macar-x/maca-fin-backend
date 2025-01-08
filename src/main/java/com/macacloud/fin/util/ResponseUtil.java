package com.macacloud.fin.util;

import com.macacloud.fin.model.CommonResponse;

public class ResponseUtil {

    public static <T> CommonResponse<T> success(T body) {
        return CommonResponse.<T>builder().body(body).build();
    }
}
