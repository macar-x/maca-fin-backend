package com.macacloud.fin.model;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class CommonResponse<T> {

    private String code;
    private String message;
    private T body;
}
