package com.example.myapplication.models;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class Friendship implements Serializable {
    private String id;
    private String userId1;
    private String userId2;
    private String status;
    private Timestamp timestamp;

    // Default constructor required for Firestore
    public Friendship() {}

    public Friendship(String userId1, String userId2) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.status = "pending";
        this.timestamp = Timestamp.now();
    }

    // Getters and setters (unchanged)

    // Helper methods
    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isAccepted() {
        return "accepted".equals(status);
    }

    public String getOtherUserId(String currentUserId) {
        return userId1.equals(currentUserId) ? userId2 : userId1;
    }

    public boolean involvesUser(String userId) {
        return userId1.equals(userId) || userId2.equals(userId);
    }
}
