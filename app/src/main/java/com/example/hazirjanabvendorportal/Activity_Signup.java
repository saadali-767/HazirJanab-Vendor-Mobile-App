package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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
import java.util.ArrayList;

public class Activity_Signup extends AppCompatActivity {

    boolean isEmailUnique = false, isCnicUnique = false, asyncTaskStatus = false;
    EditText etEmail_temp, etCnic_temp;
    String FirstName_Global, LastName_Global, Gender_Global, Cnic_Global, Password_Global, Email_Global, PhoneNumber_Global, Address_Global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        Spinner spGender = findViewById(R.id.spGender);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        String phone_number = getIntent().getStringExtra("mobileNumber");
        EditText etAddress = findViewById(R.id.etAddress);
        EditText etCnic = findViewById(R.id.etCnic);
        TextView spinnerError = (TextView) spGender.getSelectedView();

        etEmail_temp = etEmail;
        etCnic_temp = etCnic;

        String[] genderItems = {"Male", "Female", "Other"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderItems);
        spGender.setAdapter(genderAdapter);

        spGender.setPadding(10, 30, 10, 30);
        etPassword.setPadding(30, 0, 30, 0);
        etConfirmPassword.setPadding(30, 0, 30, 0);

        etCnic.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (isUpdating) {
                    oldText = text;
                    return;
                }

                // Check if the user is deleting or adding characters
                if (text.length() > oldText.length()) {
                    // Adding characters
                    if (text.length() == 6 || text.length() == 14) { // After 5th and 12th characters
                        isUpdating = true;
                        text = oldText + "-" + text.charAt(text.length() - 1);
                        etCnic.setText(text);
                        etCnic.setSelection(text.length());
                    }
                }
                oldText = text;
                isUpdating = false;
            }
        });


        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidEmail(s.toString())) {
                    etEmail.setError("Invalid Email");
                }
            }
        });
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

        spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Item was selected, clear the error.
                if (position > 0) {
                    spinnerError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No item selected, do nothing.
            }
        });



        Button btnNext = findViewById(R.id.BtnNext);
        btnNext.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                isEmailUnique = false;
                isCnicUnique = false;
                asyncTaskStatus = false;

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String address = etAddress.getText().toString();
                String cnic = etCnic.getText().toString();
                String gender = spGender.getSelectedItem().toString();
                String phoneNumber = getIntent().getStringExtra("mobileNumber");

                boolean isValid = true;

                // Validate each field
                if (etFirstName.getText().toString().trim().isEmpty()) {
                    etFirstName.setError("First Name is required");
                    isValid = false;
                }
                if (etLastName.getText().toString().trim().isEmpty()) {
                    etLastName.setError("Last Name is required");
                    isValid = false;
                }
                if (etCnic.getText().toString().trim().isEmpty()) {
                    etCnic.setError("CNIC is required");
                    isValid = false;
                }
                if (etEmail.getText().toString().trim().isEmpty()) {
                    etEmail.setError("Email is required");
                    isValid = false;
                }
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
                if (etAddress.getText().toString().trim().isEmpty()) {
                    etAddress.setError("Address is required");
                    isValid = false;
                }

                if (isValid) {

                    FirstName_Global = firstName;
                    LastName_Global = lastName;
                    Gender_Global = gender;
                    Cnic_Global = cnic;
                    Password_Global = password;
                    Email_Global = email;
                    PhoneNumber_Global = phoneNumber;
                    Address_Global = address;


                    new CheckEmailAndCnicUniqueTask(Activity_Signup.this).execute(cnic, email);
                }
            }
        });

    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private class CheckEmailAndCnicUniqueTask extends AsyncTask<String, Void, String> {
        Context context;

        public CheckEmailAndCnicUniqueTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/check_email_cnic_uniqueness.php"; // Change to the actual path of your API
            String result = "";
            String cnic = params[0];
            String email = params[1];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                postParams += "&" + URLEncoder.encode("cnic", "UTF-8") + "=" + URLEncoder.encode(cnic, "UTF-8");
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
            Log.d("CheckEmailCnicUnique", "Result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int emailStatus = jsonResponse.getInt("EmailStatus");
                String emailMessage = jsonResponse.getString("EmailMessage");
                int cnicStatus = jsonResponse.getInt("CnicStatus");
                String cnicMessage = jsonResponse.getString("CnicMessage");
                String email = jsonResponse.getString("Email");
                String cnic = jsonResponse.getString("Cnic");

                // Additional actions based on the email status
                if (emailStatus == 1) {
                    Log.d("CheckEmailCnicUnique", "Email is unique.");
                    isEmailUnique= true;
                } else {
                    Log.d("CheckEmailCnicUnique", "Email already exists.");
                    etEmail_temp.setError(emailMessage);
                }

                // Additional actions based on the CNIC status
                if (cnicStatus == 1) {
                    Log.d("CheckEmailCnicUnique", "CNIC is unique.");
                    isCnicUnique = true;
                } else {
                    Log.d("CheckEmailCnicUnique", "CNIC already exists.");
                    etCnic_temp.setError(cnicMessage);
                }

                if(isEmailUnique && isCnicUnique){
                    asyncTaskStatus = true;
                }
                if(asyncTaskStatus){
                    Toast.makeText(context, "Email and CNIC are unique", Toast.LENGTH_LONG).show();
                    Class_SingletonVendor.getInstance().setId(-1);
                    Class_SingletonVendor.getInstance().setFirst_name(FirstName_Global);
                    Class_SingletonVendor.getInstance().setLast_name(LastName_Global);
                    Class_SingletonVendor.getInstance().setGender(Gender_Global);
                    Class_SingletonVendor.getInstance().setCnic(Cnic_Global);
                    Class_SingletonVendor.getInstance().setPassword(Password_Global);
                    Class_SingletonVendor.getInstance().setEmail(Email_Global);
                    Class_SingletonVendor.getInstance().setPhone_number(PhoneNumber_Global);
                    Class_SingletonVendor.getInstance().setAddress(Address_Global);

                    Intent intent = new Intent(Activity_Signup.this, Activity_Dashboard.class);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

}