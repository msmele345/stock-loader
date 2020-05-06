package com.mitchmele.stockloader.config;

import com.mitchmele.stockloader.services.MessageErrorAdvice;
import com.mitchmele.stockloader.services.StockHandler;
import com.mitchmele.stockloader.services.StockTransformer;
import com.mitchmele.stockloader.services.StocksRouter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@EnableBinding(StocksBinder.class)
@ComponentScan("com.mitchmele.*")
public class LoaderConfig {

    @Bean
    @Qualifier("errorQueue")
    MessageChannel errorQueue() {
        return new DirectChannel();
    }
    @Bean
    @Qualifier("output")
    MessageChannel output() {
        return new DirectChannel();
    }

    @Bean
    MessagingTemplate messagingTemplate() {
        return new MessagingTemplate();
    }

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
    IntegrationFlow stockProcessingFlow(
            StockTransformer transformer,
            StockHandler handler,
            MessageErrorAdvice messageErrorAdvice
    ) {
        return IntegrationFlows.from("stocksQueue")
                .transform(transformer, (e) -> e.advice(messageErrorAdvice).requiresReply(false))
                .log(message -> "STOCK PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
                .get();
    }

    @Bean
    IntegrationFlow bidProcessingFlow(
            StockTransformer transformer,
            StockHandler handler,
            MessageErrorAdvice messageErrorAdvice
    ) {
        return IntegrationFlows.from("bidsQueue")
                .transform(transformer, (e) -> e.advice(messageErrorAdvice).requiresReply(false))
                .log(message -> "BID PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
                .get();
    }

    @Bean
    IntegrationFlow askProcessingFlow(
            StockTransformer transformer,
            StockHandler handler,
            MessageErrorAdvice messageErrorAdvice
    ) {
        return IntegrationFlows.from("asksQueue")
                .transform(transformer, (e) -> e.advice(messageErrorAdvice).requiresReply(false))
                .log(message -> "ASK PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
                .get();
    }

    @Bean
    IntegrationFlow errorsFlow() {
        return IntegrationFlows.from("errorQueue")
                .log(message -> "RECEIVED ERROR" + message.getPayload().toString())
                .channel(Source.OUTPUT)
                .get();
    }
}
