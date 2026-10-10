package com.pennywise.model;

/** A message between a user and an advisor. userId is always the client; senderRole says who wrote it. */
public class Message {
    private int id;
    private final int userId;
    private final String senderRole;
    private final String content;
    private final String createdAt;

    public Message(int id, int userId, String senderRole, String content, String createdAt) {
        this.id = id; this.userId = userId; this.senderRole = senderRole; this.content = content; this.createdAt = createdAt;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public String getSenderRole() { return senderRole; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
