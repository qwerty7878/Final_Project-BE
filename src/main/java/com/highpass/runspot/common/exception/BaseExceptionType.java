package com.highpass.runspot.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    HttpStatus getStatus();
    String getMessage();
}
