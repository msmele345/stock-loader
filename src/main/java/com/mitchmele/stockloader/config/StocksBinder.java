package com.mitchmele.stockloader.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.cloud.stream.messaging.Sink;

interface StocksBinder {

    String ERRORS  = "errors";
//    String SINGLE_STOCK_OUTPUT  = "singleOutput";
//    String BATCH_STOCKS_OUTPUT  = "batchOutput";


    @Input(Sink.INPUT)
    SubscribableChannel input();


    @Output(ERRORS)
    MessageChannel errorQueue();

//    @Output(SINGLE_STOCK_OUTPUT)
//    SubscribableChannel singleOutput();
//
//    @Output(BATCH_STOCKS_OUTPUT)
//    SubscribableChannel batchOutput();

}
