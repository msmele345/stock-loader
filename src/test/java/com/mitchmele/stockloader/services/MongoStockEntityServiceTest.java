package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.MongoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;
import java.time.LocalDate;

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
//        subject = new StockProcessor(mockMongoClient);
    }

    @Test
    public void process_shouldCallTheMongoSaveMethod_providedIncomingValidMessage() throws IOException {
        Trade payload = new Trade("ABC", 25.00, LocalDate.now());
        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .build();

        subject.process(incomingMessage);
        Mockito.verify(mockMongoClient).insertEntity(captor.capture());
    }

    @Test
    public void process_shouldCallMongoInsert_providedMessageWithTypeBid() throws IOException {
        Bid payload = new Bid("ABC", 23.50);

        Message<?> incomingMessage = MessageBuilder
                .withPayload(payload)
                .setHeader("Type", "BID")
                .build();

        subject.process(incomingMessage);

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

        subject.process(incomingMessage);
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

        assertThatThrownBy(() -> subject.process(incomingMessage))
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