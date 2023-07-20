package com.example.networks.User.Navigation.Ebook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networks.R;
import com.example.networks.User.Adapter.EbookAdapter;
import com.example.networks.User.Data.EbookData;
import com.example.networks.Utility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ebook_fragment extends Fragment {

    RecyclerView ebookRecycleView;

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
        View fragment = inflater.inflate(R.layout.user_navigation_ebook_fragment, container, false);
        ebookRecycleView = fragment.findViewById(R.id.ebookRecycleView);

        ebookRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ebookRecycleView.setHasFixedSize(true);

        FirebaseFirestore.getInstance().collection("ebook").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    ArrayList<EbookData> data = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        EbookData model = document.toObject(EbookData.class);
                        data.add(model);
                    }
                    RecyclerView.Adapter<EbookAdapter.EbookViewAdapter> adapter = new EbookAdapter(requireActivity(), data);
                    ebookRecycleView.setAdapter(adapter);
                }
            } else {
                Utility.makeToast("Something Went Wrong!", requireActivity());
            }
        });

        return fragment;
    }
}