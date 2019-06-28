package com.hyena.coretext.utils;

/**
 * 禁止操作异常
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Forbidden this Operation Exception!!!");
    }

    public ForbiddenException(String detailMessage) {
        super(detailMessage);
    }
}
