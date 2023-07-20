package com.example.networks.Admin.Activities.Image.Upload;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.networks.R;
import com.example.networks.Utility;

import java.util.HashMap;
import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class admin_activity_upload_image extends AppCompatActivity {
    Button uploadImage;
    ImageView imagePreview;
    private Bitmap bitmap;
    String category;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        startActivity(new Intent(this, com.example.networks.Admin.admin_home.class));
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Set a Back Button in the Action Bar & Perform Same Function
         * As onBackPressed() on click
         * */
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_upload_image);

        // Sets The ActionBar Style
        Utility.activityActionBar("Upload Image", Objects.requireNonNull(getSupportActionBar()), this);

        // References
        uploadImage = findViewById(R.id.uploadImage);
        imagePreview = findViewById(R.id.image);

        // Select Image From Gallery Button
        findViewById(R.id.selectImage).setOnClickListener(view -> openGallery
                .launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        //Spinner to choose Image Category
        Spinner spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"None", "Workshops", "Other Events"};
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Image Upload Button
        findViewById(R.id.uploadImage).setOnClickListener(view -> {
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            if (category.equals("None"))
                Utility.makeToast("Please Select Image Category!", this);
            else if (bitmap == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutHolder), "Choose An Image", Snackbar.LENGTH_LONG)
                        .setTextColor(Color.WHITE)
                        .setActionTextColor(ContextCompat.getColor(this, R.color.green))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.dimBlack))
                        .setAction("Select Image", view1 -> openGallery.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

                View v = snackbar.getView();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                params.gravity = Gravity.TOP;
                v.setLayoutParams(params);
                snackbar.show();
            } else
                uploadImage();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void uploadData(String downloadURL, String uuid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", uuid);
        map.put("url", downloadURL);
        map.put("category", category);

        FirebaseFirestore.getInstance().collection("gallery").document(uuid).set(map)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Image Uploaded!", this);
                    startActivity(new Intent(this, admin_activity_upload_image.class));
                    this.finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Something Went Wrong!", this);
                });
    }

    private void uploadImage() {
        progressDialog.setMessage("Image Uploading...");
        progressDialog.show();

        // Byte Stream to compress Images
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);
        byte[] finalImage = byteStream.toByteArray();

        // Uuid
        String uuid = UUID.randomUUID().toString();

        // Get A Path Link From Firebase Storage
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("gallery").child(uuid + ".jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);

        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful())
                filePath.getDownloadUrl().addOnSuccessListener(uri -> uploadData(String.valueOf(uri), uuid)); // Get A Download Url
            else {
                progressDialog.dismiss();
                Utility.makeToast("Something Went Wrong!", this);
            }
        });
    }

    ActivityResultLauncher<Intent> openGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null)
                try {
                    bitmap = Utility.rotateImageIfRequired(getApplicationContext(),
                            MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), result.getData().getData()),
                            result.getData().getData());
                    imagePreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    });
}