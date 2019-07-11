package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptyMapException extends RuntimeException {
    public EmptyMapException(String message) {
        super(message);
    }

}
