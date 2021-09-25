package com.chat.chitchat.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.UiAutomation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.chitchat.Models.User;
import com.chat.chitchat.R;
import com.chat.chitchat.Adaptor.chatsAdapter;
import com.chat.chitchat.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    com.chat.chitchat.Adaptor.chatsAdapter chatsAdapter;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String[] titles = {"USERS","GROUPS","PROFILE"};
    ImageView logout;

    ConstraintLayout boxLogout;
    TextView ok,cancel;
    FirebaseDatabase database;
    String userName;
    String userUid;

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() == null){
            openLogin();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        logout = findViewById(R.id.logout);
        boxLogout = findViewById(R.id.box_logout);
        ok = findViewById(R.id.ok_logout);
        cancel = findViewById(R.id.can_logout);

        chatsAdapter = new chatsAdapter(this);
        viewPager2.setAdapter(chatsAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(titles[position]))).attach();

        auth = FirebaseAuth.getInstance();
        userUid = auth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        database.getReference().child("users")
                .child(userUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists() && (snapshot.hasChild("name"))){
                            userName = snapshot.child("name").getValue().toString();
                        }
                        else {
                            userName = "No User Found!";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }



    public void logOut(View view){
        boxLogout.setVisibility(View.VISIBLE);
    }
    public void okLogout(View view){
        auth.signOut();
        openLogin();
        boxLogout.setVisibility(View.GONE);
    }
    public void canLogut(View view){
        boxLogout.setVisibility(View.GONE);
    }


    private void openLogin(){
        startActivity(new Intent(this,account.class));
        finish();
    }
}