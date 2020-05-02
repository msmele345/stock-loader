package com.mitchmele.stockloader.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
class MessageErrorAdviceTest extends MessageErrorAdvice {

    @Mock
    @Qualifier("mockTemplate")
    MessagingTemplate mockTemplate;

    @Mock
    @Qualifier("mockErrorChannel")
    MessageChannel mockErrorChannel;

    public MessageErrorAdviceTest(@Qualifier("mockTemplate") MessagingTemplate messagingTemplate,
                                  @Qualifier("mockErrorChannel") MessageChannel errorChannel) {
        super(messagingTemplate, errorChannel);
    }


    @Test
    public void doInvoke_shouldCall_ExecutionCallback() {
        GenericMessage<String> incomingMessage = new GenericMessage<>("some payload");

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doInvoke(mockCallback, null, incomingMessage);

        verify(mockCallback).execute();
    }
}