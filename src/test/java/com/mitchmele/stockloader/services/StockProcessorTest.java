package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import com.mitchmele.stockloader.mongodb.MongoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class StockProcessorTest {

    @Mock
    MongoClient mockMongoClient;

    @InjectMocks
    StockProcessor subject;

    @Captor
    ArgumentCaptor<Stock> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
//        subject = new StockProcessor(mockMongoClient);
    }

    @Test
    public void process_shouldCallTheMongoSaveMethod_providedIncomingValidMessage() throws IOException {
        Stock payload = new Stock();
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "STOCK")
                .build();

        subject.process(incomingMessage);
        Mockito.verify(mockMongoClient).insertStock(captor.capture());
    }

    @Test
    public void process_shouldCallMongoInsertBid_providedMessageWithTypeBid() throws IOException {
        Bid payload = new Bid("ABC", 23.50);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "BID")
                .build();

        subject.process(incomingMessage);

        ArgumentCaptor<Bid> bidCaptor = ArgumentCaptor.forClass(Bid.class);

        verify(mockMongoClient).insertBid(bidCaptor.capture());
    }

    @Test
    public void process_shouldCallMongoInsertAsk_providedMessageWithTypeAsk() throws IOException {

        Ask payload = new Ask("ABC", 23.75);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "ASK")
                .build();

        subject.process(incomingMessage);
        ArgumentCaptor<Ask> askCaptor = ArgumentCaptor.forClass(Ask.class);

        verify(mockMongoClient).insertAsk(askCaptor.capture());
    }

    @Test
    public void process_failure_shouldThrowIOExceptionIfMongoInsertFails() throws IOException {
        Message<?> incomingMessage = MessageBuilder
                .withPayload(new Stock())
                .setHeader("Type", "STOCK")
                .build();
        //use doThrow or doAnswer in front when mocking void methods
        doThrow(new RuntimeException("something bad")).when(mockMongoClient).insertStock(any());

        assertThatThrownBy(() -> subject.process(incomingMessage))
                .isInstanceOf(IOException.class)
                .hasMessage("something bad");
    }
}