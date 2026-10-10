package com.pennywise.model;

import java.math.BigDecimal;

/** Income, spending and savings for the current month. */
public class FinancialSummary {
    private final BigDecimal income;
    private final BigDecimal spent;
    private final BigDecimal left;
    private final int savingsRate;

    public FinancialSummary(BigDecimal income, BigDecimal spent, BigDecimal left, int savingsRate) {
        this.income = income; this.spent = spent; this.left = left; this.savingsRate = savingsRate;
    }
    public BigDecimal getIncome() { return income; }
    public BigDecimal getSpent() { return spent; }
    public BigDecimal getLeft() { return left; }
    public int getSavingsRate() { return savingsRate; }
    public boolean isDeficit() { return left.signum() < 0; }
}
