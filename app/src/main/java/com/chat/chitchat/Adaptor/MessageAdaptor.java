package com.chat.chitchat.Adaptor;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chat.chitchat.Models.Message;
import com.chat.chitchat.R;
import com.chat.chitchat.databinding.OneOneChatsBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageViewHolder>{
    Context context;
    ArrayList<Message> messages;
    public MessageAdaptor(Context context, ArrayList<Message> messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_one_chats, parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.binding.name.setText(message.getName());
        holder.binding.message.setText(message.getTxt());
        SimpleDateFormat format = new SimpleDateFormat("dd LLL yyyy HH:mm");
        String str = format.format(message.getTime()).toString();

        holder.binding.time.setText(str);
        Glide.with(context).load(message.getImgurl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.img);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        OneOneChatsBinding binding;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OneOneChatsBinding.bind(itemView);
        }
    }

}
