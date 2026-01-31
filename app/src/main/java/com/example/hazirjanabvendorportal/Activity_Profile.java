package com.example.hazirjanabvendorportal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class Activity_Profile extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhoneNumber, tvUsername, tvPassword, tvAddress, tvDisplayName, tvCnic, tvTotalRating, tvTotalOrders, tvService;
    ImageView ivProfilePicture, ivServiceMan;
    LinearLayout llEditUserInformation, llEditAccountCredentials, llEditAddress;
    RatingBar rbTotalRating;
    Toolbar toolbar;
    boolean asyncTaskStatus = false;
    AlertDialog dialog;
    Context context = this;
    TextInputEditText etInputField0_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = Toolbar.getInstance();
        toolbar.setup(this);

        llEditUserInformation = findViewById(R.id.llEditUserInformation);
        llEditAccountCredentials = findViewById(R.id.llEditAccountCredentials);
        llEditAddress = findViewById(R.id.llEditAddress);

        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvPassword = findViewById(R.id.tvPassword);
        tvAddress = findViewById(R.id.tvAddress);
        tvCnic = findViewById(R.id.tvCnic);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvTotalRating = findViewById(R.id.tvTotalRating);
        rbTotalRating = findViewById(R.id.rbTotalRating);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        tvService = findViewById(R.id.tvService);
        ivServiceMan = findViewById(R.id.ivServiceMan);


        //Set
        String firstName = Class_SingletonVendor.getInstance().getFirst_name();
        String capitalizedFirstName = Character.toUpperCase(firstName.charAt(0)) + firstName.substring(1);
        String lastName = Class_SingletonVendor.getInstance().getLast_name();
        String capitalizedLastName = Character.toUpperCase(lastName.charAt(0)) + lastName.substring(1);

        tvDisplayName.setText(capitalizedFirstName + "!");
        tvName.setText(capitalizedFirstName + " " + capitalizedLastName);
        tvEmail.setText(Class_SingletonVendor.getInstance().getEmail());
        tvPhoneNumber.setText(Class_SingletonVendor.getInstance().getPhone_number());
        tvPassword.setText(Class_SingletonVendor.getInstance().getPassword());
        tvAddress.setText(Class_SingletonVendor.getInstance().getAddress());
        tvCnic.setText(Class_SingletonVendor.getInstance().getCnic());
        tvTotalRating.setText(Class_SingletonVendor.getInstance().getTotal_rating()+"|5");
        rbTotalRating.setRating(Class_SingletonVendor.getInstance().getTotal_rating());
        tvTotalOrders.setText("(" + Class_SingletonVendor.getInstance().getTotal_orders() +")");

        String servicePreText = "";
        String service="";
        switch(Class_SingletonVendor.getInstance().getCategory()){
            case "Carpenter":
                servicePreText ="You have been inducted as a carpenter, and, you offer services related to";
                service = "carpentry work";
                ivServiceMan.setImageResource(R.drawable.ic_carpenter);
                break;
            case "Electrician":
                servicePreText ="You have been inducted as an electrician, and, you offer services related to";
                service = "electrical work";
                ivServiceMan.setImageResource(R.drawable.ic_electrician);
                break;
            case "Plumber":
                servicePreText ="You have been inducted as a plumber, and, you offer services related to";
                service = "plumbing work";
                ivServiceMan.setImageResource(R.drawable.ic_plumber);
                break;
            case "Mechanic":
                servicePreText ="You have been inducted as a mechanic, and, you offer services related to";
                service = "mechanical work";
                ivServiceMan.setImageResource(R.drawable.ic_mechanic);
                break;

        }

        tvService.setText( servicePreText + " " + service);

        byte[] iv_VendorPicture=null;
        Blob blob = Class_SingletonVendor.getInstance().getVendor_picture();
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
                iv_VendorPicture = outputStream.toByteArray();
                inputStream.close();
                outputStream.close();

                // Check the length of the byte array
                Log.d("BlobData", "Byte array length: " + iv_VendorPicture.length);
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
        if (iv_VendorPicture != null && iv_VendorPicture.length > 0) {
            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(iv_VendorPicture, 0, iv_VendorPicture.length);

           ivProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Set the height of the ImageView to 200dp
            //int desiredHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, desiredHeight);
            //ivProfilePicture.setLayoutParams(layoutParams);

            // Set the bitmap
            ivProfilePicture.setImageBitmap(bitmap);
        } else {
            Log.d("BlobData", "No image attached");
        }



        llEditUserInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditPopup("userInformation");
            }
        });

        llEditAccountCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditPopup("accountCredentials");
            }
        });

        llEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditPopup("address");
            }
        });
    }

    private void showEditPopup(String type) {
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.template_user_profile_popup, null);

        TextView tvPopupTitle = popupView.findViewById(R.id.tvPopupTitle);
        TextInputEditText etInputField0 = popupView.findViewById(R.id.etInputField0);
        TextInputEditText etInputField1 = popupView.findViewById(R.id.etInputField1);
        TextInputEditText etInputField2 = popupView.findViewById(R.id.etInputField2);
        TextInputEditText etInputField3 = popupView.findViewById(R.id.etInputField3);
        LinearLayout llInputField0 = popupView.findViewById(R.id.llInputField0);
        LinearLayout llInputField1 = popupView.findViewById(R.id.llInputField1);
        LinearLayout llInputField2 = popupView.findViewById(R.id.llInputField2);
        LinearLayout llInputField3 = popupView.findViewById(R.id.llInputField3);
        TextView tvInputField0 = popupView.findViewById(R.id.tvInputField0);
        TextView tvInputField1 = popupView.findViewById(R.id.tvInputField1);
        TextView tvInputField2 = popupView.findViewById(R.id.tvInputField2);
        TextView tvInputField3 = popupView.findViewById(R.id.tvInputField3);
        Button btnSaveBtn = popupView.findViewById(R.id.btnSave);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        dialog = builder.create();

        // Set fields based on the type
        if (type.equals("userInformation")) {
            tvPopupTitle.setText("Edit User Information");

            tvInputField0.setText("First Name");
            etInputField0.setHint(Class_SingletonVendor.getInstance().getFirst_name());
            etInputField0.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etInputField0.setMaxLines(1);

            tvInputField1.setText("Last Name");
            etInputField1.setHint(Class_SingletonVendor.getInstance().getLast_name());
            etInputField1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etInputField1.setMaxLines(1);

            tvInputField2.setText("Phone Number");
            etInputField2.setHint(Class_SingletonVendor.getInstance().getPhone_number());
            etInputField2.setInputType(InputType.TYPE_CLASS_PHONE);
            etInputField2.setMaxLines(1);

            tvInputField3.setText("CNIC");
            etInputField3.setHint(Class_SingletonVendor.getInstance().getCnic());
            etInputField3.setInputType(InputType.TYPE_CLASS_PHONE);
            etInputField3.setMaxLines(1);
            etInputField3.setFocusable(false);
            etInputField3.setFocusableInTouchMode(false);
            etInputField3.setClickable(false);
            etInputField3.setLongClickable(false);
            etInputField3.setKeyListener(null);

            btnSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isAtLeastOneFieldFilled = !etInputField0.getText().toString().isEmpty() ||
                            !etInputField1.getText().toString().isEmpty() ||
                            !etInputField2.getText().toString().isEmpty() ||
                            !etInputField3.getText().toString().isEmpty(); // No need to check etInputField3 as it's not editable

                    if (isAtLeastOneFieldFilled) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                        builder2.setTitle("Confirm Changes");
                        builder2.setMessage("Are you sure you want to save these changes?");
                        builder2.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Update the fields that are filled
                                if (!etInputField0.getText().toString().isEmpty()) {
                                    tvName.setText(etInputField0.getText().toString() + " " + Class_SingletonVendor.getInstance().getLast_name()); // Assuming you want a space between first and last name
                                    Class_SingletonVendor.getInstance().setFirst_name(etInputField0.getText().toString());
                                    tvDisplayName.setText(etInputField0.getText().toString() + "!");
                                }
                                if (!etInputField1.getText().toString().isEmpty()) {
                                    tvName.setText(Class_SingletonVendor.getInstance().getFirst_name() + " " + etInputField1.getText().toString()); // Assuming you want a space between first and last name
                                    Class_SingletonVendor.getInstance().setLast_name(etInputField1.getText().toString());
                                    // If you need to do something with last name, do it here. Currently, it's only concatenated with the first name.
                                }
                                if (!etInputField2.getText().toString().isEmpty()) {
                                    tvPhoneNumber.setText(etInputField2.getText().toString());
                                    Class_SingletonVendor.getInstance().setPhone_number(etInputField2.getText().toString());
                                }
                                Toast.makeText(Activity_Profile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                new UpdateVendorInfo(context).execute();
                                dialogInterface.dismiss(); // This closes popup2
                                dialog.dismiss(); // This closes popup1
                            }
                        });
                        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled, just close popup2
                                dialogInterface.dismiss();
                                // popup1 will remain open
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                    } else {
                        Toast.makeText(Activity_Profile.this, "Please fill at least one field", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Load existing user info into fields
        } else if (type.equals("accountCredentials")) {
            tvPopupTitle.setText("Edit Account Credentials");

            tvInputField0.setText("Email");
            etInputField0.setHint(Class_SingletonVendor.getInstance().getEmail());
            etInputField0.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            etInputField0.setMaxLines(1);

            tvInputField1.setText("Password");
            etInputField1.setHint(Class_SingletonVendor.getInstance().getPassword());
            etInputField1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etInputField1.setMaxLines(1);

            tvInputField2.setText("Confirm Password");
            etInputField2.setHint(Class_SingletonVendor.getInstance().getPassword());
            etInputField2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etInputField2.setMaxLines(1);

            llInputField3.setVisibility(View.GONE);
//            etInputField2.setVisibility(View.GONE);
//            etInputField3.setVisibility(View.GONE);


            etInputField0.addTextChangedListener(new TextWatcher() {
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
                        etInputField0.setError("Invalid Email");
                    }
                }
            });

            etInputField1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!isPasswordValid(charSequence.toString())) {
                        TextInputLayout tilPassword = popupView.findViewById(R.id.tilInputField1);
                        tilPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        etInputField1.setError("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                    }else{
                        TextInputLayout tilPassword = popupView.findViewById(R.id.tilInputField1);
                        tilPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable charSequence) {

                }
            });

            etInputField2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!isPasswordValid(charSequence.toString())) {
                        TextInputLayout tilConfirmPassword = popupView.findViewById(R.id.tilInputField2);
                        tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_NONE);
                        etInputField2.setError("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
                    }else{
                        TextInputLayout tilConfirmPassword = popupView.findViewById(R.id.tilInputField2);
                        tilConfirmPassword.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            btnSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isAtLeastOneFieldFilled = !etInputField0.getText().toString().isEmpty() ||
                            (!etInputField1.getText().toString().isEmpty() && !etInputField2.getText().toString().isEmpty());

                    if (isAtLeastOneFieldFilled) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                        builder2.setTitle("Confirm Changes");
                        builder2.setMessage("Are you sure you want to save these changes?");
                        builder2.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!etInputField0.getText().toString().isEmpty()) {
                                    new CheckEmailUniqueTask(Activity_Profile.this).execute(etInputField0.getText().toString());
                                    dialogInterface.dismiss(); // This closes popup2
                                    etInputField0_temp = etInputField0;
                                }
                                if (!etInputField1.getText().toString().isEmpty() && !etInputField2.getText().toString().isEmpty()) {
                                    if(etInputField1.getText().toString().equals(etInputField2.getText().toString())){
                                        tvPassword.setText(etInputField1.getText().toString());
                                        Class_SingletonVendor.getInstance().setPassword(etInputField1.getText().toString());
                                        Toast.makeText(Activity_Profile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                        new UpdateVendorInfo(context).execute();
                                        dialogInterface.dismiss(); // This closes popup2
                                        dialog.dismiss(); // This closes popup1
                                    }else {

                                        AlertDialog.Builder builder3 = new AlertDialog.Builder(view.getContext());
                                        builder3.setTitle("Error");
                                        builder3.setMessage("Passwords do not match");
                                        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        AlertDialog dialog3 = builder3.create();
                                        dialog3.show();
                                    }

                                }

                            }
                        });
                        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled, just close popup2
                                dialogInterface.dismiss();
                                // popup1 will remain open
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                    } else {
                        Toast.makeText(Activity_Profile.this, "Please fill at least one field", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // Load existing account credentials
        } else if (type.equals("address")) {
            tvPopupTitle.setText("Edit Address");

            tvInputField0.setText("Address");
            etInputField0.setHint(Class_SingletonVendor.getInstance().getAddress());
            etInputField0.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            etInputField0.setMaxLines(3);

            llInputField1.setVisibility(View.GONE);
            llInputField2.setVisibility(View.GONE);
            llInputField3.setVisibility(View.GONE);

            btnSaveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Address", "Address: " + etInputField0.getText().toString());
                    boolean isAtLeastOneFieldFilled = !etInputField0.getText().toString().isEmpty();

                    if (isAtLeastOneFieldFilled) {
                        // Update the fields that are filled
                        Log.d("Addresshee", "Address: " + etInputField0.getText().toString());
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                        builder2.setTitle("Confirm Changes");
                        builder2.setMessage("Are you sure you want to save these changes?");
                        builder2.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!etInputField0.getText().toString().isEmpty()) {
                                    if (!etInputField0.getText().toString().isEmpty()) {
                                        tvAddress.setText(etInputField0.getText().toString());
                                        Class_SingletonVendor.getInstance().setAddress(etInputField0.getText().toString());
                                    }
                                    Toast.makeText(Activity_Profile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                    new UpdateVendorInfo(context).execute();
                                }
                                dialogInterface.dismiss(); // This closes popup2
                                dialog.dismiss(); // This closes popup1
                            }
                        });
                        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User cancelled, just close popup2
                                dialogInterface.dismiss();
                                // popup1 will remain open
                            }
                        });
                        AlertDialog dialog2 = builder2.create();
                        dialog2.show();
                    } else {
                        Toast.makeText(Activity_Profile.this, "Please fill at least one field", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        dialog.show();
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

    private class CheckEmailUniqueTask extends AsyncTask<String, Void, String> {
        Context context;
        public CheckEmailUniqueTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/check_email_unique.php"; // Change to the actual path of your API
            String result = "";
            String email = params[0];

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                String postParams = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
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
            Log.d("CheckEmailUnique", "Result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonResponse = new JSONObject(result);
                int status = jsonResponse.getInt("Status");
                String message = jsonResponse.getString("Message");
                String email = jsonResponse.getString("Email");

                // Additional actions based on the status
                if (status == 1) {
                    // Email is unique, you can proceed with further actions
                    Log.d("CheckEmailUnique", "Email is unique.");
                    tvEmail.setText(email);
                    Class_SingletonVendor.getInstance().setEmail(email);
                    Toast.makeText(Activity_Profile.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    new UpdateVendorInfo(context).execute();
                } else {
                    // Email already exists, take necessary actions
                    Log.d("CheckEmailUnique", "Email already exists.");
                    etInputField0_temp.setError("Email Aready Exists!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_LONG).show();
            }
            asyncTaskStatus = true;
        }
    }
    private class UpdateVendorInfo extends AsyncTask<Void, Void, String> {
        private Context context;
        private Class_SingletonVendor singletonVendor;

        public UpdateVendorInfo(Context context) {
            this.context = context;
            this.singletonVendor = Class_SingletonVendor.getInstance();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String serverUrl = context.getResources().getString(R.string.server_url);
            String apiUrl = serverUrl + "hazirjanab/update_vendor.php";  // Adjust path as needed
            String result = "";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject vendorJson = new JSONObject();
                vendorJson.put("id", singletonVendor.getId());
                vendorJson.put("first_name", singletonVendor.getFirst_name());
                vendorJson.put("last_name", singletonVendor.getLast_name());
                vendorJson.put("email", singletonVendor.getEmail());
                vendorJson.put("phone_number", singletonVendor.getPhone_number());
                vendorJson.put("password", singletonVendor.getPassword());
                vendorJson.put("address", singletonVendor.getAddress());

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
                    Toast.makeText(context, "Vendor information updated successfully", Toast.LENGTH_SHORT).show();
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