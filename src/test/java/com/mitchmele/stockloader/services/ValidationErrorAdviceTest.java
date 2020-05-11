package com.mitchmele.stockloader.services;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

//@RunWith(Parameterized.class)
@Ignore
class ValidationErrorAdviceTest extends ValidationErrorAdvice {


    private static MessageChannel mockErrorChannel;
    private static MessagingTemplate mockTemplate;
//    @Qualifier("mockTemplate")
//    MessagingTemplate mockTemplate = mock(MessagingTemplate.class);


//    @Qualifier("mockErrorChannel")
//    MessageChannel mockErrorChannel;

    @BeforeClass
    public static void setUp() {
        mockErrorChannel = mock(MessageChannel.class);
        mockTemplate = mock(MessagingTemplate.class);
    }


    public ValidationErrorAdviceTest() {
        super(mockTemplate, mockErrorChannel);
    }


    @ParameterizedTest
    public void doInvoke_shouldCall_ExecutionCallback() {
        GenericMessage<String> incomingMessage = new GenericMessage<>("some payload");

        ExecutionCallback mockCallback = mock(ExecutionCallback.class);

        doInvoke(mockCallback, null, incomingMessage);

        verify(mockCallback).execute();
    }
}