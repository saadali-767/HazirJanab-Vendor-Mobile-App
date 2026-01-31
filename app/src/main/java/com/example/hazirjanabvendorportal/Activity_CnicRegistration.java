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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class Activity_CnicRegistration extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_GALLERY_PHOTO = 200;
    ImageView ivCnicFront, ivCnicBack;
    TextView tvCnicFront, tvCnicBack;
    boolean isFrontImageUploaded = false;
    boolean isBackImageUploaded = false;
    private ImageView lastClickedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnic_registration);

        ivCnicFront = findViewById(R.id.ivCnicFront);
        ivCnicBack = findViewById(R.id.ivCnicBack);
        tvCnicFront = findViewById(R.id.tvCnicFront);
        tvCnicBack = findViewById(R.id.tvCnicBack);


        Button btnNext = findViewById(R.id.BtnNext);

        ivCnicFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastClickedImageView = ivCnicFront;
                showImagePickerOptions();
            }
        });

        ivCnicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastClickedImageView = ivCnicBack;
                showImagePickerOptions();
            }
        });

        btnNext.setOnClickListener(view -> {
            if (isFrontImageUploaded && isBackImageUploaded) {
                showErrorMessage("Please upload both front and back CNIC images");
                return;
            }

            // Get front and back CNIC images as Blob
            Blob blobCnicFront = getBlobFromImageView(ivCnicFront);
            Blob blobCnicBack = getBlobFromImageView(ivCnicBack);
            Class_SingletonVendor.getInstance().setCnic_front(blobCnicFront);
            Class_SingletonVendor.getInstance().setCnic_back(blobCnicBack);

            // Proceed with further actions
            Intent intent = new Intent(Activity_CnicRegistration.this, Activity_PendingApproval.class);
            startActivity(intent);
            finish();
        });
    }

    private void showImagePickerOptions() {
        // Options: "Take Photo", "Choose from Gallery", "Cancel"
        CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                takePhoto();
            } else if (options[item].equals("Choose from Gallery")) {
                chooseFromGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void chooseFromGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Set the image to the appropriate ImageView based on the last clicked option
                if (lastClickedImageView == ivCnicFront) {
                    Glide.with(this).load(imageBitmap).into(ivCnicFront);
                    tvCnicFront.setVisibility(View.GONE);
                    isFrontImageUploaded = true;
                } else if (lastClickedImageView == ivCnicBack) {
                    Glide.with(this).load(imageBitmap).into(ivCnicBack);
                    tvCnicBack.setVisibility(View.GONE);
                    isFrontImageUploaded = true;
                }
            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri imageUri = data.getData();
                // Set the image to the appropriate ImageView based on the last clicked option
                if (lastClickedImageView == ivCnicFront) {
                    Glide.with(this).load(imageUri).into(ivCnicFront);
                    tvCnicFront.setVisibility(View.GONE);
                    isBackImageUploaded = true;
                } else if (lastClickedImageView == ivCnicBack) {
                    Glide.with(this).load(imageUri).into(ivCnicBack);
                    tvCnicBack.setVisibility(View.GONE);
                    isBackImageUploaded = true;
                }
            }
        }
    }

    private Blob getBlobFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Blob blob = new Blob() {
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
                    return blob;
                } else {
                    showErrorMessage("Error converting image to Blob");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("Error converting image to Blob");
            }
        }
        return null;
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_CnicRegistration.this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
