package com.mitchmele.stockloader.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class MessageErrorAdvice extends AbstractRequestHandlerAdvice {

    MessagingTemplate messagingTemplate;
    @Qualifier("errorQueue")
    MessageChannel errorQueue;

    public MessageErrorAdvice(MessagingTemplate messagingTemplate, MessageChannel errorQueue) {
        this.messagingTemplate = messagingTemplate;
        this.errorQueue = errorQueue;
    }

    @Override
    protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {
        try {
            return callback.execute();
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof MessageTransformationException) {
                Message<?> errorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Transform ErrorMessage", ex.getLocalizedMessage())
                        .build();
                messagingTemplate.send(errorQueue, errorMessage);
            } else { //can build out specific errorMessages to specific error queues later
                Message<?> defaultErrorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("DefaultErrorMessage", ex.getLocalizedMessage())
                        .build();

                messagingTemplate.send(errorQueue, defaultErrorMessage);
            }
        }
        return null;
    }
}
