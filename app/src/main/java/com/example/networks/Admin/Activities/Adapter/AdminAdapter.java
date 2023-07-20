package com.example.networks.Admin.Activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.Admin.Activities.Data.AdminData;
import com.example.networks.R;

import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewAdapter> {
    private final Context context;
    private final ArrayList<AdminData> list;

    public AdminAdapter(Context context, ArrayList<AdminData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdminAdapter.AdminViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_admin_admins_list, parent, false);
        return new AdminAdapter.AdminViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAdapter.AdminViewAdapter holder, int position) {
        AdminData currentItem = list.get(position);
        holder.id.setText(currentItem.getId());
        holder.name.setText(currentItem.getName());
        holder.email.setText(currentItem.getEmail());
        holder.gender.setText(currentItem.getGender());
        holder.mobile.setText(currentItem.getMobile());
        holder.position.setText(currentItem.getPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdminViewAdapter extends RecyclerView.ViewHolder {
        TextView name, email, position, id, gender, mobile;

        public AdminViewAdapter(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.id);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            gender = itemView.findViewById(R.id.gender);
            mobile = itemView.findViewById(R.id.mobile);
            position = itemView.findViewById(R.id.position);
        }
    }
}
