package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DriverIsDeletedException extends RuntimeException {
    public DriverIsDeletedException(String message) {
        super(message);
    }

}
