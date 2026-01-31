package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class Activity_OTP1 extends AppCompatActivity {
    EditText etMobileNumber;
    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp1);

        etMobileNumber = findViewById(R.id.etMobileNumber);
        ccp=findViewById(R.id.ccp);
        ccp.setCustomMasterCountries("PK");
        ccp.setDefaultCountryUsingNameCode("PK");
        ccp.resetToDefaultCountry();
        ccp.setCcpClickable(false);

        Button btnReceiveOTP = findViewById(R.id.BtnReceiveOTP);
        btnReceiveOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = "+92" +etMobileNumber.getText().toString();
                Log.d("Mobile Number", mobileNumber);

                if (!mobileNumber.isEmpty()) {
                    Intent intent = new Intent(Activity_OTP1.this, Activity_OTP2.class);
                    intent.putExtra("mobileNumber", mobileNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(Activity_OTP1.this, "Please input mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
