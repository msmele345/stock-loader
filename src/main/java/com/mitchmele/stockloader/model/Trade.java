package com.mitchmele.stockloader.model;

import com.mitchmele.stockloader.mongodb.StockEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@NoArgsConstructor
@Document(collection = "trades")
public class Trade implements StockEntity {

    public String symbol;
    public Double tradePrice;
    public LocalDate timeOfTrade;
    public String exchange;

    final static String DEFAULT_EXCHANGE = "NASDAQ";
    final String ENTITY_TYPE = "TRADE";


    public Trade(String symbol, Double tradePrice, LocalDate timeOfTrade) {
        this.symbol = symbol;
        this.tradePrice = tradePrice;
        this.timeOfTrade = timeOfTrade;
        this.exchange = DEFAULT_EXCHANGE;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getType() {
        return ENTITY_TYPE;
    }

    @Override
    public Double getPrice() {
        return tradePrice;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public LocalDate getTimeOfTrade() {
        return timeOfTrade;
    }

    public void setTimeOfTrade(LocalDate timeOfTrade) {
        this.timeOfTrade = timeOfTrade;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
//final static - Only one copy of variable exists which can't be reinitialize.