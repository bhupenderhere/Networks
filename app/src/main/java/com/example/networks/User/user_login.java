package com.example.networks.User;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.networks.Admin.admin_login;
import com.example.networks.R;
import com.example.networks.Utility;
import com.example.networks.super_choose_login;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class user_login extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseAuth.getInstance(); // Firebase Auth Instance

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        /*
         * Focus on EditText on activity start
         * */
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            ((TextInputLayout) findViewById(R.id.emailInputLayout)).requestFocus();
    }

    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        startActivity(new Intent(this, super_choose_login.class));
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
        setContentView(R.layout.done_user_login);

        // Style The Action Bar
        Utility.activityActionBar("User Sign In", Objects.requireNonNull(getSupportActionBar()), this);

        // Attach Auto Keyboard Open On Focus
        Utility.setAutoOpenKeyboardOnFocus(findViewById(R.id.emailInput));
        Utility.setAutoOpenKeyboardOnFocus(findViewById(R.id.passwordInput));

        // Set the password view toggle
        ((TextInputLayout) findViewById(R.id.passwordInputLayout)).setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Forgot Password
        findViewById(R.id.forgotPassword).setOnClickListener(view -> forgotPassword());

        // Login
        findViewById(R.id.loginButton).setOnClickListener(view -> {
            // Remove the Errors
            TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);
            TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);
            emailInputLayout.setErrorEnabled(false);
            emailInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.green));
            passwordInputLayout.setErrorEnabled(false);
            passwordInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.green));

            // Sign In Using The Credentials
            signInWithEmailAndPassword(Objects.requireNonNull(((TextInputEditText) findViewById(R.id.emailInput)).getText()).toString().toLowerCase(), Objects.requireNonNull(((TextInputEditText) findViewById(R.id.passwordInput)).getText()).toString().toLowerCase());
        });

        // Open Admin Login Screen
        findViewById(R.id.loginAdmin).setOnClickListener(view -> {
            startActivity(new Intent(this, admin_login.class));
            this.finish();
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        /*
         * Pre-Check On Email & Password
         * */
        String emailError = email.isEmpty() ? "Required!\nField Can Not Be Empty." :
                email.length() < 8 ? "Length Too Short!\nRequire At Least 8 Characters." : null;

        String passwordError = password.isEmpty() ? "Required!\nField Can Not Be Empty." :
                password.length() < 8 ? "Length Too Short!\nRequire At Least 8 Characters." : null;


        // If an error occur then throw the error to the user interface
        if (emailError != null) {
            throwError(emailError, "email");
            return;
        } else if (passwordError != null) {
            throwError(passwordError, "password");
            return;
        }

        // Sign In using the credential provided
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("email", email).get().addOnCompleteListener(checkEmail -> {
            if (checkEmail.isSuccessful()) {
                if (checkEmail.getResult().size() > 0) {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, signIn -> {
                        if (signIn.isSuccessful()) {
                            startActivity(new Intent(this, user_home.class));
                            this.finish();
                        }
                    }).addOnFailureListener(e -> {
                        List<String> errorType = credentialValidation(Objects.requireNonNull(e.getMessage()));
                        if (errorType.size() > 1)
                            throwError(errorType.get(0), errorType.get(1));
                        else
                            Utility.makeToast(errorType.get(0), getApplicationContext());
                    });
                } else {
                    throwError("Invalid Email!", "email");
                    throwError("Invalid Password!", "password");
                }
            } else {
                throwError("Error checking email!", "email");
            }
        });
    }

    private List<String> credentialValidation(String error) {
        // If Some other type of error occur
        List<String> list = new ArrayList<>();
        switch (error) {
            case "There is no user record corresponding to this identifier. The user may have been deleted.":
                list.add("No User Found!");
                list.add("email");
                break;
            case "The email address is badly formatted.":
                list.add("Not A Valid E-mail!");
                list.add("email");
                break;
            case "The password is invalid or the user does not have a password.":
                list.add("Invalid Password!");
                list.add("password");
                break;
            case "We have blocked all requests from this device due to unusual activity. Try again later. [ Access to this account has been temporarily disabled due to many failed login attempts. You can immediately restore it by resetting your password or you can try again later. ]":
                list.add("Account disabled temporarily due to many failed login attempts!\nWait or try resetting your password.");
                break;
            default:
                list.add(error);
                break;
        }
        return list;
    }

    private void throwError(String message, String type) {
        /*
         * Sets Error To The TextInputLayout For Both Email & Password
         * */
        TextInputLayout emailInputLayout = findViewById(R.id.emailInputLayout);
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInputLayout);

        if (type.equals("password")) {
            passwordInputLayout.setError(message);
            passwordInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
        } else if (type.equals("email")) {
            emailInputLayout.setError(message);
            emailInputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
        }
    }

    private void forgotPassword() {
        // Set a TextInputLayout
        TextInputLayout inputLayout = new TextInputLayout(user_login.this);
        inputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        inputLayout.setPadding(40, 60, 40, 10);
        inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.green));
        inputLayout.setHint("E-mail");
        inputLayout.setHintTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)));

        // Set a TextInputEditText
        TextInputEditText input = new TextInputEditText(inputLayout.getContext());
        input.requestFocus();
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setTextColor(ContextCompat.getColor(user_login.this, R.color.black));
        inputLayout.addView(input);

        // Make A Dialog Box
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(user_login.this, R.style.AlertDialogTheme);
        builder.setTitle(Utility.spannableString("Forgot Password?"))
                .setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.white)))
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .setView(inputLayout)
                .setPositiveButton("Reset", null);

        // Make a AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

            // Sets The Color of +ve and -ve button
            negativeButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            positiveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
            positiveButton.setOnClickListener(view -> {
                // Receive an email from the edit text and convert it into a string
                String userEmail = Objects.requireNonNull(input.getText()).toString();

                // If the string is empty then through an error
                if (userEmail.isEmpty()) {
                    inputLayout.setError("Required!\nField can't be empty!");
                    inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
                    return;
                } else if (userEmail.length() < 8) {
                    inputLayout.setError("Length Too Short!\nRequire At Least 8 Characters.");
                    inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
                    return;
                }

                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    // Sends a password reset link on the provided link
                    if (task.isSuccessful()) {
                        Utility.makeToast("A reset password link is sent!", getApplicationContext());
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(e -> {
                    String error = Objects.requireNonNull(e.getMessage());
                    if (Objects.equals(error, "The email address is badly formatted.")) {
                        inputLayout.setError("Not A Valid E-mail!");
                        inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
                        return;
                    } else if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                        inputLayout.setError("No User Found!");
                        inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
                        return;
                    } else if (!error.isEmpty()) {
                        inputLayout.setError(error);
                        inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
                        return;
                    }
                    alertDialog.dismiss();
                });
            });
        });
        alertDialog.show();
    }
}