package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SearchFilterException extends RuntimeException {
    public SearchFilterException(String message) {
        super(message);
    }

}
