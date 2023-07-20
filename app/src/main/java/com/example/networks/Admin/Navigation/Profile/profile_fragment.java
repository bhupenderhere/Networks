package com.example.networks.Admin.Navigation.Profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class profile_fragment extends Fragment {

    @Override
    public void onResume() {
        /*
         * Sets The Style Of Action Bar
         * */
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(Utility.spannableString("Profile"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.admin_navigation_profile_fragment, container, false);

        if (Utility.isNetworkStatusAvailable(requireContext())) {
            // Check The Internet Connection
            Utility.makeToast("Check Your Internet Connection!", requireContext());
            return fragment;
        }

        // Find the the data of that User in the database and get all the data
        // then display it on user interface
        FirebaseFirestore.getInstance()
                .collection("admin")
                .whereEqualTo("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .get().addOnCompleteListener(getData -> {
                    if (getData.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : getData.getResult()) {
                            ((TextView) fragment.findViewById(R.id.name)).setText(snapshot.getString("name"));
                            ((TextView) fragment.findViewById(R.id.email)).setText(snapshot.getString("email"));
                            ((TextView) fragment.findViewById(R.id.mobile)).setText(snapshot.getString("mobile"));
                            ((TextView) fragment.findViewById(R.id.gender)).setText(snapshot.getString("gender"));
                            ((TextView) fragment.findViewById(R.id.position)).setText(snapshot.getString("position"));
                        }
                    }
                });

        // Sign Out Button
        fragment.findViewById(R.id.signOut).setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setBackground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.white)))
                    .setTitle(Utility.spannableString("Sign out of your account?")).setCancelable(false)
                    .setPositiveButton("Sign Out", (dialogInterface, i) -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), com.example.networks.Admin.admin_login.class));
                        requireActivity().finish();
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
            });
            alertDialog.show();
        });

        return fragment;
    }
}
