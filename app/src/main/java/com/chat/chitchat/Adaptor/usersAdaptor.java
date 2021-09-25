package com.chat.chitchat.Adaptor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.chat.chitchat.Activities.chatActivity;
import com.chat.chitchat.R;
import com.chat.chitchat.Models.User;
import com.chat.chitchat.databinding.UsersReviewBinding;

import java.util.ArrayList;

public class usersAdaptor extends RecyclerView.Adapter<usersAdaptor.UsersViewHolder> {
    Context context;
    ArrayList<User> users;
    public usersAdaptor(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_review,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.name.setText(user.getName());
        holder.binding.email.setText(user.getEmail());
        Glide.with(context).load(user.getImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.img);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("imgUrl",user.getImage());
                intent.putExtra("uid",user.getUid());
                intent.putExtra("email",user.getEmail());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        UsersReviewBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = UsersReviewBinding.bind(itemView);
        }
    }
}
