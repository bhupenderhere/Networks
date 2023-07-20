package com.example.networks.User.Navigation.Notice;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networks.User.Adapter.NoticeAdapter;
import com.example.networks.User.Data.NoticeData;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class notice_fragment extends Fragment {
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
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.user_navigation_notice_fragment, container, false);
        // References
        RecyclerView noticeRecycleView = fragment.findViewById(R.id.noticeRecycleView);

        noticeRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noticeRecycleView.setHasFixedSize(true);

        FirebaseFirestore.getInstance().collection("notice").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    ArrayList<NoticeData> data = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        NoticeData model = document.toObject(NoticeData.class);
                        data.add(model);
                    }
                    RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> adapter = new NoticeAdapter(requireActivity(), data);
                    noticeRecycleView.setAdapter(adapter);
                }
            } else {
                Utility.makeToast("Something Went Wrong!", requireActivity());
            }
        });
        return fragment;
    }
}