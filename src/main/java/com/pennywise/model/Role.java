package com.pennywise.model;

/** The three kinds of user in the platform. The enum name is what is stored in the database. */
public enum Role {
    ADMIN("Admin"), ADVISOR("Financial Advisor"), USER("User");

    private final String label;
    Role(String label) { this.label = label; }
    public String getLabel() { return label; }
}
