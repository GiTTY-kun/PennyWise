package com.pennywise.model;

/** A piece of personalised advice written by an advisor for a user. */
public class Advice {
    private int id;
    private final int userId;
    private final int advisorId;
    private final String content;
    private final String createdAt;

    public Advice(int id, int userId, int advisorId, String content, String createdAt) {
        this.id = id; this.userId = userId; this.advisorId = advisorId; this.content = content; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public int getAdvisorId() { return advisorId; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
