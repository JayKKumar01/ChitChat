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
import com.chat.chitchat.Models.Group;
import com.chat.chitchat.Models.User;
import com.chat.chitchat.R;
import com.chat.chitchat.databinding.GroupReviewBinding;

import java.util.ArrayList;

public class GroupAdaptor extends RecyclerView.Adapter<GroupAdaptor.GroupsViewHolder>{
    Context context;
    ArrayList<Group> groups;

    public GroupAdaptor(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_review,parent,false);
        return new GroupAdaptor.GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.binding.name.setText(group.getName());
        Glide.with(context).load(group.getImgUrl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.img);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, chatActivity.class);
                intent.putExtra("name",group.getName());
                intent.putExtra("imgUrl",group.getImgUrl());
                intent.putExtra("uid",group.getTime());
                intent.putExtra("email",group.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder{
        GroupReviewBinding binding;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = GroupReviewBinding.bind(itemView);
        }
    }
}



