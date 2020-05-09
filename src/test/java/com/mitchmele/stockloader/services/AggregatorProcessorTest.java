package com.mitchmele.stockloader.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AggregatorProcessorTest {

    @Mock
    TradeMatcherService mockTradeMatcherService;

    @InjectMocks
    AggregatorProcessor subject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        subject = new AggregatorProcessor();
    }


    @Test
    public void process_shouldHandleGroup() {

    }
}