package com.macacloud.fin.model;

import lombok.*;

/**
 * Common Response Structure for REST.
 *
 * @author Emmett
 * @since 2025/01/08
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
public class CommonResponse<T> {

    private Integer code;
    private String message;
    private T body;
    private String timestamp;
}
