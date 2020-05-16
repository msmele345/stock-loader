package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.MongoClient;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class MongoStockEntityServiceTest {

    @Mock
    MongoClient mockMongoClient;

    @InjectMocks
    MongoStockEntityService subject;

    @Captor
    ArgumentCaptor<Trade> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void process_shouldCallTheMongoClientInsertEntity_providedIncomingValidSingleTradeMessage() throws IOException {
        Trade payload = new Trade("ABC", 25.00, LocalDate.now());
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .build();

        subject.processSingleEntity(incomingMessage);
        Mockito.verify(mockMongoClient).insertEntity(captor.capture());
    }

    @Test
    public void process_shouldCallTheMongoClientInsertTrades_providedIncomingValidTradesMessage() throws IOException {
        Trade trade1 = new Trade("ABC", 25.00, LocalDate.now());
        Trade trade2 = new Trade("ABC", 26.00, LocalDate.now());
        Trade trade3 = new Trade("ABC", 27.00, LocalDate.now());

        List<StockEntity> payload = Arrays.asList(trade1, trade2, trade3);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .build();

        ArgumentCaptor<List<StockEntity>> captor = ArgumentCaptor.forClass(List.class);

        subject.processTrades(incomingMessage);
        Mockito.verify(mockMongoClient).insertTrades(captor.capture());
    }


    @Test
    public void process_shouldCallMongoInsert_providedMessageWithTypeBid() throws IOException {
        Bid payload = new Bid("ABC", 23.50);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "BID")
                .build();

        subject.processSingleEntity(incomingMessage);

        ArgumentCaptor<Bid> bidCaptor = ArgumentCaptor.forClass(Bid.class);

        verify(mockMongoClient).insertEntity(bidCaptor.capture());
    }

    @Test
    public void process_shouldCallMongoInsert_providedMessageWithTypeAsk() throws IOException {

        Ask payload = new Ask("ABC", 23.75);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "ASK")
                .build();

        subject.processSingleEntity(incomingMessage);
        ArgumentCaptor<Ask> askCaptor = ArgumentCaptor.forClass(Ask.class);

        verify(mockMongoClient).insertEntity(askCaptor.capture());
    }

    @Test
    public void process_failure_shouldThrowIOExceptionIfMongoInsertFails() throws IOException {
        Trade payload = new Trade("ABC", 25.00, LocalDate.now());
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .build();
        //use doThrow or doAnswer in front when mocking void methods
        doThrow(new RuntimeException("something bad")).when(mockMongoClient).insertEntity(any());

        assertThatThrownBy(() -> subject.processSingleEntity(incomingMessage))
                .isInstanceOf(IOException.class)
                .hasMessage("something bad");
    }

    @Test
    public void process_failure_shouldThrowIOExceptionIfMongoInsertAllFails() throws IOException {
        Trade trade1 = new Trade("ABC", 25.00, LocalDate.now());
        Trade trade2 = new Trade("ABC", 26.00, LocalDate.now());
        Trade trade3 = new Trade("ABC", 27.00, LocalDate.now());

        List<StockEntity> payload = Arrays.asList(trade1, trade2, trade3);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .build();
        //use doThrow or doAnswer in front when mocking void methods
        doThrow(new RuntimeException("something bad")).when(mockMongoClient).insertTrades(any());

        assertThatThrownBy(() -> subject.processTrades(incomingMessage))
                .isInstanceOf(IOException.class)
                .hasMessage("something bad");
    }
}
/* DO ANSWER EX
      doAnswer(e -> {
            System.out.println("Answer");
            throw new RuntimeException("bad again");
        }).when(mockMongoClient).insertEntity(any());
 */