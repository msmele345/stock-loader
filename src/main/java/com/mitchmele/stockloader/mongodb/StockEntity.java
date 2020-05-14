package com.mitchmele.stockloader.mongodb;

public interface StockEntity {
    String getSymbol();
    String getType();
    Double getPrice();
}
//Regular Track
//Send large batches of bids and offers and change release strategy to what???? 10 to start?
//Figure out how to manage re-sending bids/offers in groups that dont match in price.

//ERROR
//DLQ and retries
//set max delivery attempts in config
