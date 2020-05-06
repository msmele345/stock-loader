package com.mitchmele.stockloader.common;

import lombok.Data;

@Data
public class ValidationError {
    String field;
    ValidationErrorType errorType;
    Throwable cause;

    public ValidationError(String field, ValidationErrorType errorType, Throwable cause) {
        this.field = field;
        this.errorType = errorType;
        this.cause = cause;
    }
}
