package com.example.dvotesystem;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends BaseActivity {
    private TextView tvName, tvEmail, tvPhone, tvAadhaar, tvVoterId;
    private TextView tvProfileState, tvProfileDistrictCity, tvProfileLat, tvProfileLong, tvProfileLastUpdated;
    private Chip tvVotingStatus;
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAadhaar = findViewById(R.id.tvAadhaar);
        tvVoterId = findViewById(R.id.tvVoterId);
        tvVotingStatus = findViewById(R.id.tvVotingStatus);
        
        tvProfileState = findViewById(R.id.tvProfileState);
        tvProfileDistrictCity = findViewById(R.id.tvProfileDistrictCity);
        tvProfileLat = findViewById(R.id.tvProfileLat);
        tvProfileLong = findViewById(R.id.tvProfileLong);
        tvProfileLastUpdated = findViewById(R.id.tvProfileLastUpdated);

        findViewById(R.id.btnChangeLanguage).setOnClickListener(v -> startActivity(new Intent(this, LanguageActivity.class)));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference().child("users").child(user.getUid());
            loadUserProfile();
        } else {
            finish();
        }
    }

    private void loadUserProfile() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvName.setText(user.name);
                        tvEmail.setText(user.email);
                        tvPhone.setText(user.phone);
                        tvAadhaar.setText(user.aadhaar);
                        tvVoterId.setText(user.voterId);
                        
                        if (user.hasVoted) {
                            tvVotingStatus.setText(R.string.voted);
                            tvVotingStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                        } else {
                            tvVotingStatus.setText(R.string.not_voted);
                            tvVotingStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F44336")));
                        }

                        if (user.location != null) {
                            tvProfileState.setText(user.location.state);
                            tvProfileDistrictCity.setText(user.location.district + ", " + user.location.city);
                            tvProfileLat.setText(String.valueOf(user.location.latitude));
                            tvProfileLong.setText(String.valueOf(user.location.longitude));
                        }

                        if (user.lastLocationUpdated != null && user.lastLocationUpdated instanceof Long) {
                            long timestamp = (long) user.lastLocationUpdated;
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                            String dateStr = sdf.format(new Date(timestamp));
                            tvProfileLastUpdated.setText(getString(R.string.last_updated, dateStr));
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, R.string.profile_not_found, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, getString(R.string.error_label) + ": " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
