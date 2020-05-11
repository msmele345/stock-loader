package com.mitchmele.stockloader.mongodb;

public interface StockEntity {
    String getSymbol();
    String getType();
    Double getPrice();
}
//Regular Track
//Figure out how to manage re-sending bids/offers in groups that dont match in price.
//Correlation strategy with symbol and price?
//test on multiple messages with multiple same bids and offers

//ERROR
//DLQ and retries
//set max delivery attempts in config
