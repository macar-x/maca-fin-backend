package com.macacloud.fin.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * Common Runtime Exception
 *
 * @author Emmett
 * @since 2024/06/13
 */
@Getter
@Setter
public class GlobalRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5232398196845848742L;

    public GlobalRuntimeException(String message) {
        super(message);
    }
}
