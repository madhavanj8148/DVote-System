package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ElectionActivity extends BaseActivity {
    private RecyclerView rvElections;
    private ElectionAdapter adapter;
    private List<Election> electionList;
    private ProgressBar pbElections;
    private TextView tvNoElections;
    private TextView tvLocationFilter;

    private DatabaseReference mDatabase;
    private String userState = "";
    private String userConstituency = "";
    private boolean isAdminMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_election);

        rvElections = findViewById(R.id.rvElections);
        pbElections = findViewById(R.id.pbElections);
        tvNoElections = findViewById(R.id.tvNoElections);
        tvLocationFilter = findViewById(R.id.tvLocationFilter);

        isAdminMode = getIntent().getBooleanExtra("isAdmin", false);

        electionList = new ArrayList<>();
        adapter = new ElectionAdapter(electionList, new ElectionAdapter.OnElectionClickListener() {
            @Override
            public void onElectionClick(Election election) {
                Intent intent = new Intent(ElectionActivity.this, CandidateActivity.class);
                intent.putExtra("electionId", election.id);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Election election) {
                showDeleteConfirmation(election);
            }
        });
        
        adapter.setAdmin(isAdminMode);

        rvElections.setLayoutManager(new LinearLayoutManager(this));
        rvElections.setAdapter(adapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            fetchUserLocationAndElections(currentUser.getUid());
        }
    }

    private void showDeleteConfirmation(Election election) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Election")
                .setMessage("Are you sure you want to delete this election?")
                .setPositiveButton("Delete", (dialog, which) -> deleteElection(election))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteElection(Election election) {
        String path = "National".equals(election.type) ? "Elections/National/" + election.id : "Elections/State/" + election.state + "/" + election.id;
        mDatabase.child(path).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Election Deleted", Toast.LENGTH_SHORT).show();
                    fetchElections(); // Refresh list
                });
    }

    private void fetchUserLocationAndElections(String uid) {
        pbElections.setVisibility(View.VISIBLE);
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userState = user.state;
                    userConstituency = user.constituency;
                    
                    if (isAdminMode) {
                        tvLocationFilter.setText("Admin Mode: All Regional Elections");
                    } else {
                        tvLocationFilter.setText("Region: " + userState + " (" + userConstituency + ")");
                    }
                    
                    fetchElections();
                } else {
                    pbElections.setVisibility(View.GONE);
                    tvNoElections.setVisibility(View.VISIBLE);
                    tvNoElections.setText("Please register with state and constituency first.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbElections.setVisibility(View.GONE);
                Toast.makeText(ElectionActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchElections() {
        electionList.clear();
        
        // 1. Fetch National Elections
        mDatabase.child("Elections").child("National").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Election election = ds.getValue(Election.class);
                    if (election != null) electionList.add(election);
                }
                
                // 2. Fetch Regional Elections
                fetchRegionalElections();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateUI();
            }
        });
    }

    private void fetchRegionalElections() {
        if (isAdminMode) {
            // Fetch ALL state elections for admin
            mDatabase.child("Elections").child("State").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot stateSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot ds : stateSnapshot.getChildren()) {
                            Election election = ds.getValue(Election.class);
                            if (election != null) electionList.add(election);
                        }
                    }
                    updateUI();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { updateUI(); }
            });
        } else {
            // Fetch only user's state and constituency
            if (TextUtils.isEmpty(userState)) {
                updateUI();
                return;
            }
            mDatabase.child("Elections").child("State").child(userState).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Election election = ds.getValue(Election.class);
                        if (election != null) {
                            if (TextUtils.isEmpty(election.constituency) || election.constituency.equals(userConstituency)) {
                                electionList.add(election);
                            }
                        }
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { updateUI(); }
            });
        }
    }

    private void updateUI() {
        pbElections.setVisibility(View.GONE);
        if (electionList.isEmpty()) {
            tvNoElections.setVisibility(View.VISIBLE);
        } else {
            tvNoElections.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
    }
}
