package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class MongoStockClient {

    StockRepository stockRepository;

    private static final Logger logger = LoggerFactory.getLogger(MongoStockClient.class);

    public MongoStockClient(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public List<Stock> getAllStocks() throws IOException {
        try {
            return stockRepository.findAll();
        } catch (Exception ex) {
            throw new IOException("Mongo Error: " + ex.getLocalizedMessage());
        }
    }

    public void insertStock(Stock stock) throws IOException {
        try {
            stockRepository.insert(stock);
            logSuccess(stock);
        } catch (Exception e) {
            String msg = String.format("Stock: %s has an exception on insert with message %s", stock, e.getLocalizedMessage());
            throw new IOException(msg);
        }
    }

    public List<Stock> batchInsertStocks(List<Stock> stocks) throws IOException {
        try {
            stockRepository.saveAll(stocks);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Mongo Error: " + e.getLocalizedMessage());
        }
        return stocks;
    }

    private void logSuccess(Stock stock) {
        String msg = String.format("Stock: %s has been successfully written to Mongo", stock.getSymbol());
        logger.info(msg);
    }
}
