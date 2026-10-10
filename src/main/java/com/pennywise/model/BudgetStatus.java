package com.pennywise.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** A budget compared with what was actually spent this month (used for progress bars and alerts). */
public class BudgetStatus {
    private final String category;
    private final BigDecimal spent;
    private final BigDecimal limit;
    private final int percent;      // 0..100, capped so the bar never overflows
    private final boolean over;

    private BudgetStatus(String category, BigDecimal spent, BigDecimal limit, int percent, boolean over) {
        this.category = category; this.spent = spent; this.limit = limit; this.percent = percent; this.over = over;
    }

    public static BudgetStatus of(String category, BigDecimal spent, BigDecimal limit) {
        int pct = 0;
        if (limit.signum() > 0) {
            pct = spent.multiply(BigDecimal.valueOf(100)).divide(limit, 0, RoundingMode.HALF_UP).intValue();
        }
        return new BudgetStatus(category, spent, limit, Math.min(100, pct), spent.compareTo(limit) > 0);
    }

    public String getCategory() { return category; }
    public BigDecimal getSpent() { return spent; }
    public BigDecimal getLimit() { return limit; }
    public int getPercent() { return percent; }
    public boolean isOver() { return over; }
}
