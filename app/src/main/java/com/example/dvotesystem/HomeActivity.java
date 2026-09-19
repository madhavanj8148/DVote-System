package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends BaseActivity {
    private TextView tvUserName;
    private TextView tvLocationDetails;
    private TextView tvCurrentDate, tvCurrentTime;
    private View layoutLocation;
    private View pulseView;
    private Handler timeHandler = new Handler();
    
    private DatabaseReference mDatabase;
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvUserName = findViewById(R.id.tvUserName);
        tvLocationDetails = findViewById(R.id.tvLocationDetails);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        layoutLocation = findViewById(R.id.layoutLocation);
        pulseView = findViewById(R.id.pulseView);

        View btnVote = findViewById(R.id.btnVote);
        View btnElection = findViewById(R.id.btnElection);
        View btnResults = findViewById(R.id.btnResults);
        View btnProfile = findViewById(R.id.btnProfile);
        View btnQRVerification = findViewById(R.id.btnQRVerification);
        View btnAdmin = findViewById(R.id.btnAdmin);
        View btnLogoutTop = findViewById(R.id.btnLogoutTop);
        View cvStats = findViewById(R.id.cvStats);
        View tvQuickActions = findViewById(R.id.tvQuickActions);

        // Header Animations
        tvUserName.setAlpha(0);
        tvUserName.setTranslationX(-50);
        tvUserName.animate().alpha(1).translationX(0).setDuration(800).start();

        // Pulse Animation for status
        if (pulseView != null) {
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse_anim);
            pulseView.startAnimation(pulse);
        }

        // Start Clock
        updateTime();

        // Entry Animations for cards
        cvStats.setAlpha(0);
        cvStats.setTranslationY(100);
        cvStats.animate().alpha(1).translationY(0).setDuration(600).setStartDelay(200).start();

        tvQuickActions.setAlpha(0);
        tvQuickActions.animate().alpha(1).setDuration(600).setStartDelay(400).start();

        animateCard(btnVote, 500);
        animateCard(btnElection, 600);
        animateCard(btnResults, 700);
        animateCard(btnProfile, 800);
        animateCard(btnQRVerification, 900);
        animateCard(btnAdmin, 1000);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            mDatabase = FirebaseDatabase.getInstance(DB_URL).getReference().child("users").child(currentUser.getUid());
            fetchUserData(btnAdmin);
        }

        btnVote.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, VotingActivity.class)));
        btnElection.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ElectionActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        btnResults.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ResultsActivity.class)));
        btnQRVerification.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, QRCodeScannerActivity.class)));
        btnAdmin.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, AdminActivity.class)));

        btnLogoutTop.setOnClickListener(v -> logout());
    }

    private void updateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        
        tvCurrentDate.setText(dateFormat.format(new Date()));
        tvCurrentTime.setText(timeFormat.format(new Date()));
        
        timeHandler.postDelayed(this::updateTime, 60000); // Update every minute
    }

    private void animateCard(View view, int delay) {
        if (view == null) return;
        view.setAlpha(0);
        view.setTranslationY(100);
        view.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(delay).start();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(HomeActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchUserData(View btnAdmin) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        tvUserName.setText(user.name);
                        
                        if (user.state != null && user.constituency != null) {
                            layoutLocation.setVisibility(View.VISIBLE);
                            tvLocationDetails.setText(user.state + " - " + user.constituency);
                        }

                        // Show admin button if role is admin
                        if ("admin".equals(user.role)) {
                            btnAdmin.setVisibility(View.VISIBLE);
                        } else {
                            btnAdmin.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Silently fail or log
            }
        });
    }
}
