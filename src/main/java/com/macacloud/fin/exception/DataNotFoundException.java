package com.macacloud.fin.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * Argument Not Valid Exception
 *
 * @author Emmett
 * @since 2025/01/17
 */
@Getter
@Setter
public class DataNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2247269632858225948L;

    public DataNotFoundException() {
        super("data not found");
    }

    public DataNotFoundException(String dataName) {
        super(dataName + " not found");
    }
}
