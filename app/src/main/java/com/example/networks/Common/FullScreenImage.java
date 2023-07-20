package com.example.networks.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.networks.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class FullScreenImage extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_full_screen_image);

        // Hides The Action Bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        Picasso.get().load(getIntent().getStringExtra("url"))
                .into((findViewById(R.id.fullImage)), new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}