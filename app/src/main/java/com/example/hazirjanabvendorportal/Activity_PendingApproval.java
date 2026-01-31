package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.Base64;

public class Activity_PendingApproval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_approval);

        new SignupTask(this).execute();
    }

    private String blobToBase64(Blob blob) {
        if (blob == null) {
            return null;
        }
        try (InputStream inputStream = blob.getBinaryStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] blobBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(blobBytes);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class SignupTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private Class_SingletonVendor singletonVendor;

        public SignupTask(Context context) {
            this.context = context;
            this.singletonVendor = Class_SingletonVendor.getInstance();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/vendor_signup.php";  // Adjust path as needed
            String result = "";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject vendorJson = new JSONObject();
                vendorJson.put("first_name", singletonVendor.getFirst_name());
                vendorJson.put("last_name", singletonVendor.getLast_name());
                vendorJson.put("gender", singletonVendor.getGender());
                vendorJson.put("CNIC", singletonVendor.getCnic());
                vendorJson.put("password", singletonVendor.getPassword());
                vendorJson.put("email", singletonVendor.getEmail());
                vendorJson.put("phone_number", singletonVendor.getPhone_number());
                vendorJson.put("address", singletonVendor.getAddress());
                vendorJson.put("vendor_picture", blobToBase64(singletonVendor.getVendor_picture())); // Convert blob to Base64
                vendorJson.put("cnic_front", blobToBase64(singletonVendor.getCnic_front())); // Convert blob to Base64
                vendorJson.put("cnic_back", blobToBase64(singletonVendor.getCnic_front())); // Convert blob to Base64
                vendorJson.put("total_rating", singletonVendor.getTotal_rating());
                vendorJson.put("aspect_time", singletonVendor.getAspect_time());
                vendorJson.put("aspect_quality", singletonVendor.getAspect_quality());
                vendorJson.put("aspect_expertise", singletonVendor.getAspect_expertise());
                vendorJson.put("category", singletonVendor.getCategory());

                OutputStream os = httpURLConnection.getOutputStream();
                os.write(vendorJson.toString().getBytes(StandardCharsets.UTF_8));
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
            Log.d("SignupTask", "Result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");

                if (status == 1) {
                    // Signup successful
                    Toast.makeText(context, "Vendor signed up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, Activity_Login.class);
                    startActivity(intent);
                    ((Activity) context).finish();
                } else {
                    // Signup failed
                    String message = jsonResponse.optString("Message", "Signup failed");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

}