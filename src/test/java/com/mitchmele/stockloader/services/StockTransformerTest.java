package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Stock;
import org.bouncycastle.util.StreamParsingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class StockTransformerTest {

//    ObjectMapper mockMapper = mock(ObjectMapper.class);
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
                .setHeader("STOCK_TYPE", "single")
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
}