package com.example.networks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.networks.Admin.admin_home;
import com.example.networks.User.user_home;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class super_splash_screen extends AppCompatActivity {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_splash_screen);

        // Hide The Action Bar In The Splash Screen
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Changes The Color Of The Progress Bar
        ((ProgressBar) findViewById(R.id.progressBar)).getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.MULTIPLY);

        /*
         * If No User Logged In Then Show The Login Screen
         * */
        if (currentUser == null) {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, super_choose_login.class));
                finish();
            }, 2000);
        } else {
            /*
             * If The E-mail Is In The Admin Collection
             * Then Show Admin Panel
             * Otherwise User Panel
             * */
            FirebaseFirestore.getInstance().collection("admin")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty())
                            showAdminUI();
                        else
                            showUserUI();
                    });
        }
    }

    private void showAdminUI() {
        /*
         * Show Admin Panel
         * */
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, admin_home.class));
            finish();
        }, 2000);
    }

    private void showUserUI() {
        /*
         * Show User Panel
         * */
        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, user_home.class));
            finish();
        }, 2000);
    }
}