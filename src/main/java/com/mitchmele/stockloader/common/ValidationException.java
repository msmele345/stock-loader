package com.mitchmele.stockloader.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {

    String message;
    List<ValidationError> errors = new ArrayList<>();

    public ValidationException(ValidationError... error) {
        this.errors.addAll(Arrays.stream(error).collect(Collectors.toList()));
        this.message = errors
                .stream()
                .map(e -> "Error Type: " + e.errorType.toString() + " for field: " +  e.field)
                .collect(Collectors.joining());
    }

    public ValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}