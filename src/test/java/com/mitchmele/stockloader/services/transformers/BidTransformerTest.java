package com.mitchmele.stockloader.services.transformers;

import com.mitchmele.stockloader.model.Bid;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BidTransformerTest {


    BidTransformer subject;

    @Before
    public void setUp() throws Exception {
        subject = new BidTransformer();
    }

    @Test
    public void transform_success_shouldAcceptJsonAndDeserializeToBid() {
        Bid expectedBid = new Bid("ABC", 45.67);

        String incomingJson = "{\"type\":\"BID\",\"symbol\":\"ABC\",\"bidPrice\":45.67}";

        Bid actual = subject.transform(incomingJson);
        assertThat(actual).isEqualTo(expectedBid);
    }
}