package com.pennywise.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Financial advisor: gives personalised advice, talks to users, tracks their progress. */
public class AdvisorUser extends User {
    public AdvisorUser(int id, String name, String email, String hash, BigDecimal income) {
        super(id, name, email, hash, income);
    }

    @Override public Role getRole() { return Role.ADVISOR; }
    @Override public String getDashboardPath() { return "advisor"; }

    @Override
    public Map<String, String> getMenu() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("dashboard", "Dashboard");
        m.put("advice", "Advice Management");
        m.put("interaction", "User Interaction");
        m.put("progress", "User Progress");
        m.put("history", "Advice History");
        return m;
    }
}
