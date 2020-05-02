package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class MongoClient {

    StockRepository stockRepository;

    private static final Logger logger = LoggerFactory.getLogger(MongoClient.class);

    public MongoClient(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    public void insertStock(Stock stock) throws IOException {
        try {
            stockRepository.insert(stock);
            logSuccess(stock);
        } catch (Exception e) {
            String msg = String.format("Stock: %s has an exception on insert with message %s", stock, e.getLocalizedMessage());
            throw new IOException(msg);
        }
    }

    public Bid insertBid(Bid bid) throws IOException {
        try {
            Bid bidInsert = stockRepository.insert(bid);
            logBidSuccess(bidInsert);
            return bidInsert;
        } catch (Exception e) {
            String msg = String.format("Bid: %s has an exception on insert with message: %s", bid, e.getLocalizedMessage());
            throw new IOException(msg);
        }
    }

    public Ask insertAsk(Ask incomingAsk) throws IOException {
        try {
            Ask askInsert = stockRepository.insert(incomingAsk);
            logAskSuccess(askInsert);
            return askInsert;
        } catch (Exception e) {
            String msg = String.format("Ask: %s has an exception on insert with message: %s", incomingAsk, e.getLocalizedMessage());
            throw new IOException(msg);
        }
    }

    private void logSuccess(Stock stock) {
        String msg = String.format("Stock: %s has been successfully written to Mongo", stock.getSymbol());
        logger.info(msg);
    }

    private void logBidSuccess(Bid bid) {
        String msg = String.format("Bid: %s has been successfully written to Mongo", bid.getSymbol());
        logger.info(msg);
    }

    private void logAskSuccess(Ask ask) {
        String msg = String.format("Ask: %s has been successfully written to Mongo", ask.getSymbol());
        logger.info(msg);
    }


}
