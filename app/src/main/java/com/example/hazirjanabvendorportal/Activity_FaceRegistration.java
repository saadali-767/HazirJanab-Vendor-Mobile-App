package com.example.hazirjanabvendorportal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;


public class Activity_FaceRegistration extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_GALLERY_PHOTO = 200;
    ImageView imageView;
    boolean isImageUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_registration);

        imageView = findViewById(R.id.ivImageViewSelfie);
        Button btnNext = findViewById(R.id.BtnNext);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickerOptions();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isImageUploaded){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Activity_FaceRegistration.this);
                    builder.setTitle("Error");
                    builder.setMessage("Please upload your selfie");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                    return;
                }else{
                    Drawable drawable = imageView.getDrawable();
                    Blob blob = null;

                    if (drawable instanceof BitmapDrawable) {
                        Log.d("Drawable", "Drawable is a BitmapDrawable");
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        Bitmap bitmap = bitmapDrawable.getBitmap();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        // Create Blob
                        blob = new Blob() {
                            @Override
                            public long length() throws SQLException {
                                return byteArray.length;
                            }

                            @Override
                            public byte[] getBytes(long pos, int length) throws SQLException {
                                // Implement as needed
                                return new byte[0];
                            }

                            @Override
                            public InputStream getBinaryStream() throws SQLException {
                                return new ByteArrayInputStream(byteArray);
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
                            public int setBytes(long l, byte[] bytes, int i, int i1) throws SQLException {
                                return 0;
                            }

                            @Override
                            public OutputStream setBinaryStream(long l) throws SQLException {
                                return null;
                            }

                            @Override
                            public void truncate(long l) throws SQLException {

                            }

                            @Override
                            public void free() throws SQLException {

                            }

                            @Override
                            public InputStream getBinaryStream(long l, long l1) throws SQLException {
                                return null;
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
                    } else {
                        // Handle if the drawable is not a BitmapDrawable
                        Log.e("Error", "Drawable is not a BitmapDrawable");
                    }
                    Class_SingletonVendor.getInstance().setVendor_picture(blob);
                    Log.d("BlobData", "Blob: " + blob);
                }
                Intent intent = new Intent(Activity_FaceRegistration.this, Activity_CnicRegistration.class);
                startActivity(intent);
            }
        });

    }

    private void showImagePickerOptions() {
        // Options: "Take Photo", "Choose from Gallery", "Cancel"
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    isImageUploaded = true;
                }
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_PHOTO);
                isImageUploaded = true;
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Glide.with(this).load(imageBitmap).into(imageView);
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Glide.with(this).load(imageUri).into(imageView);
                }
            }
        }
    }




}