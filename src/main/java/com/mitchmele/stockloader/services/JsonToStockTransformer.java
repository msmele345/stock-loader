package com.mitchmele.stockloader.services;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mitchmele.stockloader.common.ValidationError;
import com.mitchmele.stockloader.common.ValidationErrorType;
import com.mitchmele.stockloader.common.ValidationException;
import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Stock;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.mitchmele.stockloader.utils.ValidationErrorUtils.messageAsString;

@Service
@NoArgsConstructor
public class JsonToStockTransformer extends AbstractTransformer {

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
//            ValidationError error = new ValidationError(e.getCause().getMessage(), ValidationErrorType.DATA_INVALID, null);
            throw new ValidationException(jsonParseError);

        } catch (JsonMappingException e) {
            ValidationError error = new ValidationError(e.getCause().getMessage(), ValidationErrorType.DATA_INVALID, null);
            throw new ValidationException(error);
        }
    }
    //check
    private ValidationError createJsonParseError(String field, Throwable cause) {
        return new ValidationError("JSON-parse", ValidationErrorType.DATA_INVALID, cause);
    }
    private final ValidationError jsonMappingError = new ValidationError("JSON-mapping", ValidationErrorType.DATA_INVALID, null);
    private final ValidationError jsonParseError = new ValidationError("JSON-parse", ValidationErrorType.DATA_INVALID, null);

}
