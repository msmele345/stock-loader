package com.mitchmele.stockloader.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

interface StocksBinder {
    //this matches app yml bindings names
    String EXPECTED_ERROR = "expectedErrorsQueue";

    @Input(Sink.INPUT)
    SubscribableChannel input();

    @Output(EXPECTED_ERROR) //check
    MessageChannel expectedErrorsQueue();
}
//create channel
//create binding with same name
//send errors to that channel in advice
//integration flow from channel name
//output channel is the same (key difference)