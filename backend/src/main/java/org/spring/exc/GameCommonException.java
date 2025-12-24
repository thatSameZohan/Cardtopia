package org.spring.exc;

import lombok.Getter;

@Getter
public class GameCommonException extends RuntimeException {

    private final String code;

    public GameCommonException(String code, String message) {
        super(message);
        this.code = code;
    }

}
