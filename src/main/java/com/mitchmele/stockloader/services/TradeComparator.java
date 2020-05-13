package com.mitchmele.stockloader.services;

import com.mitchmele.stockloader.mongodb.StockEntity;

import java.util.Comparator;

public class TradeComparator implements Comparator<StockEntity> {

    @Override
    public int compare(StockEntity o1, StockEntity o2) {
        return o1.getPrice().intValue() - o2.getPrice().intValue();
    }
}
