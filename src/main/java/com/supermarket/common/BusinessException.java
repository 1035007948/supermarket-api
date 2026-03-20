package com.supermarket.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final HttpStatus status;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.status = HttpStatus.BAD_REQUEST;
    }
    
    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
        this.status = status;
    }
}
