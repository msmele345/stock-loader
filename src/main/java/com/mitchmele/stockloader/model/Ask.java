package com.mitchmele.stockloader.model;

import com.mitchmele.stockloader.mongodb.StockEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Data
@Document(collection = "asks")
public class Ask implements StockEntity {

    String type;
    String symbol;
    Double askPrice;

    final String ENTITY_TYPE = "ASK";

    public Ask(String symbol, Double askPrice) {
        this.type = ENTITY_TYPE;
        this.symbol = symbol;
        this.askPrice = askPrice;
    }
}
