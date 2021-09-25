package com.chat.chitchat.Frags;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.chitchat.R;
import com.chat.chitchat.Models.User;
import com.chat.chitchat.Adaptor.usersAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class users extends Fragment {
    View view;
    FirebaseDatabase database;
    ArrayList<User> users;
    usersAdaptor userAd;
    RecyclerView recyclerView;
    ProgressDialog dialog;
    FirebaseAuth auth;
    String userUid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recyclerview);
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("showing... users");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        userUid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userAd = new usersAdaptor(getContext(),users);
        recyclerView.setAdapter(userAd);
        dialog.show();

        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!userUid.equals(user.getUid())) {
                        users.add(user);
                    }
                }
                userAd.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return  view;
    }
}