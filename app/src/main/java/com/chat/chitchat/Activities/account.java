package com.chat.chitchat.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.chat.chitchat.R;
import com.chat.chitchat.Adaptor.accountAdaptor;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class account extends AppCompatActivity {
    com.chat.chitchat.Adaptor.accountAdaptor accountAdaptor;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    private String[] titles = {"LOGIN","SIGNUP"};

    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            openMain();
        }
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //getSupportActionBar().hide();
        viewPager2 = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        accountAdaptor = new accountAdaptor(this);
        viewPager2.setAdapter(accountAdaptor);
        new TabLayoutMediator(tabLayout,viewPager2,((tab, position) -> tab.setText(titles[position]))).attach();

        auth = FirebaseAuth.getInstance();



    }
}