package com.example.hazirjanabvendorportal;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_RequestDetails extends AppCompatActivity implements RecyclerViewInterface {

    //Service Information
    String service_name, service_hourly_rate, service_description, service_city, service_category, service_type, service_img_url, customer_number;

    //User Information
    String user_first_name, user_last_name, user_profile_pic;

    //activity_request_details.xml
    TextView tv_price, tv_service_name, tv_service_description, tv_service_type, tv_user_name,tv_date,tv_address,tv_status,tv_time,tv_note;

    //TextViews Static
    TextView tvDateText, tvTimeText, tv_customer_number;

    ImageView iv_serviceRequested, ivType, ivSupport;
    PhotoView iv_imageAttached;

    byte[] service_pictureBytes;

    Button rejectButton, acceptButton,startOrder;
    Stopwatch stopwatch;
    RelativeLayout stopwatchLayout;
    LinearLayout llProduct,llCustomerContact;
    Toolbar toolbar;
    userordersproductadapter cartProductAdapter;
    List<Product> productList;
    List<Product> productList2;
    // List<productorders> orders;
    List<productorders> pList;
    RecyclerView rvProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        toolbar = Toolbar.getInstance();
        toolbar.setup(this);


        Intent intent = getIntent();
        productList = new ArrayList<>();

        productList2 = new ArrayList<>();
        pList = new ArrayList<>();
        int booking_id = intent.getIntExtra("id", 0);
        int user_id = intent.getIntExtra("customerId", 0);
        int service_id = intent.getIntExtra("serviceId", 0);
        int vendor_id = intent.getIntExtra("vendorId", 0);
        String booking_city = intent.getStringExtra("city");
        String booking_address = intent.getStringExtra("address");
        Log.d("date", String.valueOf((Date) intent.getSerializableExtra("date")));
        Date booking_date = (Date) intent.getSerializableExtra("date");
        Log.d("time", intent.getStringExtra("time"));
        String booking_time = intent.getStringExtra("time");
        String booking_description = intent.getStringExtra("description");
        String booking_type = intent.getStringExtra("type");
        String booking_status = intent.getStringExtra("status");
        byte[] pictureBytes = getIntent().getByteArrayExtra("attached_pictureBytes");
        Log.d("BlobData", "Byte array length: " + pictureBytes.length);

        llProduct = findViewById(R.id.llProduct);

        fetchProductsFromDatabase(booking_id);
        rvProduct = findViewById(R.id.rvProductList);
        productList = new ArrayList<>();
        //initializeProductList(); // This method will populate the productList

        cartProductAdapter = new userordersproductadapter(Activity_RequestDetails.this,pList, productList2,  this);
        rvProduct.setLayoutManager(new LinearLayoutManager(this));
        rvProduct.setAdapter(cartProductAdapter);


        // Get service information
        new GetServiceInfoTask(this).execute(String.valueOf(service_id));
        Log.d("UserIDCheck", String.valueOf(user_id));
        new GetUserInfoTask(this).execute(String.valueOf(user_id));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tv_price.setText(service_hourly_rate);
                Log.d("user_first_name", user_first_name);
                Log.d("user_last_name", user_last_name);
                tv_user_name.setText(user_first_name + " " + user_last_name);
                tv_customer_number.setText(customer_number);
                tv_service_name.setText(service_name);
                tv_service_type.setText(booking_type);
                tv_status.setText(booking_status);
                String date;
                if(booking_type.equals("Emergency")){
                    tv_date.setVisibility(View.GONE);
                    tv_time.setVisibility(View.GONE);
                    tvDateText.setVisibility(View.GONE);
                    tvTimeText.setVisibility(View.GONE);
                }else if(booking_date!=null && booking_time!=null){
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    date = outputFormat.format(booking_date);
                    tv_date.setText(String.valueOf(date));
                    tv_time.setText(String.valueOf(booking_time));
                }
                else if (booking_date!=null) {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                    date = outputFormat.format(booking_date);
                    tv_date.setText(String.valueOf(date));
                } else if (booking_time!=null) {
                    tv_time.setText(String.valueOf(booking_time));
                }else{
                    tv_date.setText("Not Available");
                    tv_time.setText("Not Available");
                }

                tv_address.setText(booking_address);
                tv_note.setText(booking_description);
                if (pictureBytes != null && pictureBytes.length > 0) {
                    // Convert byte array to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);

                    // Display Bitmap in ImageView
                    iv_imageAttached.setImageBitmap(bitmap);
                    // Set an OnClickListener to show a toast when the user clicks the image
                    iv_imageAttached.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openImageDialog(iv_imageAttached, bitmap); // Pass the bitmap to display in the dialog
                        }
                    });
                } else {
                    Log.d("BlobData", "No image attached");
                }
                if (service_pictureBytes != null && service_pictureBytes.length > 0) {
                    // Convert byte array to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(service_pictureBytes, 0, service_pictureBytes.length);

                    iv_serviceRequested.setScaleType(ImageView.ScaleType.FIT_CENTER);

                    // Set the height of the ImageView to 200dp
                    int desiredHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight);
                    iv_serviceRequested.setLayoutParams(layoutParams);

                    // Set the bitmap
                    iv_serviceRequested.setImageBitmap(bitmap);
                } else {
                    Log.d("BlobData", "No image attached");
                }

                if(tv_service_type != null && tv_service_type.getText().toString().equals("Emergency")) {
                    ivType.setImageResource(R.drawable.ic_emergency);
                }
                if(booking_status!=null && booking_status.equals("Accepted")) {
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    startOrder.setVisibility(View.VISIBLE);
                    //stopwatchLayout.setVisibility(View.VISIBLE);

                } else if(booking_status!=null && booking_status.equals("Completed")) {
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    startOrder.setVisibility(View.GONE);
                    Class_SingletonVendor.getInstance().setTotal_orders(Class_SingletonVendor.getInstance().getTotal_orders() + 1);
                    new UpdateTotalOrdersTask(Activity_RequestDetails.this, Class_SingletonVendor.getInstance().getId(), Class_SingletonVendor.getInstance().getTotal_orders()).execute();
                } else if(booking_status!=null && booking_status.equals("In-progress")) {
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                    startOrder.setVisibility(View.GONE);
                    llCustomerContact.setVisibility(View.GONE);
                    Intent intent = new Intent(Activity_RequestDetails.this, order_progress.class);
                    intent.putExtra("booking_id",booking_id);
                   // Log.d(, "run: ");
                    intent.putExtra("booking_address",booking_address);
                    startActivity(intent);
                }else{
                    llCustomerContact.setVisibility(View.GONE);
                }
            }
        }, 2000);


        // Set service information to respective TextViews
        tvDateText = findViewById(R.id.tvDateText);
        tvTimeText = findViewById(R.id.tvTimeText);
        tv_price = findViewById(R.id.tvPrice);
        tv_user_name = findViewById(R.id.tvCustomerName);
        tv_customer_number = findViewById(R.id.tvCustomerNumber);
        tv_service_name = findViewById(R.id.tvServiceRequested);
        tv_service_type= findViewById(R.id.tvType);
        tv_status = findViewById(R.id.tvStatus);
        tv_date = findViewById(R.id.tvDate);
        tv_time = findViewById(R.id.tvTime);
        tv_address = findViewById(R.id.tvAddress);
        tv_note = findViewById(R.id.tvNote);
        iv_imageAttached = findViewById(R.id.ivImageAttached);
        iv_serviceRequested = findViewById(R.id.ivServiceRequested);
        ivType = findViewById(R.id.ivType);
        rejectButton = findViewById(R.id.rejectButton);
        acceptButton = findViewById(R.id.acceptButton);
        startOrder = findViewById(R.id.startButton);
        stopwatchLayout = findViewById(R.id.Stopwatch);
        llCustomerContact = findViewById(R.id.llCustomerContact);


        tv_price.setText(getString(R.string.loading_lines));
        tv_user_name.setText(getString(R.string.loading_lines));
        tv_customer_number.setText(getString(R.string.loading_lines));
        tv_service_name.setText(getString(R.string.loading_lines));
        tv_service_type.setText(getString(R.string.loading_lines));
        tv_status.setText(getString(R.string.loading_lines));
        tv_date.setText(getString(R.string.loading_lines));
        tv_time.setText(getString(R.string.loading_lines));
        tv_address.setText(getString(R.string.loading_lines));
        tv_note.setText(getString(R.string.loading_lines));
       // stopwatch = new Stopwatch();
       // stopwatch.setup(this);

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Activity_RequestDetails.this, "Reject Button Clicked", Toast.LENGTH_SHORT).show();
                new RejectServiceTask(Activity_RequestDetails.this).execute(String.valueOf(booking_id));
                finish();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Activity_RequestDetails.this, "Accept Button Clicked", Toast.LENGTH_SHORT).show();
                new AcceptServiceTask(Activity_RequestDetails.this).execute(String.valueOf(booking_id));

                finish();
            }
        } );
        startOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(Activity_RequestDetails.this, "Accept Button Clicked", Toast.LENGTH_SHORT).show();
                new StartServiceTask(Activity_RequestDetails.this).execute(String.valueOf(booking_id));
                finish();

            }
        } );
    }
    private void initializeProductList() {
        // Clear the existing productList to ensure it's empty before adding new items
        productList2.clear();

        // Get the list of product orders from the ProductOrderManager
         //orders = productordersdataholder.getOrderList();

        // Iterate over each order
        for (productorders order : pList) {
            // Use the product ID from the order to find the corresponding product
            Product product = ProductDataHolder.findProductById(order.getProductID());

            if (product != null) {
                // Create a new product instance with the order's quantity
                Product orderedProduct = new Product(product.getId(), product.getName(), product.getDescription(), order.getQuantity(), product.getCategory(), product.getPrice(), product.isAvailability());

                // Add the product with the updated quantity to the productList
                productList2.add(orderedProduct);
                Log.d("ProductLogh", "Product : " + orderedProduct);
                llProduct.setVisibility(View.VISIBLE);
            }
            cartProductAdapter.notifyDataSetChanged();
        }

        // Now productList is initialized with products from the orders
    }

    private void openImageDialog(PhotoView photoView,Bitmap bitmap) {
        // Create a dialog to display the image in a larger view
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_view);

        PhotoView imageView = dialog.findViewById(R.id.dialog_image_view);
        imageView.setImageBitmap(bitmap); // Set the image bitmap

        // Set an OnClickListener to close the dialog when the image is clicked
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void fetchNormalBookingsFromDatabase(int bookingId) {
        String serverUrl = getResources().getString(R.string.server_url);
        String allNormalBookingsApiUrl = serverUrl + "hazirjanab/fetch_bookingproducts.php"; // Replace with your server URL for fetching normal booking data

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, allNormalBookingsApiUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the response as a JSONObject
                            JSONObject responseObject = new JSONObject(response);

                            // Check if the status is success
                            if (responseObject.getInt("Status") == 1) {
                                // Get the normal bookings array from the response
                                JSONArray normalBookingsArray = responseObject.getJSONArray("Bookings");

                                // Clear the existing normal bookings
                                pList.clear();

                                // Process each normal booking in the array
                                for (int i = 0; i < normalBookingsArray.length(); i++) {
                                    JSONObject bookingObject = normalBookingsArray.getJSONObject(i);
                                    int productID = bookingObject.getInt("product_id");
                                    int quantity = bookingObject.getInt("quantity");


                                    // Construct a NormalBooking object (assuming you have a constructor)
                                    productorders productorder = new productorders(productID,quantity);
                                    pList.add(productorder);

                                    // Log the fetched booking
                                    Log.d("NormalBookingLog", "Fetched Normal Booking: " + productorder.getProductID());
                                }
                                //userbookingsdataholder.setOrderList(bookingList);
                                // Notify the adapter that the data set has changed to refresh the RecyclerView
                                if (cartProductAdapter != null) {
                                    cartProductAdapter.notifyDataSetChanged();
                                }
                                initializeProductList();
                            } else {
                                // Handle the failure case here
                                String message = responseObject.getString("Message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(getApplicationContext(), "Failed to fetch normal bookings: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("booking_id", String.valueOf(bookingId));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void onItemClicked(int adapterPosition) {

    }


    private class GetServiceInfoTask extends AsyncTask<String, Void, String> {
        Context context;
        public GetServiceInfoTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/individual_service.php";
            String result = "";
            String serviceId = params[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("service_id", "UTF-8") + "=" + URLEncoder.encode(serviceId, "UTF-8");
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
                Log.d("ServiceInfo Message", message);
                if(message!=null && !message.contains("Successfully")){
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
                if (status == 1) {
                    // If service retrieval is successful, handle the service information here
                    JSONObject serviceJson = jsonResponse.getJSONObject("Service");
                    String serviceName = serviceJson.getString("name");

                    // Set service name and hourly rate to respective TextViews
                    service_name = serviceJson.getString("name");
                    service_hourly_rate = "PKR "+serviceJson.getString("hourlyrate")+ "/hr";
                    service_description = serviceJson.getString("description");
                    service_city = serviceJson.getString("city");
                    service_category = serviceJson.getString("category");
                    Log.d("service_category", service_category);
                    service_type = serviceJson.getString("type");

                    String base64 = serviceJson.getString("img");
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
                    Bitmap bitmap = null;

                    try {
                        // Get the length of the Blob
                        long length = blob.length();

                        if (length > 0) {
                            // The Blob contains valid data
                            Log.d("BlobData", "Blob length: " + length);
                            // Read bytes from the Blob using an input stream
                            InputStream inputStream = blob.getBinaryStream();
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            service_pictureBytes = outputStream.toByteArray();
                            inputStream.close();
                            outputStream.close();

                            // Check the length of the byte array
                            Log.d("BlobData", "Byte array length: " + service_pictureBytes.length);
//                          bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
//                          Log.d("BlobData", "Bitmap: " + bitmap.toString());
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

                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchProductsFromDatabase(int booking_id) {
        String serverUrl = getResources().getString(R.string.server_url);
        String allServicesApiUrl = serverUrl + "hazirjanab/product.php";
        String url = allServicesApiUrl; // Replace with your server URL for fetching item data

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the response as a JSONObject
                            JSONObject responseObject = new JSONObject(response);

                            // Check if the status is success
                            if (responseObject.getInt("Status") == 1) {
                                // Get the items array from the response
                                JSONArray itemsArray = responseObject.getJSONArray("Products");

                                // Process each item in the array
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject itemObject = itemsArray.getJSONObject(i);
                                    int id = itemObject.getInt("id");
                                    String name = itemObject.getString("name");
                                    String description = itemObject.getString("description");
                                    int quantity = itemObject.getInt("quantity");
                                    String category = itemObject.getString("category");
                                    int price = itemObject.getInt("price");
                                    int availabilityInt = itemObject.getInt("availability");
                                    boolean availability = availabilityInt == 1;

                                    Log.d("ProductCheck", "Product : " + id + " " + name + " " + description + " " + quantity + " " + category + " " + price + " " + availability);

                                    // Assuming you have a way to convert your image data to a URL or file path
                                    // String imageUrl = /* Your logic to fetch or construct the image URL */;

                                    // Construct an Product object
                                    Product item = new Product(id, name, description, quantity, category, price, availability);
                                    Log.d("ProductLog", "Product : " + item);

                                    // Add the item to your list
                                    productList.add(item);
                                }
                                ProductDataHolder.setProductList(productList);
                                Log.d("bookingid check", String.valueOf(booking_id));
                                fetchNormalBookingsFromDatabase(booking_id);

                                // Notify the adapter that the data set has changed to refresh the RecyclerView
                                //  adapter.notifyDataSetChanged();
                                //  filterList("", serviceType, serviceCategory);
                            } else {
                                // Handle the failure case here
                                String message = responseObject.getString("Message");
                                //  Toast.makeText(SearchMarket.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Activity_RequestDetails.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error
                Toast.makeText(Activity_RequestDetails.this, "Failed to fetch items: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }

    private class GetUserInfoTask extends AsyncTask<String, Void, String> {
        Context context;
        public GetUserInfoTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/get_user.php";
            String result = "";
            String userId = params[0];
            Log.d("UserID", userId);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                Log.d("UserID", userId);
                Log.d("httpURLConnection", httpURLConnection.toString());

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
                Log.d("GetUserInfoTask Message", message);
                if(message!=null && !message.contains("Successfully")){
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                if (status == 1) {
                    JSONObject userJson = jsonResponse.getJSONObject("User");
                    //User information
                    user_first_name = userJson.getString("First_name");
                    Log.d("UserFirstName", user_first_name);
                    user_last_name = userJson.getString("Last_name");
                    Log.d("UserLastName", user_last_name);
                    user_profile_pic = userJson.getString("Profile_pic");
                    customer_number = userJson.getString("phone_number");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class RejectServiceTask extends AsyncTask<String, Void, String> {
        Context context;
        public RejectServiceTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/reject_booking.php";
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
                Log.d("RejectServiceTask Message", message);
                if(message!=null && !message.contains("Booking status updated to 'rejected'")){
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }

                if (status == 1) {
                    Log.d("BookingRejected", "Booking Rejected");
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

    private class AcceptServiceTask extends AsyncTask<String, Void, String> {
        Context context;
        public AcceptServiceTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/accept_booking.php";
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

    private class StartServiceTask extends AsyncTask<String, Void, String> {
        Context context;
        public StartServiceTask(Context context) {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/start_booking.php";
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
                    Intent intent = new Intent(context, order_progress.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class UpdateTotalOrdersTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private int id;
        private int totalOrders;

        public UpdateTotalOrdersTask(Context context, int id, int totalOrders) {
            this.context = context;
            this.id = id;
            this.totalOrders = totalOrders;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "path_to_your_api"; // Adjust path as needed
            String result = "";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject dataJson = new JSONObject();
                dataJson.put("id", id);
                dataJson.put("total_orders", totalOrders);

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
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");

                if (status == 1) {
                    // Update successful
                    Toast.makeText(context, "Total orders updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Update failed
                    String message = jsonResponse.optString("Message", "Update failed");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
        }
    }


}