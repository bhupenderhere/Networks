package com.example.networks.Admin.Activities.Notice.Delete;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.networks.Admin.Activities.Adapter.NoticeAdapter;
import com.example.networks.Admin.Activities.Data.NoticeData;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class admin_activity_delete_notice extends AppCompatActivity {
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
        setContentView(R.layout.admin_activity_delete_notice);

        // Sets The ActionBar Style
        Utility.activityActionBar("Delete Notice", Objects.requireNonNull(getSupportActionBar()), this);

        // Changes The Color Of The Progress Bar
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.green), PorterDuff.Mode.MULTIPLY);

        // Set The Recycle View
        RecyclerView noticeRecycleView = findViewById(R.id.noticeRecycleView);
        noticeRecycleView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        noticeRecycleView.setHasFixedSize(true);

        FirebaseFirestore.getInstance().collection("notice").orderBy("time").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && querySnapshot.size() > 0) {
                    ArrayList<NoticeData> data = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        data.add(document.toObject(NoticeData.class));
                    }
                    Collections.reverse(data);
                    noticeRecycleView.setAdapter(new NoticeAdapter(this, data));
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(admin_activity_delete_notice.this, R.style.AlertDialogTheme);
                    builder.setTitle("No Data Available!")
                            .setCancelable(false)
                            .setBackground(new ColorDrawable(ContextCompat.getColor(this, R.color.white)))
                            .setPositiveButton("Go Back", (dialog, which) -> {
                                startActivity(new Intent(this, com.example.networks.Admin.admin_home.class));
                                dialog.cancel();
                                this.finish();
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(dialog -> alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.blue)));
                    alertDialog.show();
                }
            } else {
                Utility.makeToast("Something Went Wrong!", this);
            }
        });
    }
}