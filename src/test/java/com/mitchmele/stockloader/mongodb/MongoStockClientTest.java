package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoStockClientTest {

    MongoStockClient subject;
    StockRepository mockRepo = mock(StockRepository.class);

    @BeforeEach
    void setUp() {
        subject = new MongoStockClient(mockRepo);
    }

    @Test
    public void getAllStocks_success_shouldCallRepoFindAll() throws IOException {
        subject.getAllStocks();

        verify(mockRepo).findAll();
    }

    @Test
    public void getAllStocks_success_shouldReturnListOfStocks() throws IOException {
        Stock stock1 = new Stock("TTY", 2.00, 2.50, 2.50);
        Stock stock2 = new Stock("TSLA", 200.25, 202.50, 201.00);
        Stock stock3 = new Stock("GILD", 114.67, 114.90, 114.80);

        List<Stock> expectedList = Arrays.asList(stock1, stock2, stock3);

        when(mockRepo.findAll()).thenReturn(expectedList);

        List<Stock> actual = subject.getAllStocks();

        assertThat(actual).isEqualTo(expectedList);
    }


//  Junit 5: assertThrows(IOException.class, () -> subject.getAllStocks());
    @Test
    public void getAllStocks_failure_shouldThrowIOExceptionIfRepoFindAll_fails() throws IOException {

        when(mockRepo.findAll()).thenThrow(new RuntimeException("something bad happened"));

        assertThatThrownBy(() -> subject.getAllStocks())
                .isInstanceOf(IOException.class)
                .hasMessage("Mongo Error: something bad happened");
    }
}