package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.common.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import static com.mitchmele.stockloader.utils.ValidationErrorUtils.prettyException;

@Service
public class ValidationErrorAdvice extends AbstractRequestHandlerAdvice {

    Logger logger = LoggerFactory.getLogger(ValidationErrorAdvice.class);

    MessagingTemplate messagingTemplate;
    MessageChannel expectedErrorsQueue;

    public ValidationErrorAdvice(MessagingTemplate messagingTemplate, MessageChannel expectedErrorsQueue) {
        this.messagingTemplate = messagingTemplate;
        this.expectedErrorsQueue = expectedErrorsQueue;
    }

    //Do we need more information in the headers about what field failed? Create logging service pojo*
    //can build out specific errorMessages to specific error queues later
    @Override
    protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {
        try {
            return callback.execute();
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof MessageTransformationException) {
                if (cause.getCause() instanceof ValidationException) {
                    ValidationException nestedException = (ValidationException) cause.getCause();
                    Message<?> errorMessage = MessageBuilder
                            .withPayload(message.getPayload())
                            .copyHeadersIfAbsent(message.getHeaders())
                            .setHeader(" Errors: ", nestedException.getMessage())
                            .build();
                    logger.info("ERROR DETAILS: " + ex.getLocalizedMessage());
                    messagingTemplate.send(expectedErrorsQueue, errorMessage);
                }
            } else {
                Message<?> defaultErrorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Default Error Message: ", prettyException(ex.getLocalizedMessage()))
                        .build();
                messagingTemplate.send(expectedErrorsQueue, defaultErrorMessage);
            }
        }
        return null;
    }
}
