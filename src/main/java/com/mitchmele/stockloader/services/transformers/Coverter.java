package com.mitchmele.stockloader.services.transformers;

public interface Coverter<IN, OUT> {
     OUT covert(IN value);
}
