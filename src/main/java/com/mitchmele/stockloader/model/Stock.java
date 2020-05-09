package com.mitchmele.stockloader.model;


import com.mitchmele.stockloader.mongodb.StockEntity;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stocks")
public class Stock implements StockEntity {


    String type;
    @NonNull
    String symbol;
    @NonNull
    Double bid;
    @NonNull
    Double offer;
    @NonNull
    Double lastPrice;

    final static String DEFAULT_SYMBOL = "XYZ";
    final static String ENTITY_TYPE = "STOCK";

    public Stock() {
        this.type = ENTITY_TYPE;
        this.symbol = DEFAULT_SYMBOL;
        this.bid = 4.00;
        this.offer = 4.50;
        this.lastPrice = 4.25;
    }


    public Stock(String symbol, Double bid, Double offer, Double lastPrice) {
        this.type = ENTITY_TYPE;
        this.symbol = symbol;
        this.bid = bid;
        this.offer = offer;
        this.lastPrice = lastPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public Double getPrice() {
        return this.lastPrice;
    }

    public Double getBid() {
        return bid;
    }

    public Double getOffer() {
        return offer;
    }

    public Double getLastPrice() {
        return lastPrice;
    }
}
