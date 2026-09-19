package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class VotingActivity extends BaseActivity {

    private VotingAdapter adapter;
    private List<Candidate> candidateList;
    private DatabaseReference mDatabase;
    private String currentUid;
    private LoadingDialog loadingDialog;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_voting);

        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
        loadingDialog = new LoadingDialog(this);

        RecyclerView rvVotingCandidates = findViewById(R.id.rvVotingCandidates);
        rvVotingCandidates.setLayoutManager(new LinearLayoutManager(this));

        candidateList = new ArrayList<>();
        adapter = new VotingAdapter(candidateList, this::onVoteClicked);
        rvVotingCandidates.setAdapter(adapter);

        if (currentUid != null) {
            checkIfUserHasVoted();
        } else {
            Toast.makeText(this, R.string.auth_required, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onVoteClicked(Candidate candidate) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }
        showConfirmationDialog(candidate);
    }

    private void checkIfUserHasVoted() {
        loadingDialog.show(getString(R.string.checking_status));
        mDatabase.child("users").child(currentUid).child("hasVoted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.dismiss();
                Boolean hasVoted = snapshot.getValue(Boolean.class);
                if (hasVoted != null && hasVoted) {
                    Toast.makeText(VotingActivity.this, R.string.multiple_voting_alert, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(VotingActivity.this, ResultsActivity.class));
                    finish();
                } else {
                    fetchCandidates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(VotingActivity.this, getString(R.string.network_error) + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCandidates() {
        loadingDialog.show(getString(R.string.loading_candidates));
        
        // First get user's region
        mDatabase.child("users").child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User user = userSnapshot.getValue(User.class);
                if (user == null || user.state == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(VotingActivity.this, "Regional information missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userState = user.state;
                String userConstituency = user.constituency;

                // Now fetch and filter candidates
                mDatabase.child("candidates").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingDialog.dismiss();
                        candidateList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Candidate candidate = dataSnapshot.getValue(Candidate.class);
                            if (candidate != null) {
                                // Filter by state and constituency
                                if (userState.equals(candidate.state) && userConstituency.equals(candidate.constituency)) {
                                    candidateList.add(candidate);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        
                        if (candidateList.isEmpty()) {
                            Toast.makeText(VotingActivity.this, "No candidates found for your constituency", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingDialog.dismiss();
                        Toast.makeText(VotingActivity.this, R.string.failed_load_candidates, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
            }
        });
    }

    private void showConfirmationDialog(Candidate candidate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_vote, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setWindowAnimations(R.style.DVote_Animation_Dialog);
        }

        TextView tvMessage = dialogView.findViewById(R.id.tvConfirmMessage);
        tvMessage.setText(getString(R.string.vote_confirm_msg, candidate.candidateName));

        dialogView.findViewById(R.id.btnCancelVote).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnConfirmVote).setOnClickListener(v -> {
            dialog.dismiss();
            processVote(candidate);
        });

        dialog.show();
    }

    private void processVote(Candidate candidate) {
        loadingDialog.show(getString(R.string.processing_vote));
        
        mDatabase.child("users").child(currentUid).child("hasVoted").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean hasVoted = snapshot.getValue(Boolean.class);
                if (hasVoted != null && hasVoted) {
                    loadingDialog.dismiss();
                    Toast.makeText(VotingActivity.this, R.string.already_voted_error, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    executeVoteTransaction(candidate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                finish();
            }
        });
    }

    private void executeVoteTransaction(Candidate candidate) {
        DatabaseReference candidateRef = mDatabase.child("candidates").child(candidate.candidateId).child("voteCount");

        candidateRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer votes = currentData.getValue(Integer.class);
                if (votes == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(votes + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (committed) {
                    markUserAsVoted();
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(VotingActivity.this, R.string.transaction_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void markUserAsVoted() {
        mDatabase.child("users").child(currentUid).child("hasVoted").setValue(true)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(VotingActivity.this, R.string.vote_recorded_success, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(VotingActivity.this, ResultsActivity.class));
                        finish();
                    }
                });
    }
}
