package com.mitchmele.stockloader.mongodb;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import com.mongodb.MongoClient;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
//        stockRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void insert_success_shouldInsertStock() {
        Stock inputStock = new Stock("ABC", 2.50, 2.75, 2.60);

        Stock actual = stockRepository.insert(inputStock);

        assertThat(actual).isEqualTo(inputStock);
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

//        org.springframework.data.mongodb.core.query.Query query = new Query();
//        query.addCriteria(Criteria.where("symbol").is("QQW"));

        List<Bid> actual = stockRepository.findBySymbolIgnoreCase("TTY");

        List<StockEntity> expectedResult = Collections.singletonList(bid1);
        assertThat(actual).isEqualTo(expectedResult);
    }
}