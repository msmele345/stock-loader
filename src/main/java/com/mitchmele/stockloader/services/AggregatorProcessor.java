package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//this is called after release strategy is satisfied
//return list of trades or trade as custom message
@Service
public class AggregatorProcessor implements MessageGroupProcessor {
    Logger logger = LoggerFactory.getLogger(AggregatorProcessor.class);

    TradeMatcherService tradeMatcherService;

    public AggregatorProcessor(TradeMatcherService tradeMatcherService) {
        this.tradeMatcherService = tradeMatcherService;
    }

    @Override
    public Object processMessageGroup(MessageGroup group) {
        logger.info("MESSAGE GROUP: " + group.getMessages().size());
        group.getMessages().forEach(m -> {
            System.out.println(m.getPayload());
        });

         List<StockEntity> entities = group.getMessages()
                .stream()
                .map(message -> (StockEntity) message.getPayload())
                .collect(Collectors.toList());

        Trade trade = tradeMatcherService.matchTrade(entities);
        if (trade != null) {
            logger.info("TRADES MADE: " + trade.toString());
            return trade;
        }
        return null;
    };
}
//cay @ 155.90
//HUD 25.50
//abc 2.75
//uso


/*
*         List<Trade> trades = tradeMatcherService.matchTrades(entities);
        if (trades != null && !trades.isEmpty()) {
            logger.info("TRADES MADE: " + trades.toString());
            return trades;
        }
        return null;
*
* */