package com.pennywise.model;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Admin: manages users, financial data security and system settings. */
public class AdminUser extends User {
    public AdminUser(int id, String name, String email, String hash, BigDecimal income) {
        super(id, name, email, hash, income);
    }

    @Override public Role getRole() { return Role.ADMIN; }
    @Override public String getDashboardPath() { return "admin"; }

    @Override
    public Map<String, String> getMenu() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("dashboard", "Dashboard");
        m.put("users", "User Management");
        m.put("security", "Financial Data Security");
        m.put("system", "System Settings");
        m.put("feedback", "User Feedback");
        return m;
    }
}
