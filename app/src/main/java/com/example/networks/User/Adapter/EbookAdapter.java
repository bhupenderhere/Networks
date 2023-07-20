package com.example.networks.User.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.R;
import com.example.networks.User.Activities.view_ebook;
import com.example.networks.User.Data.EbookData;

import java.util.List;

public class EbookAdapter extends RecyclerView.Adapter<EbookAdapter.EbookViewAdapter> {

    private final Context context;
    private final List<EbookData> data;

    public EbookAdapter(Context context, List<EbookData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public EbookAdapter.EbookViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_ebook, parent, false);
        return new EbookAdapter.EbookViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EbookAdapter.EbookViewAdapter holder, int position) {
        EbookData currentItem = data.get(position);
        String bookTitle = currentItem.getTitle();

        if (bookTitle.length() > 15)
            bookTitle = bookTitle.substring(0, 14) + "...";

        holder.bookTitle.setText(bookTitle);
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, view_ebook.class);
            intent.putExtra("url", currentItem.getUrl());
            intent.putExtra("title", currentItem.getTitle());
            context.startActivity(intent);
        });
        holder.bookDownload.setOnClickListener(download -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(currentItem.getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class EbookViewAdapter extends RecyclerView.ViewHolder {
        TextView bookTitle;
        Button bookDownload;

        public EbookViewAdapter(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookDownload = itemView.findViewById(R.id.bookDownload);
        }
    }

}
