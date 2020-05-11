package com.mitchmele.stockloader.services;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class StockHandler implements MessageHandler {

    MongoStockEntityService mongoStockEntityService;

    public StockHandler(MongoStockEntityService mongoStockEntityService) {
        this.mongoStockEntityService = mongoStockEntityService;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            mongoStockEntityService.process(message);
        } catch (Exception e) {
            throw new MessagingException(e.getLocalizedMessage());
        }
    }
}
