package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.model.Ask;
import com.mitchmele.stockloader.model.Bid;
import com.mitchmele.stockloader.model.Trade;
import com.mitchmele.stockloader.mongodb.StockEntity;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TradeMatcherServiceTest {

    TradeMatcherService subject;
    LocalDate tradeTime = LocalDate.of(2020, 5, 7);

    @Before
    public void setUp() throws Exception {
        subject = new TradeMatcherService();
    }

    @Test
    public void transaction_success_shouldGroupMessagesTogetherByPrice() {
        Bid bid1 = new Bid("ABC", 25.00);
        Bid bid2 = new Bid("ABC", 23.00);
        Bid bid3 = new Bid("ABC", 22.75);

        Ask ask1 = new Ask("ABC", 25.00);
        Ask ask2 = new Ask("ABC", 27.15);
        Ask ask3 = new Ask("ABC", 25.25);


        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, ask1, ask2, ask3);

        Trade actual = subject.createTransaction(entities);


        Trade expected = new Trade("ABC", 25.00, tradeTime);

        assertThat(actual).isEqualToIgnoringGivenFields(expected, "timeOfTrade");
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
    public void isMatch_success_shouldReturnTrueIfPricesMatch() {
        Bid bid1 = new Bid("ABC", 25.0);
        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, ask1);

        boolean actual = subject.isMatch(entities);
        assertThat(actual).isTrue();
    }
}