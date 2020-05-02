package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MongoClientTest {

    MongoClient subject;
    StockRepository mockRepo = mock(StockRepository.class);

    @BeforeEach
    void setUp() {
        subject = new MongoClient(mockRepo);
    }


//  Junit 5: assertThrows(IOException.class, () -> subject.getAllStocks());

    @Test
    public void insertBid_success_shouldWriteBidToRepo() throws IOException {
        Bid incomingBid = new Bid("ABC", 4.5);

        when(mockRepo.insert((StockEntity) any())).thenReturn(incomingBid);

        Bid actual = subject.insertBid(incomingBid);
        verify(mockRepo).insert(any(StockEntity.class));
        assertThat(actual).isEqualTo(incomingBid);
    }

    @Test
    public void insertBid_failure_shouldThrowIOException() throws IOException {
        when(mockRepo.insert((StockEntity) any())).thenThrow(new RuntimeException("bad news"));

        assertThatThrownBy(() -> subject.insertBid(new Bid()))
                .isInstanceOf(IOException.class)
                .hasMessage("Bid: Bid(symbol=null, bidPrice=null) has an exception on insert with message: bad news");
    }

    @Test
    public void insertAsk_success_shouldWriteAskToRepo() throws IOException {
        Ask incomingAsk = new Ask("ABC", 4.75);

        when(mockRepo.insert((StockEntity) any())).thenReturn(incomingAsk);

        Ask actual = subject.insertAsk(incomingAsk);
        verify(mockRepo).insert(any(StockEntity.class));
        assertThat(actual).isEqualTo(incomingAsk);
    }

    @Test
    public void insertAsk_failure_shouldThrowIOExceptionIfInsertFails() {
        when(mockRepo.insert((StockEntity) any())).thenThrow(new RuntimeException("bad news"));

        assertThatThrownBy(() -> subject.insertAsk(new Ask()))
                .isInstanceOf(IOException.class)
                .hasMessage("Ask: Ask(type=null, symbol=null, askPrice=null, ENTITY_TYPE=ASK) has an exception on insert with message: bad news");
    }
}