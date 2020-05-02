package com.mitchmele.stockloader.services;

import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class StocksRouter {

    final static String asks = "asksQueue";
    final static String bids = "bidsQueue";
    final static String stocks = "stocksQueue";
    final static String errors = "errorQueue";

    @Router
    public String route(Message<?> message) {
        String entityType = (String) message.getHeaders().get("Type");
        if (entityType != null) {
            switch (entityType) {
                case "BID":
                    return bids;
                case "ASK":
                    return asks;
                case "STOCK":
                    return stocks;
                default:
                    return errors;
            }
        }
        return errors;
    }
}
