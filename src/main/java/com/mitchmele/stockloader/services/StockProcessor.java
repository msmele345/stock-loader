package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import com.mitchmele.stockloader.mongodb.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class StockProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StockProcessor.class);

    MongoClient mongoClient;

    public StockProcessor(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void process(Message<?> message) throws IOException {
        logger.info("PROCESSOR RECEIVED MESSAGE WITH PAYLOAD: " + message.getPayload());

        String type = Objects.requireNonNull(message.getHeaders().get("Type")).toString();
        try {
            switch (type) {
                case "BID":
                    mongoClient.insertBid((Bid) message.getPayload());
                    return;
                case "ASK":
                    mongoClient.insertAsk((Ask) message.getPayload());
                    return;
                case "STOCK":
                    mongoClient.insertStock((Stock) message.getPayload());
                    return;
                default:
                    logger.info("PROCESSOR ERROR: Type Not Recognized");
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}

enum StockEntityType {
    BID("BID"),
    ASK("ASK"),
    STOCK("STOCK");

    public final String value;

    private StockEntityType(String value) {
        this.value = value;
    }
}
