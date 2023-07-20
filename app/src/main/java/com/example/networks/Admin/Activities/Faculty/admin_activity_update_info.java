package com.example.networks.Admin.Activities.Faculty;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class admin_activity_update_info extends AppCompatActivity {
    TextInputLayout nameInputLayout, mailInputLayout, designationInputLayout;
    TextInputEditText nameInput, mailInput, designationInput;
    ImageView image;
    Bitmap bitmap;
    String key, email, name, department, designation, imageUrl;
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
        setContentView(R.layout.admin_activity_update_info);

        // Sets The ActionBar Style
        Utility.activityActionBar("Update Information", Objects.requireNonNull(getSupportActionBar()), this);

        // References
        image = findViewById(R.id.image);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        mailInputLayout = findViewById(R.id.mailInputLayout);
        designationInputLayout = findViewById(R.id.designationInputLayout);
        nameInput = findViewById(R.id.nameInput);
        mailInput = findViewById(R.id.mailInput);
        designationInput = findViewById(R.id.designationInput);


        key = getIntent().getStringExtra("key");
        name = getIntent().getStringExtra("name");
        imageUrl = getIntent().getStringExtra("image");
        email = getIntent().getStringExtra("email");
        department = getIntent().getStringExtra("category");
        designation = getIntent().getStringExtra("designation");

        nameInput.setText(name);
        mailInput.setText(email);
        designationInput.setText(designation);

        //Spinner to choose Faculty Department
        String[] items = new String[]{"None", "Physics", "Chemistry", "Maths"};
        Spinner spinner = findViewById(R.id.spinner);
        switch (department) {
            case "Physics":
                items[0] = "Physics";
                items[1] = "None";
                break;
            case "Chemistry":
                items[0] = "Chemistry";
                items[1] = "None";
                items[2] = "Physics";
                break;
            case "Maths":
                items[0] = "Maths";
                items[1] = "None";
                items[2] = "Physics";
                items[3] = "Chemistry";
                break;
        }
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

        Picasso.get().load(imageUrl).into(image, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Select Image From Gallery
        findViewById(R.id.image).setOnClickListener(view -> openGallery.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));

        findViewById(R.id.updateButton).setOnClickListener(view -> {
            nameInputLayout.setErrorEnabled(false);
            mailInputLayout.setErrorEnabled(false);
            designationInputLayout.setErrorEnabled(false);
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            if (!validation()) return;

            if (bitmap != null) uploadImage(key);
            else uploadData(imageUrl, key);

        });
        findViewById(R.id.deleteButton).setOnClickListener(click -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.white)))
                    .setTitle(Utility.spannableString("Are You Sure?")).setCancelable(false)
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        FirebaseFirestore.getInstance().collection("faculty").document(key).delete();
                        Utility.makeToast("Faculty Deleted!", this);
                        startActivity(new Intent(this, com.example.networks.Admin.Activities.Faculty.admin_activity_view_faculty.class));
                        this.finish();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.blue));
            });
            alertDialog.show();
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private boolean validation() {
        name = Objects.requireNonNull(nameInput.getText()).toString();
        email = Objects.requireNonNull(mailInput.getText()).toString();
        designation = Objects.requireNonNull(designationInput.getText()).toString();
        if (name.isEmpty()) {
            nameInputLayout.setError("Required!");
            nameInput.requestFocus();
        } else if (email.isEmpty()) {
            mailInputLayout.setError("Required!");
            mailInput.requestFocus();
        } else if (designation.isEmpty()) {
            designationInputLayout.setError("Required!");
            designationInput.requestFocus();
        } else if (department.equals("None"))
            Utility.makeToast("Please Select The Department!", this);
        else return true;
        return false;
    }

    private void uploadData(String downloadURL, String uuid) {
        HashMap<String, String> map = new HashMap<>();
        map.put("key", uuid);
        map.put("name", Utility.toTitleCase(name));
        map.put("email", email.toLowerCase());
        map.put("image", downloadURL.toLowerCase());
        map.put("category", Utility.toTitleCase(department));
        map.put("designation", Utility.toTitleCase(designation));

        FirebaseFirestore.getInstance().collection("faculty").document(uuid).set(map).addOnSuccessListener(documentReference -> {
            progressDialog.dismiss();
            Utility.makeToast("Information Updated!", this);
            startActivity(new Intent(this, com.example.networks.Admin.Activities.Faculty.admin_activity_view_faculty.class));
            this.finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Utility.makeToast("Something Went Wrong!", this);
        });
    }

    private void uploadImage(String uuid) {
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        // Byte Stream to compress Images
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteStream);
        byte[] finalImage = byteStream.toByteArray();

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
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) try {
                bitmap = Utility.rotateImageIfRequired(getApplicationContext(), MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), result.getData().getData()), result.getData().getData());
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    });
}