package com.example.hazirjanabvendorportal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

public class Adapter_ServiceBookings extends RecyclerView.Adapter<Adapter_ServiceBookings.ViewHolder> {
    private static Context context;
    private List<Class_ServiceBooking> requestsList;
    RecyclerViewInterface recyclerViewInterface;

    public Adapter_ServiceBookings(Context context, List<Class_ServiceBooking> reqList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.requestsList = reqList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.template_requests_row,parent, false);
        return new ViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Class_ServiceBooking class_serviceBooking = requestsList.get(position);
        holder.tvCustomerName.setText("lalal"); //Get from customer id, id ooper hai usse fetch karana hoga customer id
        Log.d("Time", class_serviceBooking.getTime());
        if (class_serviceBooking.getType().equals("Emergency")) {
            Log.d("Emergency", "Emergency");
            holder.tvTimePosted.setVisibility(View.GONE);
            holder.ivServiceType.setVisibility(View.VISIBLE); // Show the image
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvCustomerName.getLayoutParams();
            // Increase the weight
            params.weight = (float) (5);
            Log.d("Weight Here", String.valueOf(params.weight));
        } else {
            Log.d("Emergency", "Not Emergency");
            holder.tvTimePosted.setVisibility(View.VISIBLE); // Show the time text
            holder.ivServiceType.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivServiceType.getLayoutParams();
            // Increase the weight
            params.weight = (float) (1);
            LinearLayout.LayoutParams params_tvCustomerName = (LinearLayout.LayoutParams) holder.tvCustomerName.getLayoutParams();
            // Increase the weight
            Log.d("Weight", String.valueOf(params_tvCustomerName.weight));
            params_tvCustomerName.weight = (float) (4.4);
        }


        holder.tvTimePosted.setText(String.valueOf(class_serviceBooking.getTime())); //isko dekh lete
        holder.tvStatus.setText(class_serviceBooking.getStatus());
        holder.tvType.setText(class_serviceBooking.getType()); //service id se nikalana hoga
        holder.tvAddress.setText(class_serviceBooking.getAddress()); //booking se nikalna hoga

        //Log.d class_serviceBooking.getService_id()
//        Log.d("ServiceID", String.valueOf(class_serviceBooking.getService_id()));
//        holder.tvServiceRequested.setText(String.valueOf(class_serviceBooking.getService_id()));
        holder.bind(String.valueOf(class_serviceBooking.getService_id()), String.valueOf(class_serviceBooking.getUser_id()), String.valueOf(class_serviceBooking.getId()));



//        if (class_serviceBooking.getTime() != null) {
//            holder.tvTimePosted.setText(String.valueOf(class_serviceBooking.getTime()));
//        } else {
//            holder.tvTimePosted.setText(""); // or some placeholder like "N/A"
//        }
//
//        if (class_serviceBooking.getDate() != null) {
//            holder.tvDate.setText(String.valueOf(class_serviceBooking.getDate()));
//        } else {
//            holder.tvDate.setText("");
//        }
//
//        if (class_serviceBooking.getTime() != null) {
//            holder.tvTime.setText(String.valueOf(class_serviceBooking.getTime()));
//        } else {
//            holder.tvTime.setText("");
//        }
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName;
        TextView tvTimePosted;
        TextView tvServiceRequested;
        TextView tvStatus;
        TextView tvType;
        TextView tvAddress;
        TextView tvPrice;
        ImageView ivServiceType;

        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvTimePosted = itemView.findViewById(R.id.tvTimePosted);
            tvServiceRequested = itemView.findViewById(R.id.tvServiceRequested);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvType = itemView.findViewById(R.id.tvType);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivServiceType= itemView.findViewById(R.id.ivServiceType);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewInterface.onItemClicked(getAdapterPosition());
                }
            });
        }

        public void bind(String serviceId, String userId, String bookingID) {
            Log.d("ServiceID", serviceId);
            new GetServiceInfoTask().execute(serviceId);
            new GetUserInfoTask().execute(userId);
            new GetBookingInfoTask().execute(bookingID);
        }

        @SuppressLint("StaticFieldLeak")
        private class GetServiceInfoTask extends AsyncTask<String, Void, String> {
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
                    Log.d("AcceptServiceTask Message", message);
                    if(message!=null && !message.contains("Successfully")){
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    if (status == 1) {
                        // If service retrieval is successful, handle the service information here
                        JSONObject serviceJson = jsonResponse.getJSONObject("Service");
                        String serviceName = serviceJson.getString("name");

                        // Set service name and hourly rate to respective TextViews
                        tvServiceRequested.setText(serviceName);
                        tvPrice.setText("PKR "+serviceJson.getString("hourlyrate")+" / Hour");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                }
            }
        }

        private class GetUserInfoTask extends AsyncTask<String, Void, String> {
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
                    Log.d("GetUserInfo Task Message", message);
                    if(message!=null && !message.contains("Successfully")){
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }

                    if (status == 1) {
                        JSONObject userJson = jsonResponse.getJSONObject("User");
                        String userFirstName = userJson.getString("First_name");
                        String userLastName = userJson.getString("Last_name");

                        // Set service name and hourly rate to respective TextViews
                        tvCustomerName.setText(userFirstName + " " + userLastName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                }
            }
        }

        private class GetBookingInfoTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String serverUrl = context.getResources().getString(R.string.server_url);
                String apiUrl = serverUrl + "hazirjanab/get_booking.php";
                String result = "";
                String bookingId = params[0];
                Log.d("BookingID", bookingId);

                try {
                    URL url = new URL(apiUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    Log.d("BookingID", bookingId);
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
                    Log.d("GetBookingInfo Task Message", message);
                    if(message!=null && !message.contains("Successfully")){
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }


                    if (status == 1) {
                        JSONObject bookingJson = jsonResponse.getJSONObject("Booking");
                        //Booking availability
                        String bookingAddress = bookingJson.getString("address")+ ", " + bookingJson.getString("city")+".";
                        String bookingStatus = bookingJson.getString("status");
                        String bookingType = bookingJson.getString("type");

                        tvAddress.setText(bookingAddress);
                        tvStatus.setText(bookingStatus);
                        tvType.setText(bookingType);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
