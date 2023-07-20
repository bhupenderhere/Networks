package com.example.networks.Admin.Navigation.About;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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

import java.time.Year;

public class about_fragment extends Fragment {

    @Override
    public void onResume() {
        /*
         * Sets The Style Of Action Bar
         * */
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setTitle(Utility.spannableString("About"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.admin_navigation_about_fragment, container, false);

        // Sets the links to the social accounts profiles.
        fragment.findViewById(R.id.github).setOnClickListener(view -> redirectDialog("https://www.github.com/bhupenderhere"));
        fragment.findViewById(R.id.twitter).setOnClickListener(view -> redirectDialog("https://www.twitter.com/bhupenderhere"));
        fragment.findViewById(R.id.instagram).setOnClickListener(view -> redirectDialog("https://www.instagram.com/bhupenderhere"));

        // Sets The Current Year In The Credits
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            ((TextView) fragment.findViewById(R.id.currentYear)).setText(String.valueOf(Year.now().getValue()));

        return fragment;
    }

    private void redirectDialog(String url) {
        // Open A Dialog Box To Ask For The Permission To Redirect To A Browser
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setBackground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.white)))
                .setTitle(Utility.spannableString("Are your sure?")).setMessage("You will be redirected to a browser!").setCancelable(false)
                .setPositiveButton("Open", (dialogInterface, i) -> {
                    // Check If The Network Connection Is Turned Off
                    if (Utility.isNetworkStatusAvailable(requireContext())) {
                        Utility.makeToast("Check Your Internet Connection!", requireActivity());
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.blue));
        });
        alertDialog.show();
    }
}