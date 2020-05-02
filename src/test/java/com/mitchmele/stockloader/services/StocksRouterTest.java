package com.mitchmele.stockloader.services;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import static org.assertj.core.api.Java6Assertions.assertThat;

class StocksRouterTest {

    StocksRouter subject = new StocksRouter();

    @Test
    public void route_shouldRouteMessageToSingleStockQueue_ifHeadersContainSingle() {
        String expectedRoute = "stocksQueue";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload")
                .setHeader("Type", "STOCK")
                .build();

        String actual = subject.route(incomingMessage);
        assertThat(actual).isEqualTo(expectedRoute);
    }

    @Test
    public void route_shouldRouteBidssToBidsQueue_ifHeadersContainBidType() {

        String expectedRoute = "bidsQueue";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some batch payload")
                .setHeader("Type", "BID")
                .build();

        String actual = subject.route(incomingMessage);

        assertThat(actual).isEqualTo(expectedRoute);
    }

    @Test
    public void route_shouldRouteAsksToAsksQueue_ifHeadersContainAskType() {

        String expectedRoute = "asksQueue";

        Message<?> incomingMessage = MessageBuilder
                .withPayload("some batch payload")
                .setHeader("Type", "ASK")
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