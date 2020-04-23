package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class StockHandlerTest {

    StockProcessor mockProcessor = mock(StockProcessor.class);

    StockHandler subject;

    @BeforeEach
    void setUp() {
        subject = new StockHandler(mockProcessor);
    }

    @Test
    public void handleMessage_shouldCallProcessor() throws IOException {
        Stock payload = new Stock();
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("STOCK_TYPE", "single")
                .build();

        subject.handleMessage(incomingMessage);
        Mockito.verify(mockProcessor).process(any());
    }

    @Test
    public void handleMessage_shouldThrowEx_ifProcessorFails() throws IOException {
        Stock payload = new Stock();
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("STOCK_TYPE", "single")
                .build();

        doThrow(new RuntimeException("Bad message")).when(mockProcessor).process(any());

        assertThatExceptionOfType(MessagingException.class)
                .isThrownBy(() -> subject.handleMessage(incomingMessage))
                .withMessage("Bad message");

    }

}