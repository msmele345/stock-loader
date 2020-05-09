package com.mitchmele.stockloader.config;

import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.services.*;
import com.rabbitmq.client.Delivery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.stream.Collectors;

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


//    @Bean
//    IntegrationFlow inboundGatewayFlow(
//            StocksRouter router
//    ) {
//        return IntegrationFlows.from(Sink.INPUT)
//                .log(message -> "ROUTER GOT MESSAGE WITH HEADERS: " + message.getHeaders().toString())
//                .route(router)
//                .get();
//    }

    @Bean
    ReleaseStrategy releaseStrategy() {
        return new ReleaseStrategy() {
            @Override
            public boolean canRelease(MessageGroup group) {
                return group.getMessages().size() == 2;
            }
        };
    }

    @Bean
    CorrelationStrategy correlationStrategy() {
        return new CorrelationStrategy() {
            @Override
            public Object getCorrelationKey(Message<?> message) {
                //get symbol to group by SYMBOL and bring in trade aggregator to check/insert trade?
                return message.getHeaders().get("Symbol");
            }
        };
    }


    @Bean
    IntegrationFlow transactionFlow(
            StockTransformer transformer,
            AggregatorProcessor processor,
            MessageErrorAdvice messageErrorAdvice
    ) {
        return IntegrationFlows.from(Sink.INPUT)
                .transform(transformer,
                        (e) -> e.advice(messageErrorAdvice).requiresReply(false))
                .enrichHeaders(h ->
                        h.headerExpression("Symbol", "payload.symbol.toString()"))
                .aggregate(e ->
                        e.outputProcessor(processor)
                                .releaseStrategy(releaseStrategy())
                                .correlationStrategy(correlationStrategy())
                )
                .log(message -> "AGGREGATE PAYLOAD AFTER: " + message.getPayload().toString())
                .channel("tradeProcessing")
                .get();
    }

    @Bean
    IntegrationFlow tradeProcessingFlow(
            StockHandler handler,
            MessageErrorAdvice messageErrorAdvice
    ) {
        return IntegrationFlows.from("tradeProcessing")
                .log(message -> "RECEIVED TRADE: " + message.getPayload().toString())
                .handle(handler, e -> e.advice(messageErrorAdvice).requiresReply(false))
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
                .channel(StocksBinder.OUTPUT)
                .get();
    }
}
