package com.example.dvotesystem;

public class Election {
    public String id;
    public String title;
    public String type; // "National" or "State"
    public String state; 
    public String constituency;
    public String status; // "Active", "Completed"
    public String date;

    public Election() {
    }

    public Election(String id, String title, String type, String state, String constituency, String status, String date) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.state = state;
        this.constituency = constituency;
        this.status = status;
        this.date = date;
    }
}
