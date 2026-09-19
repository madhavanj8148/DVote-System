package com.example.dvotesystem;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class CandidateActivity extends BaseActivity {
    private RecyclerView rvCandidates;
    private CandidateAdapter adapter;
    private List<Candidate> candidateList;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        rvCandidates = findViewById(R.id.rvCandidates);
        rvCandidates.setLayoutManager(new LinearLayoutManager(this));
        candidateList = new ArrayList<>();
        adapter = new CandidateAdapter(candidateList);
        rvCandidates.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference().child("candidates");
        fetchCandidates();
    }

    private void fetchCandidates() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                candidateList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Candidate candidate = postSnapshot.getValue(Candidate.class);
                    candidateList.add(candidate);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
