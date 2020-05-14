package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TradeMatcherServiceTest {

    TradeMatcherService subject;
    LocalDate tradeTime = LocalDate.of(2020, 5, 7);

    @Before
    public void setUp() throws Exception {
        subject = new TradeMatcherService();
    }

    @Test
    public void createTransaction_success_shouldReturnListOfUniqueTrades() {
        Bid bid1 = new Bid("ABC", 25.00);//1
        Bid bid4 = new Bid("ABC", 25.05);//2
        Bid bid2 = new Bid("ABC", 23.00);//no match
        Bid bid3 = new Bid("ABC", 22.75);//3

        Ask ask1 = new Ask("ABC", 25.00);//1
        Ask ask4 = new Ask("ABC", 25.05);//2
        Ask ask6 = new Ask("ABC", 25.05);//extra
        Ask ask2 = new Ask("ABC", 22.75);//3
        Ask ask3 = new Ask("ABC", 25.25);//no match

        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, ask1, ask2, ask3, ask4, ask6);

        List<Trade> actual = subject.createTransactions(entities);
        assertThat(actual.size()).isEqualTo(3);
    }

    @Test
    public void matchTrades_shouldReturnListOfTradesWhenBidsAndOfferCounts_DontMatch() {
        Bid bid1 = new Bid("ABC", 25.00);
        Bid bid2 = new Bid("ABC", 25.00);

        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, bid2, ask1);

        List<Trade> actual = subject.matchTrades(entities);
        assertThat(actual).hasSize(1);
    }

    @Test
    public void matchTrades_shouldReturnListOfTrades() {
        Bid bid1 = new Bid("ABC", 25.00);
        Bid bid2 = new Bid("ABC", 25.00);

        Ask ask1 = new Ask("ABC", 25.00);
        Ask ask2 = new Ask("ABC", 25.00);
        Ask ask3 = new Ask("ABC", 25.00);
        Ask ask4 = new Ask("ABC", 25.00);
        List<StockEntity> entities = Arrays.asList(bid1, bid2, ask1, ask2, ask3, ask4);

        List<Trade> actual = subject.matchTrades(entities);
        assertThat(actual).hasSize(2);
    }

    @Test
    public void matchTrade_shouldCreateTrade() {

        Bid bid1 = new Bid("ABC", 25.00);
        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, ask1);

        Trade expected = new Trade("ABC", 25.00, tradeTime);

        Trade actual = subject.matchTrade(entities);

        assertThat(actual).isEqualToIgnoringGivenFields(expected, "timeOfTrade");
    }

    @Test
    public void matchTrade_shouldReturnNullIfPrices_dontMatch() {
        Bid bid1 = new Bid("ABC", 24.00);
        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, ask1);

        Trade actual = subject.matchTrade(entities);
        assertThat(actual).isNull();
    }

    @Test
    public void isMatch_success_shouldReturnTrueIfPricesMatchAndTypesAreDiff() {
        Bid bid1 = new Bid("ABC", 25.0);
        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, ask1);

        boolean actual = subject.isMatch(entities);
        assertThat(actual).isTrue();
    }

    @Test
    public void isMatch_failure_shouldReturnFalseIfTypesAreSame() {
        Ask ask1 = new Ask("ABC", 25.00);
        Ask ask2 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(ask1, ask2);

        boolean actual = subject.isMatch(entities);
        assertThat(actual).isFalse();
    }

    @Test
    public void isMatch_failure_shouldReturnFalseIfPricesDontMatch_providedValidTypes() {
        Bid bid1 = new Bid("ABC", 25.00);
        Ask ask1 = new Ask("ABC", 25.05);

        List<StockEntity> entities = Arrays.asList(bid1, ask1);

        boolean actual = subject.isMatch(entities);
        assertThat(actual).isFalse();
    }
}