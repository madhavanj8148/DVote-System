package com.example.dvotesystem;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String aadhaar;
    public String voterId;
    public boolean hasVoted;
    public String role;
    public String state;
    public String constituency;
    public UserLocation location;
    public Object lastLocationUpdated;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String name, String email, String phone, String aadhaar, String voterId, boolean hasVoted, String role, String state, String constituency) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.aadhaar = aadhaar;
        this.voterId = voterId;
        this.hasVoted = hasVoted;
        this.role = role;
        this.state = state;
        this.constituency = constituency;
    }
}
