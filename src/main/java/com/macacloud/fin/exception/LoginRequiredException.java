package com.macacloud.fin.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * Argument Not Valid Exception
 *
 * @author Emmett
 * @since 2025/01/17
 */
@Getter
@Setter
public class LoginRequiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 165987611626349840L;

    public LoginRequiredException() {
        super("user not login");
    }
}
