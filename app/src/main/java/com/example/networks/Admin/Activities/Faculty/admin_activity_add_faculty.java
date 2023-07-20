package com.example.networks.Admin.Activities.Faculty;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

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
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class admin_activity_add_faculty extends AppCompatActivity {

    String name, mail, designation, department;
    Bitmap bitmap = null;
    TextInputLayout nameInputLayout, mailInputLayout, designationInputLayout;
    TextInputEditText nameInput, mailInput, designationInput;
    ImageView image, addImage;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        startActivity(new Intent(this, com.example.networks.Admin.Activities.Faculty.admin_activity_view_faculty.class));
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
        setContentView(R.layout.admin_activity_add_faculty);

        // Sets The ActionBar Style
        Utility.activityActionBar("Add Faculty", Objects.requireNonNull(getSupportActionBar()), this);

        // References
        image = findViewById(R.id.image);
        addImage = findViewById(R.id.addImage);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        mailInputLayout = findViewById(R.id.mailInputLayout);
        designationInputLayout = findViewById(R.id.designationInputLayout);
        nameInput = findViewById(R.id.nameInput);
        mailInput = findViewById(R.id.mailInput);
        designationInput = findViewById(R.id.designationInput);

        // Select Image From Gallery
        findViewById(R.id.addImage).setOnClickListener(view -> openGallery
                .launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));
        // Select Image From Gallery
        findViewById(R.id.image).setOnClickListener(view -> openGallery
                .launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        //Spinner to choose Faculty Department
        Spinner spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"None", "Physics", "Chemistry", "Maths"};
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        findViewById(R.id.uploadButton).setOnClickListener(view -> {
            nameInputLayout.setErrorEnabled(false);
            mailInputLayout.setErrorEnabled(false);
            designationInputLayout.setErrorEnabled(false);
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            if (!validation())
                return;

            if (bitmap == null) {
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

    private boolean validation() {
        name = Objects.requireNonNull(nameInput.getText()).toString();
        mail = Objects.requireNonNull(mailInput.getText()).toString();
        designation = Objects.requireNonNull(designationInput.getText()).toString();
        if (name.isEmpty()) {
            nameInputLayout.setError("Required!");
            nameInput.requestFocus();
        } else if (mail.isEmpty()) {
            mailInputLayout.setError("Required!");
            mailInput.requestFocus();
        } else if (designation.isEmpty()) {
            designationInputLayout.setError("Required!");
            designationInput.requestFocus();
        } else if (department.equals("None"))
            Utility.makeToast("Please Select The Department!", this);
        else
            return true;
        return false;
    }

    private void uploadData(String downloadURL, String uuid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", uuid);
        map.put("name", Utility.toTitleCase(name));
        map.put("email", mail.toLowerCase());
        map.put("image", downloadURL.toLowerCase());
        map.put("category", department);
        map.put("designation", Utility.toTitleCase(designation));

        FirebaseFirestore.getInstance().collection("faculty").document(uuid).set(map)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Faculty Added!", this);
                    startActivity(new Intent(this, com.example.networks.Admin.Activities.Faculty.admin_activity_view_faculty.class));
                    this.finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Utility.makeToast("Something Went Wrong!", this);
                });
    }

    private void uploadImage() {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        // Byte Stream to compress Images
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);
        byte[] finalImage = byteStream.toByteArray();

        // Uuid
        String uuid = UUID.randomUUID().toString();

        // Get A Path Link From Firebase Storage
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("faculty").child(uuid + ".jpg");
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
                    image.setImageBitmap(bitmap);
                    addImage.setVisibility(View.GONE);
                    image.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    });
}