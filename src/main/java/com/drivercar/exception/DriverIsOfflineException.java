package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DriverIsOfflineException extends RuntimeException {
    public DriverIsOfflineException(String message) {
        super(message);
    }

}
