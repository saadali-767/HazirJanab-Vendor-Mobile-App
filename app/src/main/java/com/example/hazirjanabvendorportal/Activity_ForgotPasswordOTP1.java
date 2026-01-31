package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Activity_ForgotPasswordOTP1 extends AppCompatActivity {

    EditText etMobileNumber;
    CountryCodePicker ccp;
    String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_otp1);

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
                mobileNumber = "+92" +etMobileNumber.getText().toString();
                Log.d("Mobile Number", mobileNumber);

                if (!mobileNumber.isEmpty()) {
                    new CheckNumberExistsTask(Activity_ForgotPasswordOTP1.this).execute(mobileNumber);
                } else {
                    Toast.makeText(Activity_ForgotPasswordOTP1.this, "Please input mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class CheckNumberExistsTask extends AsyncTask<String, Void, String> {
        Context context;

        public CheckNumberExistsTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/checkPhoneNumberExists.php"; // Change to the actual path of your API
            String result = "";
            String inputNumber = params[0];  // Assuming the number you want to check is the first parameter

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("phone_number", "UTF-8") + "=" + URLEncoder.encode(inputNumber, "UTF-8");
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("CheckNumberExists", "Result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                JSONObject vendorData = jsonResponse.getJSONObject("Vendor");
                int numberStatus = jsonResponse.getInt("Status");
                String numberMessage = jsonResponse.getString("Message");
                String firstName = vendorData.getString("first_name");
                String lastName = vendorData.getString("last_name");
                String FullName = firstName + " " + lastName;

                // Additional actions based on the number status
                if (numberStatus == 1) {
                    Log.d("CheckNumberExists", "Number is unique.");
                    //Toast.makeText(context, "Number already exists", Toast.LENGTH_LONG).show();
                    etMobileNumber.setError("No account is registered with this number. Please enter a registered number.");
                    // Actions when the number is unique
                } else {
                    Log.d("CheckNumberExists", "Number already exists.");
                    Intent intent = new Intent(Activity_ForgotPasswordOTP1.this, Activity_ForgotPasswordOTP2.class);
                    intent.putExtra("mobileNumber", mobileNumber);
                    intent.putExtra("fullName", FullName);
                    startActivity(intent);
                    // Actions when the number already exists, like showing an error message
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

}