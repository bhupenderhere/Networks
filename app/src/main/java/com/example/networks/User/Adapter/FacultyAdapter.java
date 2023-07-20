package com.example.networks.User.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.User.Data.FacultyData;
import com.example.networks.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FacultyAdapter extends RecyclerView.Adapter<com.example.networks.User.Adapter.FacultyAdapter.FacultyViewAdapter> {

    final private Context context;
    final private String category;
    final private List<FacultyData> list;


    public FacultyAdapter(List<FacultyData> list, Context context, String category) {
        this.list = list;
        this.context = context;
        this.category = category;
    }

    @NonNull
    @Override
    public com.example.networks.User.Adapter.FacultyAdapter.FacultyViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_faculty_view, parent, false);
        return new com.example.networks.User.Adapter.FacultyAdapter.FacultyViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.networks.User.Adapter.FacultyAdapter.FacultyViewAdapter holder, int position) {
        holder.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.green), PorterDuff.Mode.MULTIPLY);
        holder.progressBar.setVisibility(View.VISIBLE);
        FacultyData item = list.get(position);
        holder.name.setText(item.getName());
        holder.email.setText(item.getEmail());
        holder.designation.setText(item.getDesignation());
        Picasso.get().load(item.getImage()).into(holder.imageView, new Callback() {
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FacultyViewAdapter extends RecyclerView.ViewHolder {
        Button update;
        ImageView imageView;
        ProgressBar progressBar;
        TextView name, email, designation;

        public FacultyViewAdapter(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.facultyName);
            email = itemView.findViewById(R.id.facultyMail);
            update = itemView.findViewById(R.id.updateInfo);
            imageView = itemView.findViewById(R.id.facultyImage);
            progressBar = itemView.findViewById(R.id.progressBar);
            designation = itemView.findViewById(R.id.facultyDesignation);
        }
    }
}
