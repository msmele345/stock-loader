package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.common.ValidationError;
import com.mitchmele.stockloader.common.ValidationErrorType;
import com.mitchmele.stockloader.common.ValidationException;
import com.mitchmele.stockloader.model.Bid;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ValidationErrorAdviceTest extends ValidationErrorAdvice {

    //private final static -> create this variable only once.
    //private final -> create this variable that cant be changed for every object. First one saves memory, go for it.
    //Therefore if it cannot change, there is no point having one copy per instance.
    private static final MessageChannel mockErrorChannel = mock(MessageChannel.class);
    private static final MessagingTemplate mockTemplate = mock(MessagingTemplate.class);

    public ValidationErrorAdviceTest() {
        super(mockTemplate, mockErrorChannel);
    }

    @Test
    public void doInvoke_shouldCall_ExecutionCallback() {
        GenericMessage<String> incomingMessage = new GenericMessage<>("some payload");

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doInvoke(mockCallback, null, incomingMessage);

        verify(mockCallback).execute();
    }

    @Test
    public void doInvoke_shouldReturnResultOfExecute_IfNoErrorsOccur() {
        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload")
                .setHeader("Type", "BID")
                .build();

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);
        when(mockCallback.execute()).thenReturn(incomingMessage);

        Object actual = doInvoke(mockCallback, null, incomingMessage);
        assertThat(actual).isEqualTo(incomingMessage);
    }

    @Test
    public void doInvoke_shouldSendMessageToExpectedErrors() {
        Bid incomingBid = new Bid("ABC", null);
        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingBid)
                .setHeader("Type", "BID")
                .build();

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doAnswer(e -> {
            throw new MessageTransformationException("null not allowed",
                    new ValidationException(
                            new ValidationError("bid",
                                    ValidationErrorType.DATA_INVALID, null)));
        }).when(mockCallback).execute();

        Object actual = doInvoke(mockCallback, null, incomingMessage);
        assertThat(actual).isNull(); //errors should return null

        ArgumentCaptor<Message<?>> captor = ArgumentCaptor.forClass(Message.class);
        verify(mockTemplate).send(eq(mockErrorChannel), captor.capture());
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void doInvoke_shouldSendMessageToErrorQueueWithHeaders_containingErrorMsgs() {
        Bid incomingBid = new Bid("ABC", null);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingBid)
                .setHeader("Type", "BID")
                .build();

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doAnswer(e -> {
            throw new MessageTransformationException("bad message",
                    new ValidationException(
                            new ValidationError(
                                    "bid must not be null", ValidationErrorType.DATA_INVALID, null)));
        }).when(mockCallback).execute();

        doInvoke(mockCallback, null, incomingMessage);

        ArgumentCaptor<Message<?>> captor = ArgumentCaptor.forClass(Message.class);
        verify(mockTemplate, atLeastOnce()).send(eq(mockErrorChannel), captor.capture()); //need to capture first

        Message<?> actualMessage = captor.getValue();

        assertThat(actualMessage.getPayload()).isEqualTo(incomingBid);
        assertThat(actualMessage.getHeaders().get("Errors ")).isEqualTo("Error Type: DATA_INVALID for field: bid must not be null");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void doInvoke_shouldSendMessageToErrorQueueWithMsg_whenRunTimeErrorOccurs() {
        Bid incomingBid = new Bid("ABC", null);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingBid)
                .setHeader("Type", "BID")
                .build();

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doThrow(new RuntimeException("something very bad happened")).when(mockCallback).execute();

        doInvoke(mockCallback, null, incomingMessage);

        ArgumentCaptor<Message<?>> captor = ArgumentCaptor.forClass(Message.class);
        verify(mockTemplate, atLeastOnce()).send(eq(mockErrorChannel), captor.capture()); //need to capture first

        Message<?> actualMessage = captor.getValue();

        assertThat(actualMessage.getPayload()).isEqualTo(incomingBid);
        assertThat(actualMessage.getHeaders().get("Unknown Error ")).isEqualTo("something very bad happened");
    }
}