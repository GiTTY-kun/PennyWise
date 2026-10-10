package com.pennywise.model;

import java.math.BigDecimal;

/**
 * Abstract base class for all users. The concrete subclasses (AdminUser, AdvisorUser,
 * RegularUser) supply the role, dashboard and menu.
 */
public abstract class User implements Dashboardable {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private BigDecimal monthlyIncome;

    protected User(int id, String name, String email, String passwordHash, BigDecimal monthlyIncome) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.monthlyIncome = monthlyIncome == null ? BigDecimal.ZERO : monthlyIncome;
    }

    /** Each subclass reports its own role. */
    public abstract Role getRole();

    /** Factory: builds the right subclass for a role (used by the DAO and the user service). */
    public static User create(Role role, int id, String name, String email,
                              String passwordHash, BigDecimal monthlyIncome) {
        switch (role) {
            case ADMIN:   return new AdminUser(id, name, email, passwordHash, monthlyIncome);
            case ADVISOR: return new AdvisorUser(id, name, email, passwordHash, monthlyIncome);
            default:      return new RegularUser(id, name, email, passwordHash, monthlyIncome);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
}
