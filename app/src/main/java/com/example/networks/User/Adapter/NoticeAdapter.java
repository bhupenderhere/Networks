package com.example.networks.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.User.Data.NoticeData;
import com.example.networks.R;
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_notice, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, int position) {
        NoticeData currentItem = list.get(position);
        holder.noticeTitle.setText(currentItem.getTitle());
        holder.date_and_time.setText(String.format("%s\n%s", currentItem.getTime(), currentItem.getDate()));
        // Setting color of the Progress Bar
        holder.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.MULTIPLY);

        if (currentItem.getURL() != null) {
            holder.progressBar.setVisibility(View.VISIBLE);
            Picasso.get().load(currentItem.getURL()).into(holder.noticeImage, new Callback() {
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
        }
        holder.noticeImage.setOnClickListener(click -> {
            Intent intent = new Intent(context, com.example.networks.Common.FullScreenImage.class);
            intent.putExtra("url",currentItem.getURL());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NoticeViewAdapter extends RecyclerView.ViewHolder {
        private final TextView noticeTitle, date_and_time;
        private final ImageView noticeImage;
        private final ProgressBar progressBar;

        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            noticeTitle = itemView.findViewById(R.id.noticeTitle);
            noticeImage = itemView.findViewById(R.id.noticeImage);
            progressBar = itemView.findViewById(R.id.progressBar);
            date_and_time = itemView.findViewById(R.id.date_and_time);
        }
    }
}
