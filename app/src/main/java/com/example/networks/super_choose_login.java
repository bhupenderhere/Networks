package com.example.networks;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.networks.Admin.admin_login;
import com.example.networks.User.user_login;

import java.util.Objects;

public class super_choose_login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_choose_login);

        // Hide The Action Bar In The Splash Screen
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Open User Login Screen
        findViewById(R.id.userLogin).setOnClickListener(view -> {
            startActivity(new Intent(this, user_login.class));
            this.finish();
        });

        // Open Admin Login Screen
        findViewById(R.id.adminLogin).setOnClickListener(view -> {
            startActivity(new Intent(this, admin_login.class));
            this.finish();
        });
    }
}