package com.mitchmele.stockloader.mongodb;

public interface StockEntity {
    String getSymbol();
    String getType();
    Double getPrice();
}


//Regular Track
//test on multiple messages with multiple same bids and offers
//Figure out how to manage re-sending bids/offers in groups that dont match in price.


//ERROR Flow
//send stock with null value to debug
//re-visit bindings!!!