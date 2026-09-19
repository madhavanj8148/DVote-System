package com.example.dvotesystem;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.Random;

public class BiometricSuccessActivity extends BaseActivity {

    private FrameLayout particleContainer;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Fullscreen and Transparent Status Bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_biometric_success);

        particleContainer = findViewById(R.id.particleContainer);
        View rotatingRing = findViewById(R.id.rotatingRing);
        ImageView ivNamaste = findViewById(R.id.ivNamaste);
        ImageView ivFingerprint = findViewById(R.id.ivFingerprint);
        View scanLine = findViewById(R.id.scanLine);
        View textContainer = findViewById(R.id.textContainer);

        // Initial States
        ivNamaste.setAlpha(0f);
        ivNamaste.setScaleX(0.7f);
        ivNamaste.setScaleY(0.7f);
        textContainer.setAlpha(0f);
        textContainer.setTranslationY(60f);

        // 1. Entrance Animation
        ivNamaste.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        textContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1200)
                .setStartDelay(600)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        // 2. Rotating & Pulsing Ring
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(rotatingRing, "rotation", 0f, 360f);
        rotateAnim.setDuration(10000);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.start();

        ObjectAnimator pulseRingX = ObjectAnimator.ofFloat(rotatingRing, "scaleX", 1f, 1.08f, 1f);
        ObjectAnimator pulseRingY = ObjectAnimator.ofFloat(rotatingRing, "scaleY", 1f, 1.08f, 1f);
        pulseRingX.setDuration(4000);
        pulseRingY.setDuration(4000);
        pulseRingX.setRepeatCount(ValueAnimator.INFINITE);
        pulseRingY.setRepeatCount(ValueAnimator.INFINITE);
        pulseRingX.start();
        pulseRingY.start();

        // 3. Neon Fingerprint Pulse Glow
        ObjectAnimator fpGlow = ObjectAnimator.ofFloat(ivFingerprint, "alpha", 0.5f, 1.0f, 0.5f);
        fpGlow.setDuration(2000);
        fpGlow.setRepeatCount(ValueAnimator.INFINITE);
        fpGlow.start();

        // 4. Smooth Scanning Line
        ObjectAnimator scanAnim = ObjectAnimator.ofFloat(scanLine, "translationY", 0f, 350f);
        scanAnim.setDuration(3000);
        scanAnim.setRepeatCount(ValueAnimator.INFINITE);
        scanAnim.setRepeatMode(ValueAnimator.REVERSE);
        scanAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scanAnim.start();

        // 5. Floating Particles
        createParticles();

        // 6. Navigate to Home
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(BiometricSuccessActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 5000);
    }

    private void createParticles() {
        for (int i = 0; i < 20; i++) {
            View particle = new View(this);
            int size = random.nextInt(10) + 5;
            particle.setLayoutParams(new FrameLayout.LayoutParams(size, size));
            particle.setBackgroundResource(R.drawable.particle_shape);
            particle.setAlpha(random.nextFloat() * 0.5f + 0.2f);
            
            float startX = random.nextFloat() * getResources().getDisplayMetrics().widthPixels;
            float startY = random.nextFloat() * getResources().getDisplayMetrics().heightPixels;
            particle.setX(startX);
            particle.setY(startY);
            
            particleContainer.addView(particle);
            
            animateParticle(particle);
        }
    }

    private void animateParticle(View particle) {
        float moveX = (random.nextFloat() - 0.5f) * 200;
        float moveY = (random.nextFloat() - 0.5f) * 200;
        
        particle.animate()
                .translationXBy(moveX)
                .translationYBy(moveY)
                .setDuration(random.nextInt(3000) + 2000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> animateParticle(particle))
                .start();
    }
}
