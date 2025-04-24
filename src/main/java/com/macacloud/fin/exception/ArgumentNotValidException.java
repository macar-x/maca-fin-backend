package com.macacloud.fin.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Argument Not Valid Exception
 *
 * @author Emmett
 * @since 2024/08/18
 */
@Getter
@Setter
public class ArgumentNotValidException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5446513993880297085L;

    private List<String> fieldNameList;
    private String errorMessage;

    public static String MESSAGE_DEFAULT = "not satisfied.";
    public static String MESSAGE_NOT_EMPTY = "could not be empty.";

    public ArgumentNotValidException() {
        super(ArgumentNotValidException.MESSAGE_DEFAULT);
        this.fieldNameList = Collections.singletonList("params");
        this.errorMessage = ArgumentNotValidException.MESSAGE_DEFAULT;
    }

    public ArgumentNotValidException(String errorMessage) {
        super(errorMessage);
        this.fieldNameList = Collections.singletonList("params");
        this.errorMessage = errorMessage;
    }

    public ArgumentNotValidException(List<String> fieldNameList) {
        super(ArgumentNotValidException.MESSAGE_DEFAULT);
        this.fieldNameList = fieldNameList;
        this.errorMessage = ArgumentNotValidException.MESSAGE_DEFAULT;
    }

    public ArgumentNotValidException(List<String> fieldNameList, String errorMessage) {
        super(errorMessage);
        this.fieldNameList = fieldNameList;
        this.errorMessage = errorMessage;
    }
}
