package com.example.networks.Admin.Activities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networks.Admin.Activities.Data.UsersData;
import com.example.networks.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewAdapter> {
    private final Context context;
    private final ArrayList<UsersData> list;

    public UserAdapter(Context context, ArrayList<UsersData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_admin_users_list, parent, false);
        return new UserAdapter.UserViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewAdapter holder, int position) {
        UsersData currentItem = list.get(position);
        holder.age.setText(currentItem.getAge());
        holder.name.setText(currentItem.getName());
        holder.email.setText(currentItem.getEmail());
        holder.gender.setText(currentItem.getGender());
        holder.reg_no.setText(currentItem.getReg_no());
        holder.department.setText(currentItem.getDepartment());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class UserViewAdapter extends RecyclerView.ViewHolder {
        TextView name, email, age, reg_no, gender, department;

        public UserViewAdapter(@NonNull View itemView) {
            super(itemView);
            age = itemView.findViewById(R.id.age);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            reg_no = itemView.findViewById(R.id.reg_no);
            gender = itemView.findViewById(R.id.gender);
            department = itemView.findViewById(R.id.department);
        }
    }
}
