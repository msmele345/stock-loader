package com.mitchmele.stockloader.mongodb;

public interface StockEntity {
    String getSymbol();
    String getType();
}


//Regular Track
//Make handler/processor check for type and call appropriate mongo insert
//look at aggregator to group bids/offers of same symbol
//create handler that accepts both bids and offers and has business logic that creates the trade (same symbol) pojo and stores it in mongo

//ERROR Flow
//send stock with null value to debug
//re-visit bindings