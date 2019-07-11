package com.drivercar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundRunTimeException extends RuntimeException {
    static final long serialVersionUID = -3387516993334229948L;

    public EntityNotFoundRunTimeException(String message) {
        super(message);
    }

}
