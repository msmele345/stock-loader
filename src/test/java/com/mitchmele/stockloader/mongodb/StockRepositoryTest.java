package com.mitchmele.stockloader.mongodb;

import com.mongodb.MongoClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StockRepositoryTest.TestConfig.class)
class StockRepositoryTest {


    @Configuration
    @EnableMongoRepositories("com.mitchmele.*")
    static class TestConfig extends AbstractMongoConfiguration {

        @Override
        protected String getDatabaseName() {
            return "test-stocks";
        }

        @Override
        public MongoClient mongoClient() {
            return new MongoClient("localhost", 27018);
        }
    }

    @Test
    public void contextLoads() {

    }


}