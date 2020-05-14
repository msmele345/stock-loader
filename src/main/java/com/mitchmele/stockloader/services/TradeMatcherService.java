package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.springframework.data.mongodb.core.aggregation.AccumulatorOperators;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//correlation strategy groups messages by symbol
//aggregator matches trades by price of bid/offer and returns message with payload of trade
@Service
public class TradeMatcherService {

    public List<Trade> createTransactions(List<StockEntity> entities) {
        //input entities are bids/offers of different prices
        List<StockEntity> potentialTrades = entities.stream()
                //create Map<Double, List<StockEntity>> with groupBy
                .collect(Collectors.groupingBy(StockEntity::getPrice))
                //iterate over each map entry
                .entrySet().stream()
                //filter out values (List<StockEntity>) of size greater than 1 List<List<StockEntity>>
                .filter(entity -> entity.getValue().size() > 1)
                //flatMap to get values in list
                .flatMap(entity -> entity.getValue().stream())
                .collect(Collectors.toList());

        return matchTrades(potentialTrades); //pass potentialTrades to matchTrades function
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

    public List<Trade> matchTrades(List<StockEntity> entities) {
        //input entities is a list of bids/offers of the same price
        List<Trade> trades = new ArrayList<>();
        String symbol = entities.get(0).getSymbol();
        Double price = entities.get(0).getPrice();

        //count number of bids
        long bidCount = entities.stream()
                .filter(e -> e.getType().equals("BID"))
                .count();
        //count number of offers
        long askCount = entities.stream()
                .filter(e -> e.getType().equals("ASK"))
                .count();
        //use smaller of two counts as a number to iterate through and make trades
        long range = Long.min(bidCount, askCount);

        for (int i = 0; i < range; i++) {
            Trade newTrade = new Trade(symbol, price, LocalDate.now());
            trades.add(newTrade);
        }

        return trades;
    }

    public boolean isMatch(List<StockEntity> entities) {
        //input entities is a message group 2. Could be bids or asks with different prices
        StockEntity first = entities.get(0);
        StockEntity second = entities.get(1);

        if (first.getType() != second.getType()) {
            return first.getPrice().equals(second.getPrice());
        }
        return false;
    }
}
