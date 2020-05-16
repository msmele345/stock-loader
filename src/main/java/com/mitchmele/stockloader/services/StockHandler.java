package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.mongodb.StockEntity;
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

    public EntityPayloadType identifyPayload(Message<?> message) {
        if (message.getPayload() instanceof StockEntity) {
            return EntityPayloadType.SINGLE_TRADE;
        } else {
            return EntityPayloadType.BATCH_TRADES;
        }
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        EntityPayloadType type = identifyPayload(message);
        try {
            if (type.equals(EntityPayloadType.SINGLE_TRADE)) {
                mongoStockEntityService.processSingleEntity(message);
            } else {
                mongoStockEntityService.processTrades(message);
            }
        } catch (Exception e) {
            throw new MessagingException(e.getLocalizedMessage());
        }
    }
}


enum EntityPayloadType {
    SINGLE_TRADE,
    BATCH_TRADES;
}