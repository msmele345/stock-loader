package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Stock;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class StockHandlerTest {

    MongoStockEntityService mockProcessor = mock(MongoStockEntityService.class);

    StockHandler subject;

    @BeforeEach
    void setUp() {
        subject = new StockHandler(mockProcessor);
    }

    @Test
    public void handleMessage_shouldCallProcessor() throws IOException {
        Trade payload = new Trade("ABC", 25.00, LocalDate.now());
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "TRADE")
                .build();

        subject.handleMessage(incomingMessage);
        Mockito.verify(mockProcessor).processSingleEntity(any());
    }

    @Test
    public void handleMessage_shouldCallProcessTrades() throws IOException {
        Trade trade1 = new Trade("ABC", 25.00, LocalDate.now());
        Trade trade2 = new Trade("ABC", 26.00, LocalDate.now());
        Trade trade3 = new Trade("ABC", 27.00, LocalDate.now());

        List<StockEntity> payload = Arrays.asList(trade1, trade2, trade3);


        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "TRADE")
                .build();

        subject.handleMessage(incomingMessage);
        Mockito.verify(mockProcessor).processTrades(any());
    }

    @Test
    public void identifyPayload_success_shouldReturnEntityTypeSingle_provided_singleEntity() {
        Message<?> incomingMessage = asMessage(new Trade("ABC", 25.00, LocalDate.now()));

        EntityPayloadType actual = subject.identifyPayload(incomingMessage);
        assertThat(actual).isEqualTo(EntityPayloadType.SINGLE_TRADE);
    }

    @Test
    public void identifyPayload_success_shouldReturnEntityTypeBatch_provided_listOfEntity() {
        Trade trade1 = new Trade("ABC", 25.00, LocalDate.now());
        Trade trade2 = new Trade("ABC", 26.00, LocalDate.now());
        Trade trade3 = new Trade("ABC", 27.00, LocalDate.now());

        List<StockEntity> trades = Arrays.asList(trade1, trade2, trade3);

        Message<?> incomingMessage = asMessage(trades);

        EntityPayloadType actual = subject.identifyPayload(incomingMessage);
        assertThat(actual).isEqualTo(EntityPayloadType.BATCH_TRADES);
    }

    @Test
    public void handleMessage_shouldThrowEx_ifProcessorFails() throws IOException {
        Stock payload = new Stock();
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("STOCK_TYPE", "single")
                .build();

        doThrow(new RuntimeException("Bad message")).when(mockProcessor).processSingleEntity(any());

        assertThatExceptionOfType(MessagingException.class)
                .isThrownBy(() -> subject.handleMessage(incomingMessage))
                .withMessage("Bad message");

    }

    public static Message<?> asMessage(Object payload) {
        return org.springframework.messaging.support.MessageBuilder
                .withPayload(payload)
                .build();
    }
}