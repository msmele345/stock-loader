package com.mitchmele.stockloader.utils;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationErrorUtilsTest {

    @Test
    public void messageAsString_success_returnsStringIfPayloadIsByteArray() {
        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload".getBytes())
                .setHeader("Type", "Bid")
                .build();


        String actual = ValidationErrorUtils.messageAsString(incomingMessage);
        assertThat(actual).isEqualTo("some payload");
    }

    @Test
    public void messageAsString_success_returnsStringIfPayloadIsString() {
        Message<?> incomingMessage = MessageBuilder
                .withPayload("some payload")
                .setHeader("Type", "any")
                .build();


        String actual = ValidationErrorUtils.messageAsString(incomingMessage);
        assertThat(actual).isEqualTo("some payload");
    }

    @Test
    public void prettyException_shouldParseExceptionMessageInShortFormat() {
        String localizedMessage = "lastPrice is marked non-null but is null at [Source: (String)\"{\"symbol\":\"ABC\",\"bid\":2.3,\"offer\":2.4,\"lastPrice\":null}\"; line: 1, column: 51] (through reference chain: com.mitchmele.stockloader.model.Stock[\"lastPrice\"])";

        String actual = ValidationErrorUtils.prettyException(localizedMessage);

        String expected = "lastPrice is marked non-null but is null";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void parseValidationError_shouldDisplayMeaningfulMessage() {
        String errorMessage = "Transform ErrorMessage -> org.springframework.integration.transformer.MessageTransformationException: failed to transform message; nested exception is com.mitchmele.stockloader.common.ValidationException, failedMessage=GenericMessage [payload=byte[32], headers={amqp_receivedDeliveryMode=PERSISTENT, amqp_receivedExchange=stocks-exchange, amqp_deliveryTag=2, file_name=mcdBid.json, deliveryAttempt=1, amqp_consumerQueue=stocks-exchange.anonymous.Lxf7AomzRnquv7XhTvWBtA, amqp_redelivered=false, file_originalFile=./BOOT-INF/classes/static/mcdBid.json, file_relativePath=mcdBid.json, amqp_contentEncoding=UTF-8, Type=BID, amqp_timestamp=Mon May 11 10:26:04 CDT 2020, amqp_messageId=1295f8c4-c698-38de-ec23-f431b9cb73f9, id=971bf0e1-e6e8-11eb-3ad5-da9c276f4f94, amqp_consumerTag=amq.ctag-Ljonpi6iUZmprVv1Dlc7yQ, sourceData=(Body:'{\"symbol\":\"MCD\",\"bidPrice\":null}' MessageProperties [headers={Type=BID, file_name=mcdBid.json, file_originalFile=./BOOT-INF/classes/static/mcdBid.json, file_relativePath=mcdBid.json}, timestamp=Mon May 11";

        String actual = ValidationErrorUtils.parseValidationError(errorMessage);

        String expected = "Transform ErrorMessage -> org.springframework.integration.transformer.MessageTransformationException: failed to transform message; nested exception is com.mitchmele.stockloader.common.ValidationException";

        assertThat(actual).isEqualTo(expected);

    }
}