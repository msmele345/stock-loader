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
    //Create methods for each type of exception
    //Add more queues for each type for dynamic routing
    Logger logger = LoggerFactory.getLogger(ValidationErrorAdvice.class);

    MessagingTemplate messagingTemplate;
    MessageChannel expectedErrorsQueue;

    public ValidationErrorAdvice(MessagingTemplate messagingTemplate, MessageChannel expectedErrorsQueue) {
        this.messagingTemplate = messagingTemplate;
        this.expectedErrorsQueue = expectedErrorsQueue;
    }
    @Override
    protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {
        try {
            return callback.execute();
        } catch (MessageTransformationException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof ValidationException) {
                Message<?> errorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Errors ", cause.getMessage())
                        .build();
                messagingTemplate.send(expectedErrorsQueue, errorMessage);

            } else { //build out other queues for other types of processing exceptions here later
                Message<?> defaultErrorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Default Error Message: ", prettyException(ex.getLocalizedMessage()))
                        .build();
                messagingTemplate.send(expectedErrorsQueue, defaultErrorMessage);
            }
        } catch (Exception ex) {
            if (ex.getCause() instanceof MessageTransformationException) {
                Throwable validationCause = ex.getCause().getCause();
                Message<?> errorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Errors ", validationCause.getMessage())
                        .build();
                messagingTemplate.send(expectedErrorsQueue, errorMessage);
            } else {
                Message<?> defaultErrorMessage = MessageBuilder
                        .withPayload(message.getPayload())
                        .copyHeadersIfAbsent(message.getHeaders())
                        .setHeader("Unknown Error ", ex.getLocalizedMessage())
                        .build();
                messagingTemplate.send(expectedErrorsQueue, defaultErrorMessage);
            }
        }
        return null;
    }
}
