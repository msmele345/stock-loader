package com.mitchmele.stockloader.model;

import com.mitchmele.stockloader.mongodb.StockEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "asks")
public class Ask implements StockEntity {

    String type;
    @NonNull
    String symbol;
    @NonNull
    Double askPrice;

    final String ENTITY_TYPE = "ASK";

    public Ask(String symbol, Double askPrice) {
        this.type = ENTITY_TYPE;
        this.symbol = symbol;
        this.askPrice = askPrice;
    }

    public String getType() {
        return ENTITY_TYPE;
    }

    @Override
    public Double getPrice() {
        return this.askPrice;
    }
}
