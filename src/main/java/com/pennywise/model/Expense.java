package com.pennywise.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One spending entry (category, amount, date) belonging to a user. */
public class Expense {
    private int id;
    private final int userId;
    private final String category;
    private final BigDecimal amount;
    private final LocalDate date;

    public Expense(int id, int userId, String category, BigDecimal amount, LocalDate date) {
        this.id = id; this.userId = userId; this.category = category; this.amount = amount; this.date = date;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
