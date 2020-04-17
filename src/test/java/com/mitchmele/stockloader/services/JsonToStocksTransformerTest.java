package com.mitchmele.stockloader.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class JsonToStocksTransformerTest {


    JsonToStocksTransformer subject;

    @BeforeEach
    void setUp() {
        subject = new JsonToStocksTransformer();
    }

    @Test
    public void doTransform_() {
        Stock stock1 = new Stock("TTY", 2.00, 2.50, 2.50);
        Stock stock2 = new Stock("TSLA", 200.25, 202.50, 201.00);
        Stock stock3 = new Stock("GILD", 114.67, 114.90, 114.80);

        List<Stock> expectedList = Arrays.asList(stock1, stock2, stock3);

        Message<String> incomingMessage = MessageBuilder
                .withPayload(toStockJson(expectedList))
                .build();

        List<Stock> actual = subject.doTransform(incomingMessage);

        assertThat(actual).isEqualTo(expectedList);
    }


    public String toStockJson(List<Stock> stocks) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(stocks);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}