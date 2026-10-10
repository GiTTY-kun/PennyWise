package com.pennywise.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Regular user: tracks expenses, sets budgets and receives financial advice. */
public class RegularUser extends User {
    public RegularUser(int id, String name, String email, String hash, BigDecimal income) {
        super(id, name, email, hash, income);
    }

    @Override public Role getRole() { return Role.USER; }
    @Override public String getDashboardPath() { return "user"; }

    @Override
    public Map<String, String> getMenu() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("dashboard", "Dashboard");
        m.put("expenses", "Expense Tracker");
        m.put("budgets", "Budget Overview");
        m.put("advice", "Financial Advice");
        m.put("reports", "Financial Reports");
        m.put("profile", "Profile");
        m.put("feedback", "Send Feedback");
        return m;
    }
}
