package com.example.networks.Admin.Activities.Register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class admin_activity_add_user extends AppCompatActivity {
    TextInputLayout nameInputLayout, emailInputLayout, regNoInputLayout;
    TextInputEditText nameInput, emailInput, regNoInput;
    String age;

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
        setContentView(R.layout.admin_activity_add_user);

        // Sets The ActionBar Style
        Utility.activityActionBar("Register User", Objects.requireNonNull(getSupportActionBar()), this);

        // Input Layouts
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        regNoInputLayout = findViewById(R.id.regNoInputLayout);

        // Edit Text
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        regNoInput = findViewById(R.id.regNoInput);

        // Progress Dialog Initialisation
        ProgressDialog progressDialog = new ProgressDialog(this);

        ((SeekBar) findViewById(R.id.ageSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                age = String.valueOf(progress);
                ((TextInputEditText) findViewById(R.id.ageInput)).setText(age);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Register User
        findViewById(R.id.registerUser).setOnClickListener(view -> {
            if (Utility.isNetworkStatusAvailable(this)) {
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            nameInputLayout.setErrorEnabled(false);
            emailInputLayout.setErrorEnabled(false);
            regNoInputLayout.setErrorEnabled(false);

            // Get text from Edit Text
            String name = Utility.toTitleCase(Objects.requireNonNull(nameInput.getText()).toString());
            String email = Objects.requireNonNull(emailInput.getText()).toString().toLowerCase();
            String reg_no = Objects.requireNonNull(regNoInput.getText()).toString();

            // Get Radio Button Selected Output
            int genderRadio = ((RadioGroup) findViewById(R.id.gender)).getCheckedRadioButtonId();
            int departmentRadio = ((RadioGroup) findViewById(R.id.department)).getCheckedRadioButtonId();

            // Validation
            if (name.isEmpty()) {
                nameInputLayout.setError("Required!");
                nameInput.requestFocus();
                return;
            } else if (email.isEmpty()) {
                emailInputLayout.setError("Required!");
                emailInput.requestFocus();
                return;
            } else if (email.length() < 8) {
                emailInputLayout.setError("Length Too Short!\nShould Have At Least 8 Characters.");
                emailInput.requestFocus();
                return;
            } else if (reg_no.length() < 8) {
                regNoInputLayout.setError("Length Too Short!\nShould Have At Least 8 Characters.");
                regNoInput.requestFocus();
                return;
            } else if (genderRadio == -1) {
                Utility.makeToast("Select The Gender!", this);
                return;
            } else if (departmentRadio == -1) {
                Utility.makeToast("Select The Department!", this);
                return;
            }

            if (age == null)
                age = "18";

            // Get The Gender
            String getGender = Utility.toTitleCase(((RadioButton) findViewById(genderRadio)).getText().toString());

            // Get The Department
            String department = Utility.toTitleCase(((RadioButton) findViewById(departmentRadio)).getText().toString());

            // Get The Current Admin Email Id
            String currentAdminEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(admin_activity_add_user.this, R.style.AlertDialogTheme);

            TextInputLayout inputLayout = new TextInputLayout(admin_activity_add_user.this);
            inputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            inputLayout.setBoxStrokeColor(ContextCompat.getColor(admin_activity_add_user.this, R.color.green));
            inputLayout.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(admin_activity_add_user.this, R.color.black)));
            inputLayout.setPadding(40, 40, 40, 20);
            inputLayout.setHint("Password");

            TextInputEditText input = new TextInputEditText(inputLayout.getContext());
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            inputLayout.addView(input);

            inputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            inputLayout.setEndIconActivated(false);

            builder.setView(inputLayout)
                    .setTitle("Enter Admin Password")
                    .setCancelable(false)
                    .setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.white)))
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.red));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.blue));

                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(click -> {
                    progressDialog.show(); // Display the Progress Dialog
                    String adminPassword = Objects.requireNonNull(input.getText()).toString();  // Get The Current Admin Password

                    if (adminPassword.isEmpty()) {
                        progressDialog.dismiss();
                        inputLayout.setError("Required!");
                        input.requestFocus();
                    } else if (adminPassword.length() < 8) {
                        progressDialog.dismiss();
                        inputLayout.setError("Length Too Short!\nShould Have At Least 8 Characters.");
                        input.requestFocus();
                    } else {
                        // Sign In With The Password Entered To Check If The Password Is Valid Or Not
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(currentAdminEmail), adminPassword).addOnCompleteListener(check -> {
                            if (check.isSuccessful()) {
                                // If The PassWord Is Valid Then Make A New User
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, reg_no).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        String authId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        HashMap<String, String> data = new HashMap<>();
                                        data.put("key", authId);
                                        data.put("age", age);
                                        data.put("name", name);
                                        data.put("email", email);
                                        data.put("reg_no", reg_no);
                                        data.put("gender", getGender);
                                        data.put("department", department);
                                        FirebaseFirestore.getInstance().collection("users").document(authId).set(data).addOnSuccessListener(success -> {
                                            FirebaseAuth.getInstance().signOut(); // Log Out The New User
                                            // Sign In The Older Admin
                                            FirebaseAuth.getInstance().signInWithEmailAndPassword(Objects.requireNonNull(currentAdminEmail), adminPassword).addOnSuccessListener(signIn -> {
                                                Utility.makeToast("User Registered Successfully!", this);
                                                progressDialog.dismiss();
                                                startActivity(new Intent(this, admin_activity_add_user.class));
                                                this.finish();
                                            }).addOnFailureListener(e -> {
                                                Utility.makeToast(e.getMessage(), this);
                                                progressDialog.dismiss();
                                            });
                                        }).addOnFailureListener(e -> {
                                            Utility.makeToast(e.getMessage(), this);
                                            progressDialog.dismiss();
                                        });
                                    }
                                }).addOnFailureListener(e -> {
                                    alertDialog.dismiss();
                                    Utility.makeToast(e.getMessage(), this);
                                    progressDialog.dismiss();
                                });
                            }
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            inputLayout.setError("Password Mismatch!");
                            input.requestFocus();
                        });
                    }
                });
            });
            alertDialog.show();
        });
    }
}