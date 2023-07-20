package com.example.networks.Admin;

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
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class admin_login extends AppCompatActivity {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    public void onWindowFocusChanged(boolean hasFocus) {
        /*
         * Focus on EditText on activity start
         * */
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            findViewById(R.id.emailInputLayout).requestFocus();
    }

    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        startActivity(new Intent(this, com.example.networks.super_choose_login.class));
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
        setContentView(R.layout.admin_login);

        // Style The Action Bar
        Utility.activityActionBar("Admin Sign In", Objects.requireNonNull(getSupportActionBar()), this);

        // Attach Auto Keyboard Open On Focus
        Utility.setAutoOpenKeyboardOnFocus(findViewById(R.id.emailInput));

        // Set the password view toggle
        ((TextInputLayout) findViewById(R.id.passwordInputLayout)).setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

        // Forgot Password
        findViewById(R.id.forgotPassword).setOnClickListener(view -> forgotPassword());

        // Login
        findViewById(R.id.loginButton).setOnClickListener(view -> {
            if (Utility.isNetworkStatusAvailable(this)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", this);
                return;
            }
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

        // Open User Login Screen
        findViewById(R.id.loginUser).setOnClickListener(view -> {
            startActivity(new Intent(this, com.example.networks.User.user_login.class));
            this.finish();
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
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
        if (emailError != null && passwordError != null) {
            setErrorAndColor(findViewById(R.id.emailInputLayout), emailError);
            setErrorAndColor(findViewById(R.id.passwordInputLayout), passwordError);
            return;
        } else if (emailError != null || passwordError != null) {
            setErrorAndColor(emailError != null ? findViewById(R.id.emailInputLayout) : findViewById(R.id.passwordInputLayout), emailError != null ? emailError : passwordError);
            return;
        }
        // Sign In using the credential provided
        FirebaseFirestore.getInstance().collection("admin").whereEqualTo("email", email).get().addOnSuccessListener(task -> {
            if (!task.isEmpty()) {
                progressDialog.setMessage("Signing In...");
                progressDialog.show();
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(signIn -> {
                    if (signIn.isSuccessful()) {
                        progressDialog.dismiss();
                        Utility.makeToast("Welcome!", this);
                        startActivity(new Intent(this, admin_home.class));
                        this.finish();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    List<String> errorType = credentialValidation(Objects.requireNonNull(e.getMessage()));
                    if (errorType.size() > 1)
                        if (Objects.equals(errorType.get(1), "email"))
                            setErrorAndColor(findViewById(R.id.emailInputLayout), errorType.get(0));
                        else
                            setErrorAndColor(findViewById(R.id.passwordInputLayout), errorType.get(0));
                    else
                        Utility.makeToast(errorType.get(0), getApplicationContext());
                });
            } else {
                progressDialog.dismiss();
                setErrorAndColor(findViewById(R.id.emailInputLayout), "No User Found!\nThere is no user registered with this E-mail!");
            }
        });
    }

    private List<String> credentialValidation(String error) {
        // If Some other type of error occur
        List<String> list = new ArrayList<>();
        switch (error) {
            case "The password is invalid or the user does not have a password.":
                list.add("Invalid Password!\nThe Password entered is not valid!");
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

    private void forgotPassword() {
        // Set a TextInputLayout
        TextInputLayout inputLayout = new TextInputLayout(admin_login.this);
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
        input.setTextColor(ContextCompat.getColor(admin_login.this, R.color.black));
        inputLayout.addView(input);

        // Make A Dialog Box
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(admin_login.this, R.style.AlertDialogTheme);
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

            negativeButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
            positiveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));

            positiveButton.setOnClickListener(view -> {
                // Receive an email from the edit text and convert it into a string
                String userEmail = Objects.requireNonNull(input.getText()).toString();

                // If the string is empty then through an error
                if (userEmail.isEmpty()) {
                    setErrorAndColor(inputLayout, "Required!\nField can't be empty!");
                    return;
                } else if (userEmail.length() < 8) {
                    setErrorAndColor(inputLayout, "Length Too Short!\nRequire At Least 8 Characters.");
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
                        setErrorAndColor(inputLayout, "Invalid E-mail!\nThe E-mail entered is not valid!");
                        return;
                    } else if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                        setErrorAndColor(inputLayout, "No User Found!\nThere is no user registered with this E-mail!");
                        return;
                    } else if (!error.isEmpty()) {
                        setErrorAndColor(inputLayout, error);
                        return;
                    }
                    alertDialog.dismiss();
                });
            });
        });
        alertDialog.show();
    }

    private void setErrorAndColor(TextInputLayout inputLayout, String errorMessage) {
        inputLayout.setError(errorMessage);
        inputLayout.setBoxStrokeColor(ContextCompat.getColor(this, R.color.red));
    }
}