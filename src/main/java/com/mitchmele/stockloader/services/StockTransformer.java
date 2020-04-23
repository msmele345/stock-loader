package com.mitchmele.stockloader.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchmele.stockloader.model.Stock;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.StreamParsingException;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
@NoArgsConstructor
public class StockTransformer extends AbstractTransformer {

    @SneakyThrows
    @Override
    protected Object doTransform(Message<?> message) {
        String payload = messageAsString(message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, Stock.class);

        } catch (JsonParseException e) {
            e.printStackTrace();
            throw new StreamParsingException("Unable To Parse Json", e);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String messageAsString(Message<?> message) {
        return message.getPayload() instanceof String
                ? (String) message.getPayload()
                : new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
    }
}
