package com.mitchmele.stockloader.mongodb;


import com.mitchmele.stockloader.model.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends MongoRepository<StockEntity, String> {

    List<Bid> findBySymbolIgnoreCase(String symbol);
    Iterable<StockEntity> findStockEntitiesBySymbolIgnoreCase(String symbol);
}

//fetch all bids and asks then compare with a service?
//