package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import org.bouncycastle.util.StreamParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.io.IOException;
import static org.assertj.core.api.Assertions.*;

class StockTransformerTest {

    StockTransformer subject;

    @BeforeEach
    void setUp() {
        subject = new StockTransformer();
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
    public void doTransform_failure_shouldThrowJsonMappingExceptionIfInvalidPayload() {
        String incomingPayload = "{\n" +
                "    \"symbol\": \"BBB\",\n" +
                "    \"bid\": 200.25,\n" +
                "    \"offer\": 202.5,\n" +
                "    \"lastPrice\":" +
                "  }";

        Message<?> incomingMessage;
        incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("STOCK_TYPE", "single")
                .build();

        assertThatThrownBy(() -> subject.doTransform(incomingMessage))
                .isInstanceOf(StreamParsingException.class)
                .hasMessage("Unable To Parse Json");

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
    public void messageAsString_success_returnsStringIfPayloadIsByteArray() {
        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload".getBytes())
                .setHeader("STOCK_TYPE", "single")
                .build();


        String actual = subject.messageAsString(incomingMessage);
        assertThat(actual).isEqualTo("some payload");
    }

    @Test
    public void messageAsString_success_returnsStringIfPayloadIsString() {
        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload")
                .setHeader("STOCK_TYPE", "single")
                .build();


        String actual = subject.messageAsString(incomingMessage);
        assertThat(actual).isEqualTo("some payload");
    }

    @Test
    public void doTransform_failure_shouldCatchNPEs() {
        String incomingPayload = "{\"symbol\":\"ABC\",\"bid\":2.3,\"offer\":2.4,\"lastPrice\":null}";

        Message<?> incomingMessage = MessageBuilder
                .withPayload(incomingPayload)
                .setHeader("STOCK_TYPE", "single")
                .build();

        assertThatThrownBy(() -> subject.doTransform(incomingMessage))
                .isInstanceOf(Exception.class)
                .hasMessage("lastPrice is marked non-null but is null\n");

    }

    @Test
    public void prettyException_shouldParseExceptionMessageInShortFormat() {
        String localizedMessage = "lastPrice is marked non-null but is null at [Source: (String)\"{\"symbol\":\"ABC\",\"bid\":2.3,\"offer\":2.4,\"lastPrice\":null}\"; line: 1, column: 51] (through reference chain: com.mitchmele.stockloader.model.Stock[\"lastPrice\"])";

        String actual = subject.prettyException(localizedMessage);

        String expected = "lastPrice is marked non-null but is null";
        assertThat(actual).isEqualTo(expected);
    }
}