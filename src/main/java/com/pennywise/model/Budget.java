package com.pennywise.model;

import java.math.BigDecimal;

/** A monthly spending limit for one category. */
public class Budget {
    private int id;
    private final int userId;
    private final String category;
    private final BigDecimal amount;

    public Budget(int id, int userId, String category, BigDecimal amount) {
        this.id = id; this.userId = userId; this.category = category; this.amount = amount;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
}
