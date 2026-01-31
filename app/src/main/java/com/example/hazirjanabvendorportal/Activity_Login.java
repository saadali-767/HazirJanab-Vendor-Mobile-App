package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hazirjanabvendorportal.SplashScreens.Animation_LoginSplashScreen;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Base64;

public class Activity_Login extends AppCompatActivity {
    EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Class_SingletonVendor.destroyInstance();

        Button btnLogin = findViewById(R.id.BtnLogin);
        TextView btnSignup = findViewById(R.id.tvSignup);
        TextView  btnForgotPassword = findViewById(R.id.tvForgotPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        etPassword.setPadding(30, 0, 30, 0);

        btnLogin.setOnClickListener(view -> {
            // When the login button is clicked, attempt to login
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            // Execute the AsyncTask to perform the network request
            //String email="abdullah@gmail.com";
            //String password="1234";
            new VendorLoginTask().execute(email, password);
        });

        btnSignup.setOnClickListener(view -> {
            // When the signup button is clicked, navigate to the signup screen
            Intent intent = new Intent(Activity_Login.this, Activity_OTP1.class);
            startActivity(intent);
        });

        btnForgotPassword.setOnClickListener(view -> {
            // When the signup button is clicked, navigate to the signup screen
            Intent intent = new Intent(Activity_Login.this, Activity_ForgotPasswordOTP1.class);
            startActivity(intent);
        });
    }

    private class VendorLoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = getResources().getString(R.string.server_url);
            String loginUrl = serverUrl + "hazirjanab/vendor_login.php";
            String result = "";
            String email = params[0];
            String password = params[1];

