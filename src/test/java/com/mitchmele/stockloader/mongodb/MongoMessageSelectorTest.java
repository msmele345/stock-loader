package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoMessageSelectorTest {

    MongoStockFilter subject = new MongoStockFilter();

    @Test
    public void filterPennyStocks_success_shouldReturnListOfStocksOverADollar() {

        Stock stock1 = new Stock("AAPL", 200.50, 200.75, 200.60);
        Stock stock2 = new Stock("MSFT", 100.50, 100.75, 100.52);
        Stock stock3 = new Stock("JNJ", 131.76, 131.80, 131.78);
        Stock stock4 = new Stock("POS", .76, .80, .90);

        List<Stock> incomingStocks = Arrays.asList(stock1, stock2, stock3, stock4);

        List<Stock> expected = Arrays.asList(stock1, stock2, stock3);

        List<Stock> actual = subject.filterPennyStocks(incomingStocks);

        assertThat(actual).isEqualTo(expected);
        assertThat(actual).hasSize(3);
    }
}