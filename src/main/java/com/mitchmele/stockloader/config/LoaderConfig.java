package com.mitchmele.stockloader.config;

import com.mitchmele.stockloader.services.JsonToStocksTransformer;
import com.mitchmele.stockloader.services.StocksRouter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@EnableIntegration
@EnableBinding(StocksBinder.class)
@ComponentScan("com.mitchmele.*")
public class LoaderConfig {

    @Bean
    DirectChannel errorQueue() {
        return new DirectChannel();
    }

    //setup transformer for singles
    //setup message handler that writes to mongo for singles
    //fine tune error flow

    @Bean
    IntegrationFlow stocksGatewayFlow(
            StocksRouter router
    ) {
        return IntegrationFlows.from(Sink.INPUT)
                .log(message -> message.getHeaders().toString())
                .route(router)
                .get();
    }

    @Bean
    IntegrationFlow singleStocksFlow() {
        return IntegrationFlows.from("singleStocks")
                .log(message -> "PAYLOAD SINGLE" + message.getPayload().toString())
                .get();
    }


    @Bean
    IntegrationFlow batchStocksFlow(
            JsonToStocksTransformer jsonToStocksTransformer
    ) {
        return IntegrationFlows.from("batchStocks")
                .log(message ->  "PAYLOAD BATCH" + message.getPayload().toString())
//                .transform(Transformers.toJson())
                .transform(jsonToStocksTransformer)
                .log(message ->  "MESSAGE AFTER" + message.getPayload().toString())
                .get();
    }

    @Bean
    IntegrationFlow errorsFlow() {
        return IntegrationFlows.from("errorQueue")
                .log(message -> "RECEIVED ERROR" + message.getPayload().toString())
                .get();
    }
}
