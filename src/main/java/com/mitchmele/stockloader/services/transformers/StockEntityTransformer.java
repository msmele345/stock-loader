package com.mitchmele.stockloader.services.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface StockEntityTransformer<T> {
    T transform(String inJson) throws JsonProcessingException;
}
