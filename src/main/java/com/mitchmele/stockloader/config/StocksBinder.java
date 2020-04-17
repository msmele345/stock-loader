package com.mitchmele.stockloader.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.cloud.stream.messaging.Sink;

interface StocksBinder {

    String ERRORS  = "errors";


    @Input(Sink.INPUT)
    SubscribableChannel input();

//
    @Output(ERRORS)
    MessageChannel errorQueue();

}
