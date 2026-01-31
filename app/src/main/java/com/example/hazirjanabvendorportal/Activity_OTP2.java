package com.example.hazirjanabvendorportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthOptions;

import java.util.concurrent.TimeUnit;

public class Activity_OTP2 extends AppCompatActivity {
    private PinView pvOTPInput;
    private String codeBySystem;
    private Button btnVerifyOTP;
    private FirebaseAuth mAuth; // FirebaseAuth instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp2);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth instance
        String mobileNumber = getIntent().getStringExtra("mobileNumber");
        Log.d("Mobile Number", mobileNumber);
        pvOTPInput = findViewById(R.id.pvOTPInput);
        btnVerifyOTP = findViewById(R.id.BtnVerifyOTP);

        sendVerificationCode(mobileNumber);

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = pvOTPInput.getText().toString();
                if (!code.isEmpty() && code.length() == 6) {
                    verifyCode(code);
                } else {
                    Toast.makeText(Activity_OTP2.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerificationCode(String mobileNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mobileNumber)        // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS)   // Timeout and unit
                .setActivity(this)                   // Activity for callback binding
                .setCallbacks(mCallBacks)            // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeBySystem = s;
            Log.d("Code", s);
            Log.d("Token", forceResendingToken.toString());
            Log.d("Code", codeBySystem);
            Log.d("CodeSent", "Code sent successfully");
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                pvOTPInput.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(Activity_OTP2.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(Activity_OTP2.this, Activity_Signup.class);
                            intent.putExtra("mobileNumber", getIntent().getStringExtra("mobileNumber"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(Activity_OTP2.this, "Verification not completed! Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
