package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class CarIsDeletedException extends RuntimeException {
    public CarIsDeletedException(String message) {
        super(message);
    }

}
