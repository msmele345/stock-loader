package com.mitchmele.stockloader.utils;

import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;

public class ValidationErrorUtils {

    public static String prettyException(String localizedMessage) {
        return localizedMessage.split(" at")[0];
    }

    public static String parseValidationError(String errorMessage) {
        String[] parts = errorMessage.split(" failedMessage");
        return parts[0].substring(0, parts[0].length() -1);
    }

    public static String messageAsString(Message<?> message) {
        return message.getPayload() instanceof String
                ? (String) message.getPayload()
                : new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
    }
}
