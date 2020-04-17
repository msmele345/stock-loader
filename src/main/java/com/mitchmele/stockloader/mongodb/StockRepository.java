package com.mitchmele.stockloader.mongodb;


import com.mitchmele.stockloader.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends MongoRepository<Stock, String> { }
