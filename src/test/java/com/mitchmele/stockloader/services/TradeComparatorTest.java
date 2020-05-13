package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TradeComparatorTest {

    TradeComparator subject;

    @Before
    public void setUp() throws Exception {
        subject = new TradeComparator();
    }

    @Test
    public void compare_shouldReturn0_ifPricesMatch() {
        Bid bid1 = new Bid("ABC", 25.00); //1 if greater
        Ask ask1 = new Ask("ABC", 25.00); //-1 if greater

        int actual = subject.compare(bid1, ask1);
        assertThat(actual).isEqualTo(0);
    }

    @Test
    public void compare_shouldReturn1_if01Price_isGreater() {
        Bid bid1 = new Bid("ABC", 26.00); //1 if greater
        Ask ask1 = new Ask("ABC", 25.00); //-1 if greater

        int actual = subject.compare(bid1, ask1);
        assertThat(actual).isEqualTo(1);
    }

    @Test
    public void compare_shouldReturnNegative1_if02Price_isGreater() {
        Bid bid1 = new Bid("ABC", 25.00); //1 if greater
        Ask ask1 = new Ask("ABC", 26.00); //-1 if greater

        int actual = subject.compare(bid1, ask1);
        assertThat(actual).isEqualTo(-1);
    }
}