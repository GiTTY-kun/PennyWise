package com.pennywise.model;

/** Feedback / support ticket sent by a user to the admin. Status is OPEN or RESOLVED. */
public class Feedback {
    private int id;
    private final int userId;
    private final String content;
    private final String status;
    private final String createdAt;

    public Feedback(int id, int userId, String content, String status, String createdAt) {
        this.id = id; this.userId = userId; this.content = content; this.status = status; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public boolean isOpen() { return "OPEN".equals(status); }
}
