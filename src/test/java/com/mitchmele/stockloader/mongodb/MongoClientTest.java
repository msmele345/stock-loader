package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MongoClientTest {

    MongoClient subject;
//    MongoTemplate mongoTemplate = mock(MongoTemplate.class);
    StockRepository mockRepo = mock(StockRepository.class);

    @BeforeEach
    void setUp() {
        subject = new MongoClient(mockRepo);
    }

    @Test
    public void insertBid_success_shouldWriteBidToRepo() throws IOException {
        Bid incomingBid = new Bid("ABC", 4.5);

        when(mockRepo.insert((StockEntity) any())).thenReturn(incomingBid);

        StockEntity actual = subject.insertEntity(incomingBid);
        verify(mockRepo).insert(any(StockEntity.class));
        assertThat(actual).isEqualTo(incomingBid);
    }

    //  Junit 5: assertThrows(IOException.class, () -> subject.getAllStocks());
    @Test
    public void insertBid_failure_shouldThrowIOException() throws IOException {
        when(mockRepo.insert((StockEntity) any())).thenThrow(new RuntimeException("bad news"));

        assertThatThrownBy(() -> subject.insertEntity(new Bid()))
                .isInstanceOf(IOException.class)
                .hasMessage("Entity: Bid(type=null, symbol=null, bidPrice=null) threw an exception on insert with message: bad news");
    }

    @Test
    public void insert_success_shouldWriteAskToRepo() throws IOException {
        Ask incomingAsk = new Ask("ABC", 4.75);

        when(mockRepo.insert((StockEntity) any())).thenReturn(incomingAsk);

        StockEntity actual = subject.insertEntity(incomingAsk);
        verify(mockRepo).insert(any(StockEntity.class));
        assertThat(actual).isEqualTo(incomingAsk);
    }

    @Test
    public void insert_success_trade_shouldWriteTradeToRep() throws IOException {
        Trade incomingTrade = new Trade("ABC", 25.00, LocalDate.now());
        when(mockRepo.insert(incomingTrade)).thenReturn(incomingTrade);

        StockEntity actual = subject.insertEntity(incomingTrade);
        verify(mockRepo).insert(incomingTrade);

        assertThat(actual).isEqualTo(incomingTrade);
    }

    @Test
    public void insertAsk_failure_shouldThrowIOExceptionIfInsertFails() {
        when(mockRepo.insert((StockEntity) any())).thenThrow(new RuntimeException("bad news"));

        assertThatThrownBy(() -> subject.insertEntity(new Ask()))
                .isInstanceOf(IOException.class)
                .hasMessage("Ask: Ask(type=null, symbol=null, askPrice=null, ENTITY_TYPE=ASK) has an exception on insert with message: bad news");
    }

    @Test
    public void getTypePretty_shouldReturnOnlyTypeFromCanonicalName() {
        String inputString = "com.mitchmele.stockloader.model.Bid";

        String actual = subject.getTypePretty(inputString);

        assertThat(actual).isEqualTo("Bid");
    }

    @Test//if the template is used
    public void findBySymbol_success_shouldCallMongoTemplate() { }
}