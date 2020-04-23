package com.mitchmele.stockloader.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mitchmele.stockloader.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JsonToStocksTransformer extends AbstractTransformer {

    final static Logger logger = LoggerFactory.getLogger(JsonToStocksTransformer.class);

    @Override
    protected List<Stock> doTransform(Message<?> message) {
        if (message != null) {
            String payload = message.getPayload().toString();
            try {
                Gson gson = new Gson();
                //convert to json object or use gson helper for byte array
                return gson.fromJson(payload, new TypeToken<List<Stock>>() {}.getType());
            } catch (JsonSyntaxException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
