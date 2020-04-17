package com.mitchmele.stockloader.services;

import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class StocksRouter {

    final static String singleStock = "singleStocks";
    final static String batchStocks = "batchStocks";
    final static String errors = "errorQueue";

    @Router
    public String route(Message<?> message) {
        String stockType = (String) message.getHeaders().get("STOCK_TYPE");
        if (stockType != null) {
            switch (stockType) {
                case "single":
                    return singleStock;
                case "batch":
                    return batchStocks;
                default:
                    return errors;
            }
        }
        return errors;
    }
}
