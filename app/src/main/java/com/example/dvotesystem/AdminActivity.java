package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private String currentUid;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    private String selectedState = "";
    private String selectedConstituency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
        
        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (currentUid == null) {
            finish();
            return;
        }

        checkAdminRole();

        MaterialButton btnAddCandidate = findViewById(R.id.btnAddCandidate);
        MaterialButton btnAddElection = findViewById(R.id.btnAddElection);
        MaterialButton btnManageElections = findViewById(R.id.btnManageElections);
        MaterialButton btnStartElection = findViewById(R.id.btnStartElection);
        MaterialButton btnEndElection = findViewById(R.id.btnEndElection);
        MaterialButton btnResetElection = findViewById(R.id.btnResetElection);
        MaterialButton btnViewUsers = findViewById(R.id.btnViewUsers);
        MaterialButton btnLiveResults = findViewById(R.id.btnLiveResults);

        btnAddCandidate.setOnClickListener(v -> showAddCandidateDialog());
        btnAddElection.setOnClickListener(v -> showAddElectionDialog());
        btnManageElections.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ElectionActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });
        btnViewUsers.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, UserListActivity.class)));
        btnLiveResults.setOnClickListener(v -> startActivity(new Intent(AdminActivity.this, ResultsActivity.class)));

        btnStartElection.setOnClickListener(v -> updateElectionStatus(true));
        btnEndElection.setOnClickListener(v -> updateElectionStatus(false));

        btnResetElection.setOnClickListener(v -> showResetConfirmation());
    }

    private void showAddElectionDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_election, null);
        EditText etTitle = view.findViewById(R.id.etElectionTitle);
        EditText etDate = view.findViewById(R.id.etElectionDate);
        AutoCompleteTextView acType = view.findViewById(R.id.acElectionType);
        AutoCompleteTextView acState = view.findViewById(R.id.acElectionState);
        AutoCompleteTextView acConstituency = view.findViewById(R.id.acElectionConstituency);
        View tilState = view.findViewById(R.id.tilElectionState);
        View tilConstituency = view.findViewById(R.id.tilElectionConstituency);

        String[] types = {"National", "State"};
        acType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types));

        acType.setOnItemClickListener((parent, v, position, id) -> {
            String type = (String) parent.getItemAtPosition(position);
            if ("State".equals(type)) {
                tilState.setVisibility(View.VISIBLE);
                tilConstituency.setVisibility(View.VISIBLE);
            } else {
                tilState.setVisibility(View.GONE);
                tilConstituency.setVisibility(View.GONE);
            }
        });

        // Setup States
        String[] states = {"Andhra Pradesh", "Telangana"};
        acState.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states));

        acState.setOnItemClickListener((parent, v, position, id) -> {
            String state = (String) parent.getItemAtPosition(position);
            mDatabase.child("States").child(state).child("Constituencies").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> constituencies = new ArrayList<>();
                    constituencies.add(""); // For State-wide option
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        constituencies.add(ds.getValue(String.class));
                    }
                    acConstituency.setAdapter(new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_dropdown_item_1line, constituencies));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        new AlertDialog.Builder(this)
                .setTitle("Add New Election")
                .setView(view)
                .setPositiveButton(R.string.add_btn, (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    String date = etDate.getText().toString().trim();
                    String type = acType.getText().toString();
                    String state = acState.getText().toString();
                    String constituency = acConstituency.getText().toString();

                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(type)) {
                        addElection(title, date, type, state, constituency);
                    } else {
                        Toast.makeText(this, "Title and Type are required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }

    private void addElection(String title, String date, String type, String state, String constituency) {
        String path = "National".equals(type) ? "Elections/National" : "Elections/State/" + state;
        DatabaseReference ref = mDatabase.child(path).push();
        String id = ref.getKey();
        
        Election election = new Election(id, title, type, state, constituency, "Active", date);
        if (id != null) {
            ref.setValue(election)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Election Added Successfully", Toast.LENGTH_SHORT).show());
        }
    }

    private void checkAdminRole() {
        mDatabase.child("users").child(currentUid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (!"admin".equals(role)) {
                    Toast.makeText(AdminActivity.this, R.string.access_denied_admin, Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finish();
            }
        });
    }

    private void showAddCandidateDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_candidate, null);
        EditText etName = view.findViewById(R.id.etCandidateName);
        EditText etParty = view.findViewById(R.id.etPartyName);
        EditText etPhotoUrl = view.findViewById(R.id.etPhotoUrl);
        EditText etLogoUrl = view.findViewById(R.id.etLogoUrl);
        AutoCompleteTextView acState = view.findViewById(R.id.acDialogState);
        AutoCompleteTextView acConstituency = view.findViewById(R.id.acDialogConstituency);

        selectedState = "";
        selectedConstituency = "";

        // Setup State Dropdown
        String[] states = {"Andhra Pradesh", "Telangana"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        acState.setAdapter(stateAdapter);

        acState.setOnItemClickListener((parent, v, position, id) -> {
            selectedState = (String) parent.getItemAtPosition(position);
            selectedConstituency = "";
            acConstituency.setText("");
            
            // Load Constituencies
            mDatabase.child("States").child(selectedState).child("Constituencies").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> constituencies = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        constituencies.add(ds.getValue(String.class));
                    }
                    ArrayAdapter<String> consAdapter = new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_dropdown_item_1line, constituencies);
                    acConstituency.setAdapter(consAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        });

        acConstituency.setOnItemClickListener((parent, v, position, id) -> {
            selectedConstituency = (String) parent.getItemAtPosition(position);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Candidate")
                .setView(view)
                .setPositiveButton(R.string.add_btn, null)
                .setNegativeButton(R.string.cancel_btn, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            MaterialButton button = (MaterialButton) dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                String name = etName.getText().toString().trim();
                String party = etParty.getText().toString().trim();
                String photoUrl = etPhotoUrl.getText().toString().trim();
                String logoUrl = etLogoUrl.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(party) || 
                    TextUtils.isEmpty(selectedState) || TextUtils.isEmpty(selectedConstituency)) {
                    Toast.makeText(this, "Please fill all fields, including State and Constituency", Toast.LENGTH_SHORT).show();
                    return;
                }

                addCandidate(name, party, logoUrl, photoUrl, dialog);
            });
        });

        dialog.show();
    }

    private void addCandidate(String name, String party, String symbol, String photo, AlertDialog dialog) {
        String id = mDatabase.child("candidates").push().getKey();
        Candidate candidate = new Candidate(id, name, party, symbol, photo, selectedState, selectedConstituency, 0);
        if (id != null) {
            mDatabase.child("candidates").child(id).setValue(candidate)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, R.string.candidate_added, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add candidate: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updateElectionStatus(boolean isStarted) {
        mDatabase.child("electionConfig").child("isStarted").setValue(isStarted)
                .addOnSuccessListener(aVoid -> {
                    int statusRes = isStarted ? R.string.election_started : R.string.election_ended;
                    Toast.makeText(this, statusRes, Toast.LENGTH_SHORT).show();
                });
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.reset_election)
                .setMessage(R.string.reset_confirm_msg)
                .setPositiveButton(R.string.reset_btn, (dialog, which) -> resetElection())
                .setNegativeButton(R.string.cancel_btn, null)
                .show();
    }

    private void resetElection() {
        // Reset candidate votes
        mDatabase.child("candidates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("voteCount").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Reset user hasVoted status
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("hasVoted").setValue(false);
                }
                Toast.makeText(AdminActivity.this, R.string.reset_complete, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
