package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//correlation strategy groups messages by symbol
//aggregator matches trades by price of bid/offer and returns message with payload of trade
@Service
public class TradeMatcherService {

    public List<Trade> createTransaction(List<StockEntity> entities) {

        List<Trade> trades = new ArrayList<>();

        Map<Boolean, List<StockEntity>> matchedTrades = entities
                .stream()
                .collect(Collectors.partitioningBy(typeCheck("BID")));
        List<List<StockEntity>> subSets = new ArrayList<List<StockEntity>>(matchedTrades.values());

        List<StockEntity> asks = subSets.get(0);
        List<StockEntity> bids = subSets.get(1);

        List<Double> askPrices = asks.stream().map(StockEntity::getPrice).collect(Collectors.toList());

        int range = Math.min(bids.size(), asks.size());

        for (int i = 0; i < range; i++) {
            LocalDate currentTime = LocalDate.now();
            if (askPrices.contains(bids.get(i).getPrice())) {
                trades.add(
                        new Trade(
                                bids.get(i).getSymbol(),
                                bids.get(i).getPrice(),
                                currentTime));
            }
            //remove from ask list?
            //re-send unused bids/offers to channel
        }
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

    public boolean isMatch(List<StockEntity> entities) {
        return entities.stream().allMatch(priceCheck(entities.get(0).getPrice()));
    }//checks price of first item and makes sure it matches the other in the message group

    //p matches type in <>

    public static Predicate<StockEntity> typeCheck(String type) {
        return p -> p.getType().equals(type);
    }
    public static Predicate<StockEntity> priceCheck(Double price) {
        return p -> p.getPrice().equals(price);
    }





    //THIS IS FOR IF AT SOME POINT WE NEED TO COMPARE PRICES OF MANY BIDS/OFFERS OF SAME SYMBOL
    public int hasMatches(List<StockEntity> entities) {
        TradeComparator tradeComparator = new TradeComparator();
        int count = 0;

        entities.forEach(e -> {

        });
        return 0;
    }
    //this just maps bids and offers together from a list - not helpful rn
    public static List<Map.Entry<StockEntity, StockEntity>> zipBids(List<Bid> bids, List<Ask> asks) {
        return IntStream.range(0, Math.min(bids.size(), asks.size()))
                //if prices match, create the entry
                .mapToObj(i ->
                        new AbstractMap.SimpleEntry<StockEntity, StockEntity>(bids.get(i), asks.get(i)))
                .collect(Collectors.toList());

    }

    /*
    Comparator<StockEntity> byPrice = (StockEntity bid, StockEntity ask) -> bid.getPrice().intValue() - ask.getPrice().intValue();
         byPrice.compare(bid, ask)
     */
}
