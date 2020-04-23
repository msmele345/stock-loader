package com.mitchmele.stockloader.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "stocks")
public class Stock {

    @Id
    String symbol;
    Double bid;
    Double offer;
    Double lastPrice;

    final static String DEFAULT_SYMBOL = "XYZ";

    public Stock() {
        this.symbol = DEFAULT_SYMBOL;
        this.bid = 4.00;
        this.offer = 4.50;
        this.lastPrice = 4.25;
    }


    public Stock(String symbol, Double bid, Double offer, Double lastPrice) {
        this.symbol = symbol;
        this.bid = bid;
        this.offer = offer;
        this.lastPrice = lastPrice;
    }
}
