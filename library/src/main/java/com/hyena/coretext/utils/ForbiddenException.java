package com.hyena.coretext.utils;

/**
 * Created by yangzc on 17/8/14.
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
