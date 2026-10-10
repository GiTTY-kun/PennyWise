package com.pennywise.model;

/** One key/value row of the system and security settings table. */
public class Setting {
    private int id;
    private final String key;
    private final String value;

    public Setting(int id, String key, String value) { this.id = id; this.key = key; this.value = value; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKey() { return key; }
    public String getValue() { return value; }
}
