package com.highpass.runspot.session.exception;

import com.highpass.runspot.common.exception.BaseException;
import com.highpass.runspot.common.exception.BaseExceptionType;

public class SessionException extends BaseException {

    public SessionException(final BaseExceptionType exceptionType) {
        super(exceptionType);
    }
}
