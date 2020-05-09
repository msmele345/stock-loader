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

@Service
public class AggregatorProcessor implements MessageGroupProcessor {
    //need correlation strategy to be on symbol for this to work
    //return list of trades as custom message
    //pass entities to createTransaction method to create list of trades in message (to later be inserted to Mongo)
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
            logger.info("TRADE MADE: " + trade.toString());
            return trade;
        }
        return null;
    };
}
