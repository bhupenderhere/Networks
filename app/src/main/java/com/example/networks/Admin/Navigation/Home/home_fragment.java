package com.example.networks.Admin.Navigation.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.networks.Admin.Activities.Ebook.Upload.admin_activity_upload_ebook;
import com.example.networks.Admin.Activities.Faculty.admin_activity_view_faculty;
import com.example.networks.Admin.Activities.Image.Upload.admin_activity_upload_image;
import com.example.networks.Admin.Activities.List.admin_activity_admins_list;
import com.example.networks.Admin.Activities.List.admin_activity_users_list;
import com.example.networks.Admin.Activities.Notice.Delete.admin_activity_delete_notice;
import com.example.networks.Admin.Activities.Notice.Upload.admin_activity_upload_notice;
import com.example.networks.Admin.Activities.Register.admin_activity_add_admin;
import com.example.networks.Admin.Activities.Register.admin_activity_add_user;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class home_fragment extends Fragment {
    @Override
    public void onResume() {
        /*
         * Sets The Style Of Action Bar
         * */
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(Utility.spannableString("Home"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.admin_navigation_home_fragment, container, false);

        if (Utility.isNetworkStatusAvailable(requireContext())) {
            // Check The Internet Connection
            Utility.makeToast("Check Your Internet Connection!", requireContext());
            return fragment;
        }

        // Sets Greeting Message
        ((TextView) fragment.findViewById(R.id.greeting)).setText(Utility.getGreetings());

        // Sets The Hi Message To The Current Admin
        FirebaseFirestore.getInstance()
                .collection("admin")
                .whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .get().addOnCompleteListener(getData -> {
                    if (getData.isSuccessful())
                        for (QueryDocumentSnapshot snapshot : getData.getResult()) {
                            String name = "\uD83D\uDC4B Hi, " + snapshot.getString("name");
                            ((TextView) fragment.findViewById(R.id.name)).setText(name);
                        }
                });

        // See All Admins List
        fragment.findViewById(R.id.seeAdmins).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_admins_list.class));
            requireActivity().finish();
        });

        // See All Users List
        fragment.findViewById(R.id.seeUsers).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_users_list.class));
            requireActivity().finish();
        });

        // Register A New Admin
        fragment.findViewById(R.id.addAdmin).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_add_admin.class));
            requireActivity().finish();
        });

        // Register A New User
        fragment.findViewById(R.id.addUser).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_add_user.class));
            requireActivity().finish();
        });

        // Upload Notice
        fragment.findViewById(R.id.uploadNotice).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_upload_notice.class));
            requireActivity().finish();
        });

        // Delete Notice
        fragment.findViewById(R.id.deleteNotice).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_delete_notice.class));
            requireActivity().finish();
        });

        // Upload Ebook
        fragment.findViewById(R.id.uploadEbook).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_upload_ebook.class));
            requireActivity().finish();
        });

        // Upload Image
        fragment.findViewById(R.id.uploadImage).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_upload_image.class));
            requireActivity().finish();
        });

        // Upload Faculty
        fragment.findViewById(R.id.uploadFaculty).setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), admin_activity_view_faculty.class));
            requireActivity().finish();
        });

        return fragment;
    }
}