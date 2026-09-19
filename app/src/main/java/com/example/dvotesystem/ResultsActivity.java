package com.example.dvotesystem;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsActivity extends BaseActivity {

    private ResultsAdapter adapter;
    private List<Candidate> candidateList;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();

        RecyclerView rvResults = findViewById(R.id.rvResults);
        rvResults.setLayoutManager(new LinearLayoutManager(this));

        candidateList = new ArrayList<>();
        adapter = new ResultsAdapter(candidateList);
        rvResults.setAdapter(adapter);

        fetchResults();
    }

    private void fetchResults() {
        mDatabase.child("candidates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                candidateList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Candidate candidate = dataSnapshot.getValue(Candidate.class);
                    if (candidate != null) {
                        candidateList.add(candidate);
                    }
                }
                
                // Sort by vote count descending
                Collections.sort(candidateList, (c1, c2) -> Integer.compare(c2.voteCount, c1.voteCount));
                
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ResultsActivity.this, getString(R.string.error_label) + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
