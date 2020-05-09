package com.mitchmele.stockloader.services;

public interface Coverter<IN, OUT> {
     OUT covert(IN value);
}
