package com.example.networks.Admin.Activities.Ebook.Upload;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import com.example.networks.R;
import com.example.networks.Utility;

import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class admin_activity_upload_ebook extends AppCompatActivity {
    TextInputEditText pdfTitle;
    TextView pdfPreview;
    Uri uri;
    String getPDFName;
    ProgressDialog progressDialog;

    public void onWindowFocusChanged(boolean hasFocus) {
        /*
         * Focus on EditText on activity start
         * */
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            findViewById(R.id.ebookTitleLayout).requestFocus();
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
        setContentView(R.layout.admin_activity_upload_ebook);

        // Sets The ActionBar Style
        Utility.activityActionBar("Upload Ebook", Objects.requireNonNull(getSupportActionBar()), this);

        // Reference
        pdfTitle = findViewById(R.id.ebookTitle);
        pdfPreview = findViewById(R.id.title);

        // Attach Auto Keyboard Open On Focus
        Utility.setAutoOpenKeyboardOnFocus(pdfTitle);

        // For Selecting Notice Image from Gallery
        findViewById(R.id.selectPDF).setOnClickListener(view -> someActivityResultLauncher.launch(Intent.createChooser(new Intent().setType("application/pdf").setAction(Intent.ACTION_GET_CONTENT), "Select PDF File")));

        // For Uploading the Ebook PDF to Firebase after checking the constraints
        findViewById(R.id.uploadPDF).setOnClickListener(view -> {
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            ((TextInputLayout) findViewById(R.id.ebookTitleLayout)).setErrorEnabled(false);
            if (Objects.requireNonNull(pdfTitle.getText()).toString().isEmpty()) {
                ((TextInputLayout) findViewById(R.id.ebookTitleLayout)).setError("Required");
                pdfTitle.requestFocus();
            } else if (uri == null) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutHolder), "Choose An E-Book", Snackbar.LENGTH_LONG)
                        .setTextColor(Color.WHITE)
                        .setActionTextColor(ContextCompat.getColor(this, R.color.green))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.dimBlack))
                        .setAction("Select E-Book", view1 -> someActivityResultLauncher.launch(Intent.createChooser(new Intent().setType("application/pdf").setAction(Intent.ACTION_GET_CONTENT), "Select PDF File")));

                View v = snackbar.getView();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
                params.gravity = Gravity.TOP;
                v.setLayoutParams(params);
                snackbar.show();
            } else
                uploadPDF();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        findViewById(R.id.pdfViewer).setVisibility(View.GONE);

        findViewById(R.id.cancelPDF).setOnClickListener(view -> {
            startActivity(new Intent(this, admin_activity_upload_ebook.class));
            this.finish();
        });
    }

    private void uploadData(String downloadUrl, String uuid) {
        HashMap<String, String> data = new HashMap<>();
        data.put("title", Utility.toTitleCase(Objects.requireNonNull(pdfTitle.getText()).toString()));
        data.put("url", downloadUrl);
        data.put("key", uuid);
        FirebaseFirestore.getInstance().collection("ebook").document(uuid).set(data).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            Utility.makeToast("E-Book Uploaded!", this);
            startActivity(new Intent(this, admin_activity_upload_ebook.class));
            this.finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Utility.makeToast("Something Went Wrong!", this);
        });
    }

    private void uploadPDF() {
        progressDialog.setMessage("E-Book Uploading...");
        progressDialog.show();

        // Get A Unique Id
        String uuid = UUID.randomUUID().toString();

        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ebook").child(uuid + ".pdf");
        final UploadTask uploadTask = filePath.putFile(uri);

        uploadTask.addOnCompleteListener(taskSnapshot -> {
            if (taskSnapshot.isSuccessful())
                filePath.getDownloadUrl().addOnSuccessListener(uriTask -> uploadData(String.valueOf(uriTask), uuid));
            else {
                progressDialog.dismiss();
                Utility.makeToast("Something Went Wrong!", this);
            }
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @SuppressLint({"Range", "Recycle"})
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                uri = Objects.requireNonNull(result.getData()).getData();
                if (uri.toString().startsWith("content://"))
                    try {
                        Cursor cursor;
                        // SDK Configuration for other Android Version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            cursor = admin_activity_upload_ebook.this.getContentResolver().query(uri, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) // get filename of selected file
                                getPDFName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                else if (uri.toString().startsWith("file://"))  // Default file prefix
                    getPDFName = new File(uri.toString()).getName();

                String s = getPDFName;
                s = s.replace(".pdf", "");
                if (s.length() > 15)
                    s = s.substring(0, 15) + "...pdf";
                else
                    s = s + "...pdf";

                pdfPreview.setText(s); // Set to filename
                findViewById(R.id.pdfViewer).setVisibility(View.VISIBLE);
            }
        }
    });
}