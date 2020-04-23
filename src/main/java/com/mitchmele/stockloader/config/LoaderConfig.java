package com.mitchmele.stockloader.config;

import com.mitchmele.stockloader.services.JsonToStocksTransformer;
import com.mitchmele.stockloader.services.StockHandler;
import com.mitchmele.stockloader.services.StockTransformer;
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

    @Bean
    @Qualifier("singleOutput")
    DirectChannel singleOutput() {
        return new DirectChannel();
    }

    @Bean
    @Qualifier("batchOutput")
    DirectChannel batchOutput() {
        return new DirectChannel();
    }


    //MAIN TRACK:
    //Setup producer to bind queue/exchange.
    // This will prevent me from having to create queues to keep track of ALL messages
    //ADD integration tests and re-evaluate config (with creds?)
    //add error advice with error queue

    @Bean
    IntegrationFlow inboundGatewayFlow(
            StocksRouter router
    ) {
        return IntegrationFlows.from(Sink.INPUT)
                .log(message -> "ROUTER GOT MESSAGE WITH HEADERS: " + message.getHeaders().toString())
                .route(router)
                .get();
    }

    @Bean
    IntegrationFlow singleStocksFlow(
            StockTransformer transformer,
            StockHandler handler
    ) {
        return IntegrationFlows.from("singleStocks")
                .log(message -> "PAYLOAD SINGLE BEFORE: " + message.getPayload().toString())
                .transform(transformer)
                .log(message -> "PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
//                .channel(StocksBinder.SINGLE_STOCK_OUTPUT) //for errors later
                .get();
    }

    @Bean
    IntegrationFlow batchStocksFlow(
            JsonToStocksTransformer jsonToStocksTransformer
    ) {
        return IntegrationFlows.from("batchStocks")
                .log(message ->  "PAYLOAD BATCH: " + message.getPayload().toString())
//                .transform(Transformers.toJson())
                .transform(jsonToStocksTransformer)
                .log(message ->  "MESSAGE AFTER: " + message.getPayload().toString())
//                .channel(StocksBinder.BATCH_STOCKS_OUTPUT) //for errors later
                .get();
    }

    @Bean
    IntegrationFlow errorsFlow() {
        return IntegrationFlows.from("errorQueue")
                .log(message -> "RECEIVED ERROR" + message.getPayload().toString())
                .get();
    }
}
