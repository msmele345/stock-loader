package com.mitchmele.stockloader.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.StreamParsingException;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@NoArgsConstructor
public class StockTransformer extends AbstractTransformer {

    @SneakyThrows
    @Override
    protected Object doTransform(Message<?> message) {
        String payload = messageAsString(message);
        MessageHeaders headers = message.getHeaders();
        String type = Objects.requireNonNull(headers.get("Type")).toString();

        try {
            ObjectMapper mapper = new ObjectMapper();
            switch (type) {
                case "BID":
                    return mapper.readValue(payload, Bid.class);
                case "ASK":
                    return mapper.readValue(payload, Ask.class);
                case "STOCK":
                    return mapper.readValue(payload, Stock.class);
                default:
                    return null;
            }

        } catch (JsonParseException e) {
            throw new StreamParsingException("Unable To Parse Json", e);

        } catch (JsonMappingException e) {
            throw new Exception(prettyException(e.getLocalizedMessage()));
        }
    }

    public String messageAsString(Message<?> message) {
        return message.getPayload() instanceof String
                ? (String) message.getPayload()
                : new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
    }

    public String prettyException(String localizedMessage) {
        return localizedMessage.split(" at")[0];
    }
}
