package com.mitchmele.stockloader.services.transformers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchmele.stockloader.model.Bid;

public class BidTransformer implements StockEntityTransformer<Bid> {

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Bid transform(String inJson) {
        try {
           return mapper.readValue(inJson, Bid.class);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
