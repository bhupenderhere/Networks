package com.example.networks.Admin.Activities.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.Admin.Activities.Data.NoticeData;
import com.example.networks.Admin.Activities.Notice.Delete.admin_activity_delete_notice;
import com.example.networks.Common.FullScreenImage;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {
    private final Context context;
    private final ArrayList<NoticeData> list;

    public NoticeAdapter(Context context, ArrayList<NoticeData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_admin_delete_notice, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {
        NoticeData currentItem = list.get(position);

        // Setting color of the Progress Bar
        holder.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.MULTIPLY);
        holder.noticeTitle.setText(currentItem.getTitle());

        if (currentItem.getUrl() != null) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(currentItem.getUrl()).into(holder.noticeImage, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    holder.progressBar.setVisibility(View.GONE);
                    throw new RuntimeException(e);
                }
            });
        } else
            Utility.makeToast("Something Went Wrong!", context);

        holder.viewNotice.setOnClickListener(view -> {
            Intent intent = new Intent(context, FullScreenImage.class);
            intent.putExtra("url", currentItem.getUrl());
            context.startActivity(intent);
        });

        holder.deleteNotice.setOnClickListener(view -> {
            if (Utility.isNetworkStatusAvailable(context)) {
                // Check The Internet Connection
                Utility.makeToast("Check Your Internet Connection!", context);
                return;
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setBackground(new ColorDrawable(ContextCompat.getColor(context, R.color.white)))
                    .setTitle(Utility.spannableString("Are you sure you want to delete the notice?")).setCancelable(false)
                    .setPositiveButton("Delete", (dialogInterface, i) -> {
                        FirebaseStorage.getInstance().getReferenceFromUrl(currentItem.getUrl()).delete().addOnCompleteListener(task -> {
                            FirebaseFirestore.getInstance().collection("notice")
                                    .document(currentItem.getKey())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Utility.makeToast("Notice Deleted", context);
                                        Intent intent = new Intent(context, admin_activity_delete_notice.class);
                                        context.startActivity(intent);
                                        // Finish the current activity
                                        if (context instanceof Activity)
                                            ((Activity) context).finish();
                                    }).addOnFailureListener(e -> Utility.makeToast("Something Went Wrong!", context));
                        });
                    }).setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialog -> {
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.red));
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.blue));
            });
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NoticeViewAdapter extends RecyclerView.ViewHolder {
        private final TextView noticeTitle;
        private final ImageView noticeImage;
        private final Button deleteNotice, viewNotice;
        private final ProgressBar progressBar;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            noticeTitle = itemView.findViewById(R.id.noticeTitle);
            noticeImage = itemView.findViewById(R.id.noticeImage);
            progressBar = itemView.findViewById(R.id.progressBar);
            deleteNotice = itemView.findViewById(R.id.deleteNotice);
            viewNotice = itemView.findViewById(R.id.viewNotice);
        }
    }
}
