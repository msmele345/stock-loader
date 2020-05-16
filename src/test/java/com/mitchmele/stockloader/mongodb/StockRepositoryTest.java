package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import com.mitchmele.stockloader.model.Trade;
import com.mongodb.MongoClient;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StockRepositoryTest.TestConfig.class)
class StockRepositoryTest {

    @Autowired
    StockRepository stockRepository;

    @Configuration
    @EnableMongoRepositories("com.mitchmele.*")
    static class TestConfig extends AbstractMongoConfiguration {

        @Override
        protected String getDatabaseName() {
            return "test-stocks";
        }

        @Override
        public MongoClient mongoClient() {
            return new MongoClient("localhost", 27017);
        }
    }

    @BeforeEach
     void setUp() {
        stockRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
//        stockRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void insert_success_shouldInsertTrade() {
        Trade inputStock = new Trade("ABC", 2.50, LocalDate.now());

        Trade actual = stockRepository.insert(inputStock);

        assertThat(actual).isEqualTo(inputStock);
    }

    @Test
    public void insert_trades_shouldInsertListOfTrades() {
        Trade inputStock = new Trade("ABC", 2.50, LocalDate.now());
        Trade inputStock2 = new Trade("ABC", 2.75, LocalDate.now());
        Trade inputStock3 = new Trade("ABC", 2.90, LocalDate.now());

        List<StockEntity> inputTrades = Arrays.asList(inputStock, inputStock2, inputStock3);

        List<StockEntity> actual = stockRepository.insert(inputTrades);
        assertThat(actual).hasSize(3);
        assertThat(actual).isEqualTo(inputTrades);
    }

    @Test
    public void insertBid_success_shouldInsertBidToBidCollection() {
        Bid inputBid = new Bid("UUP", 56.78);

        Bid actual = stockRepository.insert(inputBid);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(inputBid);
    }

    @Test
    public void insertAsk_success_shouldInsertBidToBidCollection() {
        Ask inputAsk = new Ask("UUP", 57.00);

        Ask actual = stockRepository.insert(inputAsk);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(inputAsk);
    }

//   Ask[] asks = {ask1, ask2, ask3};
    @Test
    @Ignore("No requirement for this functionality yet")
    public void fetchBySymbol_success_shouldFetchAllRecordsBySymbol() {
        Bid bid1 = new Bid("TTY", 4.50);
        Bid bid2 = new Bid("VVB", 10.50);
        Bid bid3 = new Bid("QQW", 100.90);

        List<Bid> bids = Arrays.asList(bid1, bid2, bid3);

        Ask ask1 = new Ask("TTY", 4.75);
        Ask ask2 = new Ask("VVB", 10.75);
        Ask ask3 = new Ask("QQW", 101.00);

        List<Ask> asks = Arrays.asList(ask1, ask2, ask3);

        stockRepository.insert(bids);
        stockRepository.insert(asks);

        stockRepository.insert(new Ask("ASK", 80.00));

        List<Bid> actual = stockRepository.findBySymbolIgnoreCase("TTY");

        List<StockEntity> expectedResult = Collections.singletonList(bid1);
        assertThat(actual).isEqualTo(expectedResult);
    }
}