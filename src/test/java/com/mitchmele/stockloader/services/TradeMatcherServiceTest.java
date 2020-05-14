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
    public void transaction_success_shouldGroupMessagesTogetherByPrice() {
        Bid bid1 = new Bid("ABC", 25.00);
        Bid bid4 = new Bid("ABC", 25.00);
        Bid bid2 = new Bid("ABC", 23.00);
        Bid bid3 = new Bid("ABC", 22.75);

        Ask ask1 = new Ask("ABC", 25.00);
        Ask ask4 = new Ask("ABC", 25.01);
        Ask ask2 = new Ask("ABC", 27.15);
        Ask ask3 = new Ask("ABC", 25.25);


        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, ask1, ask2, ask3, ask4);

        List<Trade> actual = subject.createTransaction(entities);

        Trade expectedTrade = new Trade("ABC", 25.00, tradeTime);
        List<Trade> expected = Arrays.asList(expectedTrade);

        assertThat(actual.size()).isEqualTo(2);
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
    public void matchTrades_shouldReturnListOfTrades() {
        Bid bid1 = new Bid("ABC", 25.00);
        Bid bid2 = new Bid("ABC", 25.00);

        Ask ask1 = new Ask("ABC", 25.00);

        List<StockEntity> entities = Arrays.asList(bid1, bid2, ask1);

        List<Trade> actual = subject.matchTrades(entities);
        assertThat(actual).hasSize(1);
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



    @Test
    public void mapBidsOffers_shouldFindMatchesInBids_with_OffersAtSamePrice() {
        //create map of key (type) to value (list of bids)

        Bid bid1 = new Bid("ABC", 24.90);
        Bid bid2 = new Bid("ABC", 25.10);
        Bid bid3 = new Bid("ABC", 25.05);
        Bid bid4 = new Bid("ABC", 24.95);
        Bid bid5 = new Bid("ABC", 24.93);
        Bid bid6 = new Bid("ABC", 24.92);

        Ask ask1 = new Ask("ABC", 25.10);
        Ask ask2 = new Ask("ABC", 25.05);
        Ask ask3 = new Ask("ABC", 26.00);
        Ask ask4 = new Ask("ABC", 25.30);
        Ask ask6 = new Ask("ABC", 25.31);
        Ask ask7 = new Ask("ABC", 25.32);
        Ask ask8 = new Ask("ABC", 25.33);
        Ask ask9 = new Ask("ABC", 25.34);

        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, ask1, ask2, ask3, ask4, bid5, bid6, ask6, ask7, ask8, ask9);
        Set<StockEntity> expectedEntities = new HashSet<>();
        expectedEntities.add(ask1);
        expectedEntities.add(ask2);
        //OR
        Set<StockEntity> expectedEntities2 = Collections
                .unmodifiableSet(new HashSet<>(Arrays.asList(ask1, ask2)));


        Set<StockEntity> actual = subject.mapBidsOffers(entities);
        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(expectedEntities);
    }

    @Test
    public void mapBidsOffers_shouldFindMatchesInBids_with_OffersAtSamePriceOnlyOnce() {
        //create map of key (type) to value (list of bids)

        Bid bid1 = new Bid("ABC", 24.90);
        Bid bid2 = new Bid("ABC", 25.10);
        Bid bid3 = new Bid("ABC", 25.05);
        Bid bid4 = new Bid("ABC", 24.95);


        Ask ask1 = new Ask("ABC", 25.10);
        Ask ask2 = new Ask("ABC", 25.05);
        Ask ask3 = new Ask("ABC", 26.00);
        Ask ask4 = new Ask("ABC", 25.10);


        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, ask1, ask2, ask3, ask4);
        Set<StockEntity> expectedEntities = Collections
                .unmodifiableSet(new HashSet<>(Arrays.asList(ask1, ask2)));

        Set<StockEntity> actual = subject.mapBidsOffers(entities);
        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(expectedEntities);
    }

    @Test//multiple matching bids/offers
    public void mapBidsOffers_shouldFindMultipleMatches_ifProvidedEqualNumberOfBidsAndAsks_withSamePrice() {
        //create map of key (type) to value (list of bids)

        Bid bid1 = new Bid("ABC", 24.90);
        Bid bid2 = new Bid("ABC", 25.10);
        Bid bid3 = new Bid("ABC", 25.05);
        Bid bid4 = new Bid("ABC", 24.95);
        Bid bid5 = new Bid("ABC", 25.10);

        Ask ask1 = new Ask("ABC", 25.10);
        Ask ask2 = new Ask("ABC", 25.05);
        Ask ask3 = new Ask("ABC", 26.00);
        Ask ask4 = new Ask("ABC", 25.10);


        List<StockEntity> entities = Arrays.asList(bid1, bid2, bid3, bid4, bid5, ask1, ask2, ask3, ask4);
        Set<StockEntity> expectedEntities = Collections
                .unmodifiableSet(new HashSet<>(Arrays.asList(ask1, ask2)));

        Set<StockEntity> actual = subject.mapBidsOffers(entities);
        assertThat(actual).hasSize(3);
        assertThat(actual).isEqualTo(expectedEntities);
    }

}