            try {
                URL url = new URL(loginUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") +
                        "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

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
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");
                String message = jsonResponse.getString("Message");

                Toast.makeText(Activity_Login.this, message, Toast.LENGTH_LONG).show();

                if (status == 1) {
                    JSONObject vendorJson = jsonResponse.getJSONObject("User");
                    //User information
                    String vendor_id = vendorJson.getString("id");
                    String vendor_first_name = vendorJson.getString("first_name");
                    Log.d("UserFirstName", vendor_first_name);
                    String vendor_last_name = vendorJson.getString("last_name");
                    Log.d("UserLastName", vendor_last_name);
                    String vendor_gender = vendorJson.getString("gender");
                    Log.d("UserGender", vendor_gender);
                    String vendor_cnic = vendorJson.getString("CNIC");
                    Log.d("UserCNIC", vendor_cnic);
                    String vendor_password = vendorJson.getString("password");
                    Log.d("UserPassword", vendor_password);
                    String vendor_email = vendorJson.getString("email");
                    Log.d("UserEmail", vendor_email);
                    String vendor_phone_number = vendorJson.getString("phone_number");
                    Log.d("UserPhoneNumber", vendor_phone_number);
                    String vendor_address = vendorJson.getString("Address");
                    Log.d("UserAddress", vendor_address);

                    //Vendor Picture
                    String picture_base64 = vendorJson.getString("picture");
                    Log.d("Base64", picture_base64);
                    byte[] picture_decodedByte = Base64.getDecoder().decode(picture_base64);
                    // Create a Blob object from the byte array
                    Blob picture_blob = new Blob() {
                        @Override
                        public long length() throws SQLException {
                            return picture_decodedByte.length;
                        }

                        @Override
                        public byte[] getBytes(long pos, int length) throws SQLException {
                            return new byte[0]; // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream() throws SQLException {
                            return new ByteArrayInputStream(picture_decodedByte);
                        }

                        @Override
                        public long position(byte[] pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public long position(Blob pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public OutputStream setBinaryStream(long pos) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void truncate(long len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void free() throws SQLException {
                            // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream(long pos, long length) throws SQLException {
                            return new ByteArrayInputStream(picture_decodedByte);
                        }
                    };

                    //Vendor CNIC Front
                    String cnic_front_base64 = vendorJson.getString("cnic_front");
                    Log.d("Base64", cnic_front_base64);
                    byte[] cnic_front_decodedByte = Base64.getDecoder().decode(cnic_front_base64);
                    // Create a Blob object from the byte array
                    Blob cnic_front_blob = new Blob() {
                        @Override
                        public long length() throws SQLException {
                            return cnic_front_decodedByte.length;
                        }

                        @Override
                        public byte[] getBytes(long pos, int length) throws SQLException {
                            return new byte[0]; // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream() throws SQLException {
                            return new ByteArrayInputStream(cnic_front_decodedByte);
                        }

                        @Override
                        public long position(byte[] pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public long position(Blob pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public OutputStream setBinaryStream(long pos) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void truncate(long len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void free() throws SQLException {
                            // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream(long pos, long length) throws SQLException {
                            return new ByteArrayInputStream(cnic_front_decodedByte);
                        }
                    };

                    //Vendor CNIC Back
                    String cnic_back_base64 = vendorJson.getString("cnic_back");
                    Log.d("Base64", cnic_back_base64);
                    byte[] cnic_back_decodedByte = Base64.getDecoder().decode(cnic_back_base64);
                    // Create a Blob object from the byte array
                    Blob cnic_back_blob = new Blob() {
                        @Override
                        public long length() throws SQLException {
                            return cnic_back_decodedByte.length;
                        }

                        @Override
                        public byte[] getBytes(long pos, int length) throws SQLException {
                            return new byte[0]; // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream() throws SQLException {
                            return new ByteArrayInputStream(cnic_back_decodedByte);
                        }

                        @Override
                        public long position(byte[] pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public long position(Blob pattern, long start) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public OutputStream setBinaryStream(long pos) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void truncate(long len) throws SQLException {
                            throw new SQLFeatureNotSupportedException();
                        }

                        @Override
                        public void free() throws SQLException {
                            // Implement as needed
                        }

                        @Override
                        public InputStream getBinaryStream(long pos, long length) throws SQLException {
                            return new ByteArrayInputStream(cnic_back_decodedByte);
                        }
                    };

                    float total_rating = (float) vendorJson.getDouble("total_rating");
                    Log.d("TotalRating", String.valueOf(total_rating));
                    float aspect_time = (float) vendorJson.getDouble("aspect_time");
                    Log.d("AspectTime", String.valueOf(aspect_time));
                    float aspect_quality = (float) vendorJson.getDouble("aspect_quality");
                    Log.d("AspectQuality", String.valueOf(aspect_quality));
                    float aspect_expertise = (float) vendorJson.getDouble("aspect_expertise");
                    Log.d("AspectExpertise", String.valueOf(aspect_expertise));
                    String category = vendorJson.getString("category");
                    Log.d("Category", category);
                    int approval_status = vendorJson.getInt("approval_status");
                    Log.d("ApprovalStatus", String.valueOf(approval_status));
                    int total_orders = vendorJson.getInt("total_orders");
                    Log.d("TotalOrders", String.valueOf(total_orders));

                    if(approval_status==0){
                        Toast.makeText(Activity_Login.this, "Your account is not approved yet", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Class_SingletonVendor singletonVendor = Class_SingletonVendor.getInstance();
                    if(singletonVendor.getId()==0){
                        singletonVendor.setId(Integer.parseInt(vendor_id));
                        singletonVendor.setFirst_name(vendor_first_name);
                        singletonVendor.setLast_name(vendor_last_name);
                        singletonVendor.setGender(vendor_gender);
                        singletonVendor.setCnic(vendor_cnic);
                        singletonVendor.setPassword(vendor_password);
                        singletonVendor.setEmail(vendor_email);
                        singletonVendor.setPhone_number(vendor_phone_number);
                        singletonVendor.setAddress(vendor_address);
                        singletonVendor.setVendor_picture(picture_blob);
                        singletonVendor.setCnic_front(cnic_front_blob);
                        singletonVendor.setCnic_back(cnic_back_blob);
                        singletonVendor.setTotal_rating(total_rating);
                        singletonVendor.setAspect_time(aspect_time);
                        singletonVendor.setAspect_quality(aspect_quality);
                        singletonVendor.setAspect_expertise(aspect_expertise);
                        singletonVendor.setCategory(category);
                        singletonVendor.setApproval_status(approval_status);
                        singletonVendor.setTotal_orders(total_orders);
                    }

                    singletonVendor.DisplayAllInformation();

                    // If login is successful, navigate to the requests list screen
                    Intent intent = new Intent(Activity_Login.this, Animation_LoginSplashScreen.class);
                    startActivity(intent);
                    finish(); // Close the login activity
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(Activity_Login.this, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }
}
