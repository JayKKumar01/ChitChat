package com.chat.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chat.chitchat.Adaptor.MessageAdaptor;
import com.chat.chitchat.Models.Message;
import com.chat.chitchat.R;
import com.chat.chitchat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class chatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String userName, userImgUrl, userUid, userEmail;
    String recName, recImgUrl, recUid, recEmail;
    String textField;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    ArrayList<Message> messages;
    MessageAdaptor messageAdaptor;

    boolean isGroup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        userUid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        getUserInfo();
        getRecInfo();

    }

    private void getRecInfo() {
        recName = getIntent().getStringExtra("name");
        recImgUrl = getIntent().getStringExtra("imgUrl");
        recUid = getIntent().getStringExtra("uid");
        recEmail = getIntent().getStringExtra("email");

        String xx = recEmail.replace("@","");
        String[] arr = {userUid,recUid};
        Arrays.sort(arr);
        textField = arr[0]+arr[1];
        if(xx.equals(recEmail)){
            textField = recUid;
            binding.email.setTextSize(14);
            binding.email.setTextColor(Color.parseColor("#FF009688"));
            recEmail = "Created by "+ recEmail;
        }
        updateChatView();
        binding.name.setText(recName);
        binding.email.setText(recEmail);

        Glide.with(chatActivity.this).load(recImgUrl).placeholder(R.drawable.avatar).into(binding.imgProfile);
    }

    private void getUserInfo() {
        reference.child("users")
                .child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userName = snapshot.child("name").getValue().toString();
                            userEmail = snapshot.child("email").getValue().toString();
                            userImgUrl = snapshot.child("image").getValue().toString();
                        }
                        else {
                            userName = "No User Found!";
                            userEmail = "Not Found";
                            userImgUrl = "Not Found";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void send(View view){
        String strr = binding.message.getText().toString();
        if (!strr.isEmpty()){
            sendMassege();
        }
    }

    public void sendMassege(){
        String txt = binding.message.getText().toString();
//        String[] uids = {userUid,recUid};
//        Arrays.sort(uids);
//        textField = uids[0]+uids[1];
        Long time = System.currentTimeMillis();
        Message message = new Message(txt,userName,userEmail,userUid,userImgUrl,time);
        binding.message.setText("");

        database.getReference().child("chats")
                .child(textField)
                .child(time+"")
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //isFriend();
                }
                else {
                    Toast.makeText(chatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(chatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    public void updateChatView(){
        messages = new ArrayList<>();
        messageAdaptor = new MessageAdaptor(chatActivity.this,messages);
        binding.recyclerview.setAdapter(messageAdaptor);

        database.getReference().child("chats").child(textField)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            messages.add(message);
                        }
                        messageAdaptor.notifyDataSetChanged();
                        binding.recyclerview.smoothScrollToPosition(binding.recyclerview.getAdapter().getItemCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public void goBack(View view){
        finish();
    }
}