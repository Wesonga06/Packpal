package models;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String email;
    private String passwordHash;
    private String name; // corresponds to 'full_name' in DB
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 🟩 Full constructor (when retrieving from DB)
    public User(int userId, String email, String passwordHash, String name,
                Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🟩 Constructor for creating a new user (before insert)
    public User(String email, String passwordHash, String name) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
    }

    // 🟩 Empty constructor (for frameworks or serialization)
    public User() {}

    // ===========================
    // Getters
    // ===========================
    public int getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // ===========================
    // Setters
    // ===========================
    public void setUserId(int userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // ===========================
    // Utility
    // ===========================
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

