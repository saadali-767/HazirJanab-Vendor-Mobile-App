package com.example.hazirjanabvendorportal;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Activity_RequestsList extends AppCompatActivity implements RecyclerViewInterface {
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseReference;
    private LocationCallback locationCallback;
    RecyclerView rvRequestsList;
    List<Class_ServiceBooking> requestsList;
    Adapter_ServiceBookings adapter_serviceBookings;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    private View overlayView;
    private ProgressDialog progressDialog;
    LinearLayout llNoRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_list);
        showOverlay();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("coordinates");
        startLocationUpdates();

        toolbar = Toolbar.getInstance();
        toolbar.setup(this);

        NotificationBadge.getInstance(this).setCounter("Home", 0);


        rvRequestsList = findViewById(R.id.RequestsRecyclerView);
        rvRequestsList.setLayoutManager(new LinearLayoutManager(this));

        llNoRequests = findViewById(R.id.llNoRequests);

        requestsList = new ArrayList<>();
        if(requestsList.isEmpty()) {
            llNoRequests.setVisibility(View.VISIBLE);
        }
        new GetRequests(this).execute();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestsList.clear();
                NotificationBadge.getInstance(Activity_RequestsList.this).setCounter("Home", 0);
                new GetRequests(Activity_RequestsList.this).execute();
            }
        });


        //requestsList.add(new Class_ServiceBooking(1, 1, 1, 1, "Karachi", "Gulshan",new Date(123, 1, 1), new Time(1,2,3), "Description", null, "Pending"));
        adapter_serviceBookings = new Adapter_ServiceBookings(Activity_RequestsList.this, requestsList, this);
        rvRequestsList.setAdapter(adapter_serviceBookings);
        adapter_serviceBookings.notifyDataSetChanged();
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener(aVoid -> Log.d("LocationUpdates", "Location updates stopped"))
                .addOnFailureListener(e -> Log.e("LocationUpdates", "Failed to stop location updates", e));
    }


    private void startLocationUpdates() {
        Log.d("mujeeb", "startLocationUpdates: ");
        // Create location request and location callback
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval
        locationRequest.setFastestInterval(5000); // Fastest interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("mujeeb", "return: ");

                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update Firebase database with new location
                    updateLocationInFirebase(location);
                }
            }
        };

        // Check for location permissions here and request if not provided (not shown for brevity)

        // Start location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateLocationInFirebase(Location location) {
        String vendorId = String.valueOf(Class_SingletonVendor.getInstance().getId()); // Use your singleton to get the vendor ID

        // Create a map with the location data
        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("latitude", location.getLatitude());
        locationMap.put("longitude", location.getLongitude());

        // Update the location data under the vendor's ID
        databaseReference.child(vendorId).updateChildren(locationMap)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Location updated for vendor: " + vendorId))
                .addOnFailureListener(e -> Log.d("Firebase", "Failed to update location for vendor: " + vendorId, e));
    }

    @Override
    public void onItemClicked(int adapterPosition) {
        Log.d("Position", String.valueOf(adapterPosition));
        if(requestsList.isEmpty()){
            return;
        }
        Intent intent = new Intent(this, Activity_RequestDetails.class);
        intent.putExtra("id", requestsList.get(adapterPosition).getId());
        intent.putExtra("customerId", requestsList.get(adapterPosition).getUser_id());
        intent.putExtra("serviceId", requestsList.get(adapterPosition).getService_id());
        intent.putExtra("vendorId", requestsList.get(adapterPosition).getVendor_id());
        intent.putExtra("city", requestsList.get(adapterPosition).getCity());
        intent.putExtra("address", requestsList.get(adapterPosition).getAddress());
        if (requestsList.get(adapterPosition).getDate() != null) {
            Log.d("Date", requestsList.get(adapterPosition).getDate().toString());
            intent.putExtra("date", requestsList.get(adapterPosition).getDate());
        } else
            Log.d("Date", "null");

        if (requestsList.get(adapterPosition).getTime() != null) {
            Log.d("Time", requestsList.get(adapterPosition).getTime().toString());
            intent.putExtra("time", requestsList.get(adapterPosition).getTime());
        } else{
            Log.d("Time", "null");
        }
        intent.putExtra("description", requestsList.get(adapterPosition).getDescription());

        Blob pictureBlob = requestsList.get(adapterPosition).getPicture();
        Bitmap bitmap = null;
        byte[] pictureBytes = null;

        try {
            // Get the length of the Blob
            long length = pictureBlob.length();

            if (length > 0) {
                // The Blob contains valid data
                Log.d("BlobData", "Blob length: " + length);
                // Read bytes from the Blob using an input stream
                InputStream inputStream = pictureBlob.getBinaryStream();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                pictureBytes = outputStream.toByteArray();
                inputStream.close();
                outputStream.close();

                // Check the length of the byte array
                Log.d("BlobData", "Byte array length: " + pictureBytes.length);
//                bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
//                Log.d("BlobData", "Bitmap: " + bitmap.toString());
            } else {
                // The Blob does not contain any data
                Log.d("BlobData", "Blob is empty");
            }
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        intent.putExtra("attached_pictureBytes", pictureBytes!=null?pictureBytes:"check here" );
        intent.putExtra("type", requestsList.get(adapterPosition).getType());
        intent.putExtra("status", requestsList.get(adapterPosition).getStatus());

       startActivity(intent);
    }

    private void showOverlay() {
        // Create and add the overlay view
        overlayView = new View(this);
        overlayView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlayView.setBackgroundColor(Color.parseColor("#80000000")); // Semi-transparent black color
        ViewGroup rootView = findViewById(android.R.id.content);
        rootView.addView(overlayView);

        // Show a progress dialog as an alternative to overlay view
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we fetch important data...");
        progressDialog.setCancelable(false); // Prevent dismiss on touch outside
        progressDialog.show();
    }

    private void dismissOverlay() {
        // Remove the overlay view
        if (overlayView != null && overlayView.getParent() != null) {
            ((ViewGroup) overlayView.getParent()).removeView(overlayView);
        }

        // Dismiss the progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class GetRequests extends AsyncTask<Void, Void, String> {
        private Context context;
        private int vendorId;
        private Class_SingletonVendor singletonVendor;

        public GetRequests(Context context) {
            this.context = context;
            this.vendorId= Class_SingletonVendor.getInstance().getId();
        }
        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/bookings.php";
            String result = "";

            Log.d("VendorId", String.valueOf(vendorId));

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                Log.d("ApiUrl", apiUrl);
                Log.d("HttpURLConnection", httpURLConnection.toString());

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("vendor_id", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(vendorId), "UTF-8");
                Log.d("PostParams", postParams);
                writer.write(postParams);
                writer.flush();
                writer.close();
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                Log.d("BufferedReader", br.toString());
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
            Log.d("TAG", "doInBackground: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");
                Log.d("Status", String.valueOf(status));
//                String message = jsonResponse.getString("Message");
//                Log.d("Message", message);

                if (status == 1) {
                    JSONArray bookingsArray = jsonResponse.getJSONArray("Bookings");

                    // Populate the list with booking records
                    for (int i = 0; i < bookingsArray.length(); i++) {
                        JSONObject bookingJson = bookingsArray.getJSONObject(i);
                        // Assuming each booking record has a "booking_info" field
                        String bookingId = bookingJson.getString("id");
                        String userId = bookingJson.getString("user_id");
                        String serviceId = bookingJson.getString("service_id");
                        String vendorId = bookingJson.getString("vendor_id");
                        String city = bookingJson.getString("city");
                        String address = bookingJson.getString("address");
                        String dateString = bookingJson.getString("date");
//                        String date = new Date(2021, 1, 1).toString();
                        String time = bookingJson.getString("time");
                        Log.d("Time", time);
                        Log.d("Date", dateString);

                        String description = bookingJson.getString("description");

                        String base64 = bookingJson.getString("picture");
                        Log.d("Base64", base64);
                        byte[] decodedByte = Base64.getDecoder().decode(base64);
                        // Create a Blob object from the byte array
                        Blob blob = new Blob() {
                            @Override
                            public long length() throws SQLException {
                                return decodedByte.length;
                            }

                            @Override
                            public byte[] getBytes(long pos, int length) throws SQLException {
                                return new byte[0]; // Implement as needed
                            }

                            @Override
                            public InputStream getBinaryStream() throws SQLException {
                                return new ByteArrayInputStream(decodedByte);
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
                                return new ByteArrayInputStream(decodedByte);
                            }
                        };
                        try {
                            if (blob != null && blob.length() > 0) {
                                // Blob contains data
                                Log.d("BlobData", "Blob contains data");
                            } else {
                                // Blob is empty
                                Log.d("BlobData", "Blob is empty");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // Handle SQLException
                        }

                        String type = bookingJson.getString("type");
                        String booking_status = bookingJson.getString("status");

                        Log.d("Time", time);
                        Log.d("Date", dateString);

                        Date date;
                        if (dateString.equals("0000-00-00") || dateString.equals("null") || dateString.equals("")) {
                            date = null; // or assign a default date value
                        } else {
                            try {
                                Log.d("Date: ", dateString);
                                // Parse the date string
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                date = new Date(dateFormat.parse(dateString).getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                                // Handle parsing exception
                                Log.d("Date", "Error parsing date");
                                date = null; // or assign a default date value
                            }
                        }

                        Class_ServiceBooking bookingInfo = new Class_ServiceBooking(Integer.parseInt(bookingId), Integer.parseInt(userId), Integer.parseInt(serviceId), Integer.parseInt(vendorId), city, address, date, time, description, blob, type, booking_status);
                        if(bookingInfo.getStatus().equals("Pending")){
                            requestsList.add(bookingInfo);
                            NotificationBadge.getInstance(Activity_RequestsList.this).incrementCounter("Home");
                            Log.d("Check3:","Done");
                        }
                        Log.d("Check","Done");
                    }
                    Log.d("Check2","Done");
                    adapter_serviceBookings.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    String message = "Error: " + jsonResponse.optString("Message", "Unknown error");
                    if(!message.contains("No bookings found for vendor ID:")){
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                }
                dismissOverlay();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
            if (requestsList.isEmpty()) {
                llNoRequests.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }else {
                llNoRequests.setVisibility(View.GONE);
            }
        }
    }


}

