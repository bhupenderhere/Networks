package com.example.networks.Admin.Activities.Notice.Upload;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class admin_activity_upload_notice extends AppCompatActivity {
    private Bitmap bitmap;
    private ImageView notice;
    TextInputLayout noticeTitleLayout;
    TextInputEditText noticeTitle;
    ProgressDialog progressDialog;

    public void onWindowFocusChanged(boolean hasFocus) {
        /*
         * Focus on EditText on activity start
         * */
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            findViewById(R.id.noticeTitleLayout).requestFocus();
    }

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
        setContentView(R.layout.admin_activity_upload_notice);

        // Sets The ActionBar Style
        Utility.activityActionBar("Upload Notice", Objects.requireNonNull(getSupportActionBar()), this);

        // Get The Input Field
        noticeTitleLayout = findViewById(R.id.noticeTitleLayout);
        noticeTitle = findViewById(R.id.noticeTitle);

        // Attach Auto Keyboard Open On Focus
        Utility.setAutoOpenKeyboardOnFocus(noticeTitle);

        // Get The Notice Preview Image
        notice = findViewById(R.id.notice);

        // Select Image From Gallery Button
        findViewById(R.id.selectNotice).setOnClickListener(view -> openGallery
                .launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        // Upload Image To The Database
        findViewById(R.id.uploadNotice).setOnClickListener(view -> {
            noticeTitleLayout.setErrorEnabled(false);
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            if (Objects.requireNonNull(noticeTitle.getText()).toString().isEmpty()) {
                noticeTitleLayout.setError("Field Required");
                noticeTitle.requestFocus();
            } else if (bitmap == null) {
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
        map.put("title", Utility.toTitleCase(Objects.requireNonNull(noticeTitle.getText()).toString()));
        map.put("time", new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date()));
        map.put("date", new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));

        FirebaseFirestore.getInstance().collection("notice").document(uuid).set(map)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Notice Uploaded!", this);
                    startActivity(new Intent(this, admin_activity_upload_notice.class));
                    this.finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Something Went Wrong!", this);
                });
    }

    private void uploadImage() {
        progressDialog.setMessage("Notice Uploading...");
        progressDialog.show();

        // Byte Stream to compress Images
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);
        byte[] finalImage = byteStream.toByteArray();

        // Uuid
        String uuid = UUID.randomUUID().toString();

        // Get A Path Link From Firebase Storage
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("notice").child(uuid + ".jpg");
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
                    notice.setImageBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    });
}