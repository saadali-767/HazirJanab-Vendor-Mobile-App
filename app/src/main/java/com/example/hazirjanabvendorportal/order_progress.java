package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import java.util.HashMap;
import java.util.Map;

public class order_progress extends AppCompatActivity implements Stopwatch.StopwatchCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    Stopwatch stopwatch;
    RelativeLayout stopwatchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_progress);
        Intent intent = getIntent();
        int booking_id = intent.getIntExtra("booking_id", 0);
        Log.d("booking id for complete service", String.valueOf(booking_id));
        String booking_address = intent.getStringExtra("address");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("coordinates");
        startLocationUpdates();
        stopwatchLayout = findViewById(R.id.Stopwatch);
        stopwatch = new Stopwatch();
        stopwatch.setup(this, booking_id, this);


    }
    @Override
    public void onStopwatchStop(int bookingId) {
        new CompleteServiceTask(this).execute(String.valueOf(bookingId));
        // You might want to handle 'finish()' in a different way, especially if it's supposed to close the activity.
    }

    private void startLocationUpdates() {
        Log.d("TAG", "startLocationUpdates: ");
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval
        locationRequest.setFastestInterval(5000); // Fastest interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("TAG", "exiting: ");

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("TAG", "onLocationResult: ");

                    updateLocationInFirebase(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions...
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateLocationInFirebase(Location location) {
        // You can use booking_id or any other unique identifier related to the order
        String vendorId = String.valueOf(Class_SingletonVendor.getInstance().getId()); // Use your singleton to get the vendor ID
        Log.d("TAG", "updateLocationInFirebase: update");
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("latitude", location.getLatitude());
        locationMap.put("longitude", location.getLongitude());

        databaseReference.child(vendorId).updateChildren(locationMap)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Location updated for booking: " + vendorId))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to update location for booking: " + vendorId, e));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    class CompleteServiceTask extends AsyncTask<String, Void, String> {
        Context context;
        public CompleteServiceTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/complete_booking.php";
            String result = "";
            String bookingId = params[0];
            Log.d("BookingID", bookingId);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                Log.d("UserID", bookingId);
                Log.d("httpURLConnection", httpURLConnection.toString());

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("booking_id", "UTF-8") + "=" + URLEncoder.encode(bookingId, "UTF-8");
                Log.d("PostParams", postParams);
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                    Log.d("Response", response.toString());
                }
                br.close();

                result = response.toString();
                Log.d("Result", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Result", result);
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");
                String message = jsonResponse.getString("Message");
                Log.d("AcceptServiceTask Message", message);
                if(message!=null && !message.contains("Booking status updated to 'accepted'")){
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                if (status == 1) {
                    Log.d("BookingAccepted", "Booking Accepted");
                    finish();
                    Intent intent = new Intent(context, Activity_RequestsList.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }
}