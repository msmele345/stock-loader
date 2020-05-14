package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
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

    public List<Trade> createTransaction(List<StockEntity> entities) {

        List<Trade> trades = new ArrayList<>();

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

        return trades;
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
        //iterate though list of bids and offers with same price
        //create trade with bid/offer
        //remove that bid and offer
        //leave bids or offers that dont have matching counterpart
        //return list of matched trades

        return null;
    }

    public boolean isMatch(List<StockEntity> entities) {
        StockEntity first = entities.get(0);
        StockEntity second = entities.get(1);

        if(first.getType() != second.getType()) {
            return first.getPrice().equals(second.getPrice());
        }
        return false;
    }

    public Set<StockEntity> mapBidsOffers(List<StockEntity> entities) {
        Map<Boolean, List<StockEntity>> matchedTrades = entities
                .stream()
                .collect(Collectors.partitioningBy(typeCheck("BID")));
        List<List<StockEntity>> subSets = new ArrayList<List<StockEntity>>(matchedTrades.values());

        List<StockEntity> asks = subSets.get(0);
        List<Double> bids = subSets.get(1).stream()
                .map(StockEntity::getPrice)
                .collect(Collectors.toList());

        return asks.stream()
                .filter(entity -> bids.contains(entity.getPrice()))
                .collect(Collectors.toSet());
    }

    //p matches type in <>
    public static Predicate<StockEntity> typeCheck(String type) {
        return p -> p.getType().equals(type);
    }
    public static Predicate<StockEntity> priceCheck(Double price) {
        return p -> p.getPrice().equals(price);
    }

    //this just maps bids and offers together from a list - not helpful rn
    public static List<Map.Entry<StockEntity, StockEntity>> zipBids(List<Bid> bids, List<Ask> asks) {
        return IntStream.range(0, Math.min(bids.size(), asks.size()))
                //if prices match, create the entry
                .mapToObj(i ->
                        new AbstractMap.SimpleEntry<StockEntity, StockEntity>(bids.get(i), asks.get(i)))
                .collect(Collectors.toList());

    }
}
/*
  Comparator<StockEntity> byPrice = (StockEntity bid, StockEntity ask) -> bid.getPrice().intValue() - ask.getPrice().intValue();
       byPrice.compare(bid, ask)
   */