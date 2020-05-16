package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

        assertThatThrownBy(() -> subject.insertEntity(new Bid("ABC", 25.00)))
                .isInstanceOf(IOException.class)
                .hasMessage("Entity: Bid(type=BID, symbol=ABC, bidPrice=25.0, ENTITY_TYPE=BID) threw an exception on insert with message: bad news");
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

        assertThatThrownBy(() -> subject.insertEntity(new Ask("ABC", 4.75)))
                .isInstanceOf(IOException.class)
                .hasMessage("Entity: Ask(type=ASK, symbol=ABC, askPrice=4.75, ENTITY_TYPE=ASK) threw an exception on insert with message: bad news");
    }

    @Test
    public void insertTrades_success_shouldCallRepoInsertAll() throws IOException {
        Trade inputStock = new Trade("ABC", 2.50, LocalDate.now());
        Trade inputStock2 = new Trade("ABC", 2.75, LocalDate.now());
        Trade inputStock3 = new Trade("ABC", 2.90, LocalDate.now());

        List<StockEntity> inputTrades = Arrays.asList(inputStock, inputStock2, inputStock3);

        subject.insertTrades(inputTrades);

        verify(mockRepo).insert(anyIterable());
    }

    @Test
    public void insertTrades_success_shouldReturnInsertedTrades() throws IOException {
        Trade inputStock = new Trade("ABC", 2.50, LocalDate.now());
        Trade inputStock2 = new Trade("ABC", 2.75, LocalDate.now());
        Trade inputStock3 = new Trade("ABC", 2.90, LocalDate.now());

        List<StockEntity> inputTrades = Arrays.asList(inputStock, inputStock2, inputStock3);

        when(mockRepo.insert(anyIterable())).thenReturn(inputTrades);

        List<StockEntity> actual = subject.insertTrades(inputTrades);

        assertThat(actual).isEqualTo(inputTrades);
    }

    @Test
    public void insertTrades_failure_shouldThrowIOException_whenRepoInsertErrors() {
        Trade inputStock = new Trade("ABC", 2.50, LocalDate.now());
        Trade inputStock2 = new Trade("ABC", 2.75, LocalDate.now());
        Trade inputStock3 = new Trade("ABC", 2.90, LocalDate.now());

        List<StockEntity> inputTrades = Arrays.asList(inputStock, inputStock2, inputStock3);

        when(mockRepo.insert(anyIterable())).thenThrow(new RuntimeException("bad news bears"));

        assertThatThrownBy(() -> subject.insertTrades(inputTrades))
                .isInstanceOf(IOException.class)
                .hasMessage("Trades for Symbol: ABC threw an exception on insert with message: bad news bears");
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