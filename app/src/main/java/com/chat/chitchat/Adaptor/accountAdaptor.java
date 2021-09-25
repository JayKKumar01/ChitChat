package com.chat.chitchat.Adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chat.chitchat.Frags.login;
import com.chat.chitchat.Frags.signup;

public class accountAdaptor extends FragmentStateAdapter {
    private String[] titles = {"LOGIN","SIGNUP"};

    public accountAdaptor(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new login();
            case 1:
                return new signup();
        }
        return new login();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
