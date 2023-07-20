package com.example.networks.User.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.networks.R;
import com.example.networks.Utility;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class view_ebook extends AppCompatActivity {
    private String url;
    private PDFView viewer;
    ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        /*
         * Close Current Activity & Move to Previous Activity
         * */
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
        setContentView(R.layout.user_activity_view_ebook);
        url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");

        progressBar = findViewById(R.id.progressBar);

        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);

        // Sets The ActionBar Style
        Utility.activityActionBar(title, Objects.requireNonNull(getSupportActionBar()), this);

        viewer = findViewById(R.id.bookView);

        new BookDownload().execute(url);
    }

    private class BookDownload extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL link = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) link.openConnection();
                if (httpURLConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            viewer.fromStream(inputStream).load();
            progressBar.setVisibility(View.GONE);
        }
    }
}