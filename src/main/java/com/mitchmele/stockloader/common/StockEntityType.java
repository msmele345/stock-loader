package com.mitchmele.stockloader.common;

public enum StockEntityType {
    BID("BID"),
    ASK("ASK"),
    STOCK("STOCK");

    public final String value;

    private StockEntityType(String value) {
        this.value = value;
    }
}

