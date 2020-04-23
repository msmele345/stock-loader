package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Stock;
import com.mitchmele.stockloader.mongodb.MongoStockClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StockProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StockProcessor.class);

    MongoStockClient mongoStockClient;

    public StockProcessor(MongoStockClient mongoStockClient) {
        this.mongoStockClient = mongoStockClient;
    }

    public void process(Message<?> message) throws IOException {
        logger.info("PROCESSOR RECEIVED MESSAGE WITH PAYLOAD: " + message.getPayload());
        try {
            mongoStockClient.insertStock((Stock) message.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
}
