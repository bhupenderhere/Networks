package com.example.networks.User.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.networks.R;
import com.example.networks.User.user_home;
import com.example.networks.User.user_login;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class user_profile extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        startActivity(new Intent(this, user_home.class));
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
        setContentView(R.layout.user_activity_user_profile);
        // Style The Action Bar
        Utility.activityActionBar("Profile", Objects.requireNonNull(getSupportActionBar()), this);


        // Find the the data of that User in the database and get all the data
        // then display it on user interface
        FirebaseFirestore.getInstance()
                .collection("users")
                .whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .get().addOnCompleteListener(getData -> {
                    if (getData.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : getData.getResult()) {
                            ((TextView) findViewById(R.id.name)).setText(snapshot.getString("name"));
                            ((TextView) findViewById(R.id.email)).setText(snapshot.getString("email"));
                            ((TextView) findViewById(R.id.age)).setText(snapshot.getString("age"));
                            ((TextView) findViewById(R.id.gender)).setText(snapshot.getString("gender"));
                            ((TextView) findViewById(R.id.department)).setText(snapshot.getString("department"));
                            ((TextView) findViewById(R.id.reg_no)).setText(snapshot.getString("reg_no"));
                        }
                    }
                });

        // Sign Out Button
        findViewById(R.id.signOut).setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.white)))
                    .setTitle(Utility.spannableString("Sign out of your account?")).setCancelable(false)
                    .setPositiveButton("Sign Out", (dialogInterface, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, user_login.class));
                        this.finish();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                // Sets The Color of +ve and -ve button
                negativeButton.setTextColor(ContextCompat.getColor(this, R.color.red));
                positiveButton.setTextColor(ContextCompat.getColor(this, R.color.blue));
            });
            alertDialog.show();
        });
    }
}