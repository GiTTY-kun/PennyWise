package com.pennywise.model;

/** One line of the security report. */
public class LogEntry {
    private int id;
    private final String event;
    private final String createdAt;

    public LogEntry(int id, String event, String createdAt) { this.id = id; this.event = event; this.createdAt = createdAt; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEvent() { return event; }
    public String getCreatedAt() { return createdAt; }
}
