package com.pennywise.util;

import com.pennywise.model.Bar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Turns a map of label -> number into bar-chart rows scaled against the largest value. */
public final class Bars {
    private Bars() { }

    /** Generic over any Number type (BigDecimal for money, Integer for counts). */
    public static List<Bar> of(Map<String, ? extends Number> data) {
        double max = 0;
        for (Number n : data.values()) {
            max = Math.max(max, n.doubleValue());
        }
        if (max <= 0) {
            max = 1;
        }
        List<Bar> bars = new ArrayList<>();
        for (Map.Entry<String, ? extends Number> e : data.entrySet()) {
            bars.add(new Bar(e.getKey(), e.getValue(), (int) Math.round(e.getValue().doubleValue() * 100 / max)));
        }
        return bars;
    }
}
