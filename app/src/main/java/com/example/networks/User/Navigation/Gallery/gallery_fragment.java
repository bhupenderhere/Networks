package com.example.networks.User.Navigation.Gallery;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networks.R;
import com.example.networks.User.Adapter.GalleryAdapter;
import com.example.networks.Utility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class gallery_fragment extends Fragment {
    @Override
    public void onResume() {
        /*
         * Sets The Style Of Action Bar
         * */
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(Utility.spannableString("Networks"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.user_navigation_gallery_fragment, container, false);
        RecyclerView workshopRecycleView = fragment.findViewById(R.id.convocation);
        workshopRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        workshopRecycleView.setHasFixedSize(true);
        RecyclerView otherRecycleView = fragment.findViewById(R.id.others);
        otherRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        otherRecycleView.setHasFixedSize(true);
        displayImages(workshopRecycleView, "Workshops");
        displayImages(otherRecycleView, "Other Events");
        return fragment;
    }

    public void displayImages(RecyclerView recyclerView, String category) {
        FirebaseFirestore.getInstance().collection("gallery").whereEqualTo("category", category).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    ArrayList<String> data = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        data.add(Objects.requireNonNull(document.get("url")).toString());
                    }
                    RecyclerView.Adapter<GalleryAdapter.GalleryViewAdapter> adapter = new GalleryAdapter(requireActivity(), data);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    recyclerView.setAdapter(adapter);
                }
            } else
                Utility.makeToast("Something Went Wrong!", requireActivity());
        });
    }
}