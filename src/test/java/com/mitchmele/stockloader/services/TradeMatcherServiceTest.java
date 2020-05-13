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
import java.util.Map;

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

        List<Trade> actual = subject.createTransaction(entities);

        Trade expectedTrade = new Trade("ABC", 25.00, tradeTime);
        List<Trade> expected = Arrays.asList(expectedTrade);

        assertThat(actual.size()).isEqualTo(1);
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

    @Test
    public void hasMatches_shouldReturnNumberOfTradeMatches_fromList() {

        Bid bid1 = new Bid("ABC", 24.90);
        Bid bid2 = new Bid("ABC", 25.10);
        Bid bid3 = new Bid("ABC", 25.05);
        Bid bid4 = new Bid("ABC", 24.95);

        Ask ask1 = new Ask("ABC", 25.10);
        Ask ask2 = new Ask("ABC", 25.05);
        Ask ask3 = new Ask("ABC", 26.00);
        Ask ask4 = new Ask("ABC", 25.30);

        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, ask1, ask2, ask3, ask4);

        int actual = subject.hasMatches(entities);
        assertThat(actual).isEqualTo(2);
    }


    @Test
    public void zipBids_shouldDoThings() {
        //create entry if bids and asks match in price
        Bid bid1 = new Bid("ABC", 24.90);
        Bid bid2 = new Bid("ABC", 25.10);
        Bid bid3 = new Bid("ABC", 25.05);
        Bid bid4 = new Bid("ABC", 24.95);

        List<Bid> bids = Arrays.asList(bid1, bid2, bid3, bid4);

        Ask ask1 = new Ask("ABC", 25.10);
        Ask ask2 = new Ask("ABC", 25.05);
        Ask ask3 = new Ask("ABC", 26.00);
        Ask ask4 = new Ask("ABC", 25.30);

        List<Ask> asks = Arrays.asList(ask1, ask2, ask3, ask4);

        List<Map.Entry<StockEntity, StockEntity>>  actual = TradeMatcherService.zipBids(bids, asks);
        assertThat(actual).isNotEmpty();
    }

}