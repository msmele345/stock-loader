package com.mitchmele.stockloader.mongodb;


import com.mitchmele.stockloader.model.Stock;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoStockFilter {

    public List<Stock> filterPennyStocks(List<Stock> stocks) {
        return stocks.stream()
                .filter(stock -> stock.getLastPrice() > 1.00)
                .collect(Collectors.toList());
    }
}
