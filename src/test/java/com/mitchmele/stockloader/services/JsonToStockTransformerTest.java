package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.common.ValidationError;
import com.mitchmele.stockloader.common.ValidationErrorType;
import com.mitchmele.stockloader.common.ValidationException;
import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonToStockTransformerTest {

    JsonToStockTransformer subject;

    @BeforeEach
    void setUp() {
        subject = new JsonToStockTransformer();
    }

    @Test
    public void doTransform_success_shouldCallMapperonIncomingPayload() throws IOException {

        Stock expectedStock = new Stock("BBB", 200.25, 202.5, 201.0);

        String incomingPayload = "{\n" +
                "    \"symbol\": \"BBB\",\n" +
                "    \"bid\": 200.25,\n" +
                "    \"offer\": 202.5,\n" +
                "    \"lastPrice\": 201.0\n" +
                "  }";

        Message<?> incomingMessage;
        incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("Type", "STOCK")
                .build();

        Object actual = subject.doTransform(incomingMessage);
        assertThat(actual).isEqualTo(expectedStock);
    }

    @Test
    public void doTransform_failure_shouldThrowJsonParseExceptionIfInvalidPayload() {
        Ask expectedAsk = new Ask("ABC", 2.75);

        String incomingPayload = "{\"type\":\"ASK\",\"symbol\":\"ABC\",\"askPrice\":}";

        Message<?> incomingMessage;
        incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("Type", "ASK")
                .build();

        ValidationError expectedError = new ValidationError("JSON-parse", ValidationErrorType.DATA_INVALID, null);

        ValidationException expected = new ValidationException(expectedError);

        assertThatThrownBy(() -> subject.doTransform(incomingMessage))
                .isInstanceOf(ValidationException.class)
                .isEqualToComparingFieldByFieldRecursively(expected);

    }

    @Test
    public void doTransform_success_bid_shouldSerializeBid() {
        Bid expectedBid = new Bid("ABC", 2.50);

        String incomingPayload = "{\"type\":\"BID\",\"symbol\":\"ABC\",\"bidPrice\":2.5}";

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("Type", "BID")
                .build();

        Object actual = subject.doTransform(incomingMessage);
        assertThat(actual).isEqualTo(expectedBid);
    }

    @Test
    public void doTransform_success_ask_shouldSerializeAsk() {
        Ask expectedAsk = new Ask("ABC", 2.75);

        String incomingPayload = "{\"type\":\"ASK\",\"symbol\":\"ABC\",\"askPrice\":2.75}";

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("Type", "ASK")
                .build();

        Object actual = subject.doTransform(incomingMessage);
        assertThat(actual).isEqualTo(expectedAsk);
    }

    @Test
    public void doTransform_failure_shouldCatchNPEs() {
        String incomingPayload = "{\"type\":\"BID\",\"symbol\":\"ABC\",\"bidPrice\":null}";

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("Type", "BID")
                .build();

        ValidationError expectedError = new ValidationError("JSON-mapping", ValidationErrorType.DATA_INVALID, null);

        ValidationException expected = new ValidationException(expectedError);

        assertThatThrownBy(() -> subject.doTransform(incomingMessage))
                .isInstanceOf(ValidationException.class);
//                .isEqualToComparingFieldByField(expected);
    }
}