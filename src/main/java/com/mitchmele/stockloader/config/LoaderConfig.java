package com.mitchmele.stockloader.config;

import com.mitchmele.stockloader.services.AggregatorProcessor;
import com.mitchmele.stockloader.services.JsonToStockTransformer;
import com.mitchmele.stockloader.services.StockHandler;
import com.mitchmele.stockloader.services.ValidationErrorAdvice;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

@Configuration
@EnableIntegration
@EnableBinding(StocksBinder.class)
@ComponentScan("com.mitchmele.*")
public class LoaderConfig {

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
                //and price? TODO
                return message.getHeaders().get("Symbol");
            }
        };
    }
                //what if two messages of same type get grouped? Need to prevent only bid/ask groups!!


    @Bean
    IntegrationFlow transactionFlow(
            JsonToStockTransformer transformer,
            AggregatorProcessor processor,
            ValidationErrorAdvice validationErrorAdvice
    ) {
        return IntegrationFlows.from(Sink.INPUT)
                .transform(transformer,
                        (e) -> e.advice(validationErrorAdvice).requiresReply(false))
                .enrichHeaders(h ->
                        h.headerExpression("Symbol", "payload.symbol.toString()"))
                .aggregate(e ->
                        e.outputProcessor(processor)
                                .releaseStrategy(releaseStrategy())
                                .correlationStrategy(correlationStrategy())
                )
                .channel("tradeProcessing")
                .get();
    }

    @Bean
    IntegrationFlow tradeProcessingFlow(
            StockHandler handler,
            ValidationErrorAdvice validationErrorAdvice
    ) {
        return IntegrationFlows.from("tradeProcessing")
                .log(message -> "RECEIVED TRADE: " + message.getPayload().toString())
                .handle(handler, e -> e.advice(validationErrorAdvice).requiresReply(false))
                .get();
    }

    @Bean
    IntegrationFlow bidProcessingFlow(
            JsonToStockTransformer transformer,
            StockHandler handler,
            ValidationErrorAdvice validationErrorAdvice
    ) {
        return IntegrationFlows.from("bidsQueue")
                .transform(transformer, (e) -> e.advice(validationErrorAdvice).requiresReply(false))
                .log(message -> "BID PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
                .get();
    }

    @Bean
    IntegrationFlow askProcessingFlow(
            JsonToStockTransformer transformer,
            StockHandler handler,
            ValidationErrorAdvice validationErrorAdvice
    ) {
        return IntegrationFlows.from("asksQueue")
                .transform(transformer, (e) -> e.advice(validationErrorAdvice).requiresReply(false))
                .log(message -> "ASK PAYLOAD SINGLE AFTER: " + message.getPayload().toString())
                .handle(handler)
                .get();
    }
}
