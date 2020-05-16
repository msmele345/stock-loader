package com.mitchmele.stockloader.model;

import com.mitchmele.stockloader.mongodb.StockEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "bids")
public class Bid implements StockEntity {

    String type;
    @NonNull
    String symbol;
    @NonNull
    Double bidPrice;

    final String ENTITY_TYPE = "BID";

    public Bid(String symbol, Double bidPrice) {
        this.type = ENTITY_TYPE;
        this.symbol = symbol;
        this.bidPrice = bidPrice;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public String getType() {
        return ENTITY_TYPE;
    }

    @Override
    public Double getPrice() {
        return this.bidPrice;
    }
}
