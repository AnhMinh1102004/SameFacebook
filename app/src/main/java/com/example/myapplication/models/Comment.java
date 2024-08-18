package com.example.myapplication.models;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String postId;
    private String userId;
    private String content;
    private Timestamp timestamp;

    // Constructor mặc định cần thiết cho Firestore
    public Comment() {}

    public Comment(String id, String postId, String userId, String content, Timestamp timestamp) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setPostId(String postId) { this.postId = postId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
