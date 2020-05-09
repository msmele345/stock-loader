package com.mitchmele.stockloader.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class AggregatorHandler implements MessageHandler {

    Logger logger = LoggerFactory.getLogger(AggregatorHandler.class);

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        logger.info("AGGREGATOR PAYLOAD: " + message.getPayload());
    }
}
