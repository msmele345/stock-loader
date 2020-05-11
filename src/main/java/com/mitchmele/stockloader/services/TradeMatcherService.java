package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//correlation strategy groups messages by symbol
//aggregator matches trades by price of bid/offer and returns message with payload of trade
@Service
public class TradeMatcherService {

    public static Trade createTransaction(List<StockEntity> entities) {

        Trade newTrade = null;

        Map<Boolean, List<StockEntity>> matchedTrades = entities
                .stream()
                .collect(Collectors.partitioningBy(typeCheck("BID")));
        List<List<StockEntity>> subSets = new ArrayList<List<StockEntity>>(matchedTrades.values());

        List<StockEntity> asks = subSets.get(0);
        List<StockEntity> bids = subSets.get(1);

        List<Double> askPrices = asks.stream().map(StockEntity::getPrice).collect(Collectors.toList());

        for (int i = 0; i < bids.size(); i++) {
            LocalDate currentTime = LocalDate.now();
            if (askPrices.contains(bids.get(i).getPrice())) {
                newTrade = (new Trade(
                        bids.get(i).getSymbol(),
                        bids.get(i).getPrice(),
                        currentTime
                ));
            }
        }
        return newTrade;
    }

    public static Predicate<StockEntity> typeCheck(String type) {
        return p -> p.getType().equals(type);
    }

    public Trade matchTrade(List<StockEntity> entities) {
        String symbol = entities.get(0).getSymbol();
        Trade newTrade = null;

        if (isMatch(entities)) {
            Double tradePrice = entities.get(0).getPrice();
            newTrade = new Trade(symbol, tradePrice, LocalDate.now());
        }
        return newTrade;
    }

    public boolean isMatch(List<StockEntity> entities) {
        return entities.stream().allMatch(priceCheck(entities.get(0).getPrice()));
    }

    //p matches type in <>
    public static Predicate<StockEntity> priceCheck(Double price) {
        return p -> p.getPrice().equals(price);
    }
}
