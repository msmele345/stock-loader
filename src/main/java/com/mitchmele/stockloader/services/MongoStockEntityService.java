package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.mongodb.MongoClient;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MongoStockEntityService {

    private static final Logger logger = LoggerFactory.getLogger(MongoStockEntityService.class);

    MongoClient mongoClient;

    public MongoStockEntityService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void processSingleEntity(Message<?> message) throws IOException {
        logger.info("MONGO_ENTITY_SERVICE RECEIVED MESSAGE SINGLE TRADE: " + message.getPayload());

        try {
            StockEntity payload = (StockEntity) message.getPayload();
            mongoClient.insertEntity(payload);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public void processTrades(Message<?> message) throws IOException {
        logger.info("MONGO_ENTITY_SERVICE RECEIVED MESSAGE WITH TRADES: " + message.getPayload());
        try {
            List<StockEntity> payload = (List<StockEntity>) message.getPayload();
            mongoClient.insertTrades(payload);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}