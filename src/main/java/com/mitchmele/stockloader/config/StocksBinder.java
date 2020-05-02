package com.mitchmele.stockloader.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.cloud.stream.messaging.Sink;

interface StocksBinder {

    String ERRORS  = "errorQueue";
    String OUTPUT = "output";

    @Input(Sink.INPUT)
    SubscribableChannel input();

    @Output(OUTPUT) //check
    MessageChannel output();
}
