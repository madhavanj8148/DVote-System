package com.example.dvotesystem;

public class Candidate {
    public String candidateId;
    public String candidateName;
    public String partyName;
    public String partySymbol;
    public String photoUrl;
    public String state;
    public String constituency;
    public int voteCount;

    public Candidate() {
        // Default constructor required for calls to DataSnapshot.getValue(Candidate.class)
    }

    public Candidate(String candidateId, String candidateName, String partyName, String partySymbol, String photoUrl, String state, String constituency, int voteCount) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.partyName = partyName;
        this.partySymbol = partySymbol;
        this.photoUrl = photoUrl;
        this.state = state;
        this.constituency = constituency;
        this.voteCount = voteCount;
    }
}
