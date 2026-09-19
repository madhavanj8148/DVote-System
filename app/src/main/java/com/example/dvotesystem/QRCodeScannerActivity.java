package com.example.dvotesystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScannerActivity extends BaseActivity {
    private final String DB_URL = "https://dvote-system-default-rtdb.firebaseio.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        MaterialButton btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> startScanner());

        startScanner();
    }

    private void startScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getString(R.string.align_qr_desc));
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, getString(R.string.cancel_btn), Toast.LENGTH_SHORT).show();
            } else {
                verifyQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void verifyQRCode(String scannedData) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance(DB_URL).getReference("users");
        
        usersRef.orderByChild("voterId").equalTo(scannedData).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(QRCodeScannerActivity.this, R.string.reg_success, Toast.LENGTH_SHORT).show(); // Reuse success toast
                    Intent intent = new Intent(QRCodeScannerActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(QRCodeScannerActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show(); // Reuse auth failed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QRCodeScannerActivity.this, getString(R.string.error_label), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
