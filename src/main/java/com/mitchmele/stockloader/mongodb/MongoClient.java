package com.mitchmele.stockloader.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MongoClient {

    StockRepository stockRepository;

    private static final Logger logger = LoggerFactory.getLogger(MongoClient.class);

    public MongoClient(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public StockEntity insertEntity(StockEntity incomingEntity) throws IOException {
        try {
            StockEntity newEntity = stockRepository.insert(incomingEntity);
            logEntitySuccess(newEntity);
            return newEntity;
        } catch (Exception e) {
            String msg = String.format("Entity: %s threw an exception on insert with message: %s", incomingEntity, e.getLocalizedMessage());
            throw new IOException(msg);
        }
    }

    private void logEntitySuccess(StockEntity entity) {
        String type = getTypePretty(entity.getType());
        String msg = String.format("Stock Entity Type: %s with Symbol: %S has been successfully written to Mongo", type, entity.getSymbol());
        logger.info(msg);
    }

    protected String getTypePretty(String inputString) {
        return inputString.substring(inputString.length() - 5);
    }
}
