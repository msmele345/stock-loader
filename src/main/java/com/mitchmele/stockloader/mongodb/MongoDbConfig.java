package com.mitchmele.stockloader.mongodb;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories("com.mitchmele.*")
public class MongoDbConfig extends AbstractMongoConfiguration {

//    @Value("${spring.data.mongodb.uri}")
//    String mongoConnectionUri;
//
//    @Bean
//    public MongoDbFactory mongoDbFactory() {
//        return new SimpleMongoClientDbFactory(mongoConnectionUri);
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(mongoDbFactory());
//    }

//    @Bean
//    public MongoClient mongoClient() {
//        MongoClient mongoClient = new MongoClient("localhost", 27017);
//        mongoClient.getDatabase("stocks");
//        return mongoClient;
//    }


    @Override
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "tradeRepo");
    }

    @Override
    protected String getDatabaseName() {
        return "tradeRepo";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient("localhost", 27017);
    }
}
