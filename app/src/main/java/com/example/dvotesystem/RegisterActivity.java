package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity {
    private EditText etName, etEmail, etPhone, etAadhaar, etVoterId, etPassword;
    private AutoCompleteTextView acState, acConstituency;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private LoadingDialog loadingDialog;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";
    
    private String selectedState = "";
    private String selectedConstituency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference();
        loadingDialog = new LoadingDialog(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAadhaar = findViewById(R.id.etAadhaar);
        etVoterId = findViewById(R.id.etVoterId);
        etPassword = findViewById(R.id.etPassword);
        acState = findViewById(R.id.acState);
        acConstituency = findViewById(R.id.acConstituency);
        
        setupStateDropdown();

        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnRegister.setOnClickListener(v -> registerUser());
        btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void setupStateDropdown() {
        String[] states = {"Andhra Pradesh", "Telangana"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, states);
        acState.setAdapter(adapter);

        acState.setOnItemClickListener((parent, view, position, id) -> {
            selectedState = (String) parent.getItemAtPosition(position);
            selectedConstituency = "";
            acConstituency.setText("");
            loadConstituencies(selectedState);
        });
    }

    private void loadConstituencies(String state) {
        mDatabase.child("States").child(state).child("Constituencies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> constituencies = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    constituencies.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line, constituencies);
                acConstituency.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error loading constituencies", Toast.LENGTH_SHORT).show();
            }
        });
        
        acConstituency.setOnItemClickListener((parent, view, position, id) -> {
            selectedConstituency = (String) parent.getItemAtPosition(position);
        });
    }

    private void registerUser() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String aadhaar = etAadhaar.getText().toString().trim();
        String voterId = etVoterId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || 
            TextUtils.isEmpty(aadhaar) || TextUtils.isEmpty(voterId) || TextUtils.isEmpty(password) ||
            TextUtils.isEmpty(selectedState) || TextUtils.isEmpty(selectedConstituency)) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show(getString(R.string.creating_account));
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserInfo(firebaseUser.getUid(), name, email, phone, aadhaar, voterId, selectedState, selectedConstituency);
                        }
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_label) + ": " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserInfo(String uid, String name, String email, String phone, String aadhaar, String voterId, String state, String constituency) {
        User user = new User(uid, name, email, phone, aadhaar, voterId, false, "voter", state, constituency);
        
        mDatabase.child("users").child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, R.string.reg_success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.error_label) + ": " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
