package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

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

public class Activity_ForgotPassword extends AppCompatActivity {
    EditText etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        etPassword.setPadding(30, 0, 30, 0);
        etConfirmPassword.setPadding(30, 0, 30, 00);

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isPasswordValid(charSequence.toString())) {
                    TextInputLayout tilPassword = findViewById(R.id.tilPassword);
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    etPassword.setError("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                }else{
                    TextInputLayout tilPassword = findViewById(R.id.tilPassword);
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {

            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isPasswordValid(charSequence.toString())) {
                    TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
                    tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    etConfirmPassword.setError("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                }else{
                    TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
                    tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button btnNext = findViewById(R.id.BtnNext);
        btnNext.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String phoneNumber = getIntent().getStringExtra("mobileNumber");

                boolean isValid = true;
                if (etPassword.getText().toString().trim().isEmpty()) {
                    TextInputLayout tilPassword = findViewById(R.id.tilPassword);
                    TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
                    tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    etPassword.setError("Password is required");
                    isValid = false;
                }else{
                    if (!isPasswordValid(password)) {
                        isValid = false;
                    }
                }
                if (!password.equals(confirmPassword)) {
                    TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
                    tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    etConfirmPassword.setError("Password does not match");
                    isValid = false;
                }
                if (etConfirmPassword.getText().toString().trim().isEmpty()) {
                    etConfirmPassword.setError("Confirm Password is required");
                    isValid = false;
                }
                if (isValid) {
                    new UpdatePasswordTask(Activity_ForgotPassword.this).execute(phoneNumber, password);
                }
            }
        });

    }

    public boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[0-9].*")) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }
        if (!password.matches(".*[a-z].*")) {
            return false;
        }
        if (!password.matches(".*[!@#$%^&*+=?-].*")) {
            return false;
        }
        return true;
    }

    private class UpdatePasswordTask extends AsyncTask<String, Void, String> {
        Context context;
        String mobileNumber; // To keep track of the mobile number

        public UpdatePasswordTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/forgot_password.php"; // Adjust path as needed
            String result = "";
            mobileNumber = params[0];
            String newPassword = params[1];

            Log.d("UpdatePasswordTask", "Mobile Number: " + mobileNumber);
            Log.d("UpdatePasswordTask", "New Password: " + newPassword);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // Set content type to JSON

                // Construct JSON data
                JSONObject dataJson = new JSONObject();
                dataJson.put("mobile_number", mobileNumber);
                dataJson.put("password", newPassword);

                // Send JSON data
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(dataJson.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("UpdatePasswordTask", "Result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");
                String message = jsonResponse.optString("Message", "Error"); // Use optString for safe parsing

                // Additional actions based on the status
                if (status == 1) {
                    Log.d("UpdatePasswordTask", "Password updated successfully.");
                    // Password updated successfully
                    Toast.makeText(context, "Password updated successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, Activity_ForgotPasswordUpdate.class); // Assuming you have a follow-up activity
                    intent.putExtra("mobileNumber", mobileNumber); // Pass mobile number if necessary
                    context.startActivity(intent);
                } else {
                    Log.d("UpdatePasswordTask", "Failed to update password: " + message);
                    if(message.contains("New password cannot be the same as the old password")){
                            TextInputLayout tilPassword = findViewById(R.id.tilPassword);
                            tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            EditText etPassword = findViewById(R.id.etPassword);
                            etPassword.setError("New password cannot be the same as the old password");
                            TextInputLayout tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
                            tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
                            etConfirmPassword.setError("New password cannot be the same as the old password");
                    }
                    // Failed to update password
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }


}