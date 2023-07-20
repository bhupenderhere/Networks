package com.example.networks.Admin.Activities.Faculty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.networks.Admin.Activities.Adapter.FacultyAdapter;
import com.example.networks.Admin.Activities.Data.FacultyData;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class admin_activity_view_faculty extends AppCompatActivity {

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
        setContentView(R.layout.admin_activity_view_faculty);

        // Sets The ActionBar Style
        Utility.activityActionBar("Upload Faculty", Objects.requireNonNull(getSupportActionBar()), this);

        // References
        FloatingActionButton addFaculty = findViewById(R.id.addFaculty);
        addFaculty.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.icon_add));

        // To add New Faculty Data
        addFaculty.setOnClickListener(view -> startActivity(new Intent(this, admin_activity_add_faculty.class)));

        physicsDept();
        chemistryDept();
        mathsDept();
    }

    private void fetchFacultyData(String deptName, List<FacultyData> facultyList, RecyclerView recyclerView, LinearLayout noDataView) {
        FirebaseFirestore.getInstance().collection("faculty").whereEqualTo("category", deptName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                facultyList.clear();

                if (querySnapshot != null) {
                    if (querySnapshot.size() > 0) {
                        noDataView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        facultyList.add(document.toObject(FacultyData.class));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(new FacultyAdapter(facultyList, this, deptName));
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(e -> Utility.makeToast(e.getMessage(), this));
    }

    private void physicsDept() {
        fetchFacultyData("Physics", new ArrayList<>(), findViewById(R.id.physicsDept), findViewById(R.id.physicsNoData));
    }

    private void chemistryDept() {
        fetchFacultyData("Chemistry", new ArrayList<>(), findViewById(R.id.chemistryDept), findViewById(R.id.chemistryNoData));
    }

    private void mathsDept() {
        fetchFacultyData("Maths", new ArrayList<>(), findViewById(R.id.mathsDept), findViewById(R.id.mathsNoData));
    }
}