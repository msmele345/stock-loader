package com.mitchmele.stockloader.mongodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.mitchmele.stockloader.utils.ValidationErrorUtils.prettyException;

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

    public List<StockEntity> insertTrades(List<StockEntity> inputTrades) throws IOException {
        try {
            return stockRepository.insert(inputTrades);
        } catch (Exception e) {
            String msg = String.format("Inserting TRADES threw an exception on insert with message: %s", e.getLocalizedMessage());
            logger.info(msg);
            throw new IOException(msg);
        }
    }

    private void logEntitySuccess(StockEntity entity) {
        String type = getTypePretty(entity.getType());
        String msg = String.format("Stock Entity Type: %s with Symbol: %S has been successfully written to Mongo", type, entity.getSymbol());
        logger.info(msg);
    }

    protected static String getTypePretty(String inputString) {
        return inputString.substring(inputString.length() - 3);
    }

}
