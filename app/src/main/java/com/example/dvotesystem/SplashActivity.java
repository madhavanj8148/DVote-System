package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvTitle = findViewById(R.id.tvSplashTitle);
        TextView tvSubTitle = findViewById(R.id.tvSplashSubTitle);
        View lottieView = findViewById(R.id.lottieSplash);

        // Entry Animations
        lottieView.setAlpha(0f);
        lottieView.setScaleX(0.5f);
        lottieView.setScaleY(0.5f);
        
        lottieView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        tvTitle.setAlpha(0f);
        tvTitle.setTranslationY(50f);
        tvTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(400)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        tvSubTitle.setAlpha(0f);
        tvSubTitle.animate()
                .alpha(0.8f)
                .setDuration(1000)
                .setStartDelay(800)
                .start();

        // Seed official data to Firebase (Background)
        ConstituencySeeder.seedData();

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;
            if (currentUser != null) {
                intent = new Intent(SplashActivity.this, BiometricActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 3000);
    }
}
