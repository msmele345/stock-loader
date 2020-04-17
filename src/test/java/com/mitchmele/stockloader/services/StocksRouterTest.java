package com.mitchmele.stockloader.services;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import static org.assertj.core.api.Java6Assertions.assertThat;

class StocksRouterTest {

    StocksRouter subject = new StocksRouter();

    @Test
    public void route_shouldRouteMessageToSingleStockQueue_ifHeadersContainSingle() {
        String expectedRoute = "singleStocks";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload")
                .setHeader("STOCK_TYPE", "single")
                .build();

        String actual = subject.route(incomingMessage);
        assertThat(actual).isEqualTo(expectedRoute);
    }

    @Test
    public void route_shouldRouteMessageToBatchStocksQueue_ifHeadersContainBatch() {

        String expectedRoute = "batchStocks";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some batch payload")
                .setHeader("STOCK_TYPE", "batch")
                .build();

        String actual = subject.route(incomingMessage);

        assertThat(actual).isEqualTo(expectedRoute);
    }

    @Test
    public void route_failure_shouldRouteToErrorsIfHeadersNotValid() {

        String expectedRoute = "errorQueue";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some bad payload")
                .build();

        String actual = subject.route(incomingMessage);
        assertThat(actual).isEqualTo(expectedRoute);
    }
}