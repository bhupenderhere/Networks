package com.example.networks.User.Navigation.Faculty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.networks.User.Adapter.FacultyAdapter;
import com.example.networks.User.Data.FacultyData;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class faculty_fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.user_navigation_faculty_fragment, container, false);

        fetchFacultyData("Physics", new ArrayList<>(), fragment.findViewById(R.id.physicsDept), fragment.findViewById(R.id.physicsNoData));
        fetchFacultyData("Chemistry", new ArrayList<>(), fragment.findViewById(R.id.chemistryDept), fragment.findViewById(R.id.chemistryNoData));
        fetchFacultyData("Maths", new ArrayList<>(), fragment.findViewById(R.id.mathsDept), fragment.findViewById(R.id.mathsNoData));

        return fragment;
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(new FacultyAdapter(facultyList, getContext(), deptName));
                } else {
                    noDataView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(e -> Utility.makeToast(e.getMessage(), getContext()));
    }
}