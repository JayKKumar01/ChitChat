package com.chat.chitchat.Adaptor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.chat.chitchat.Frags.groups_chat;
import com.chat.chitchat.Frags.login;
import com.chat.chitchat.Frags.profile;
import com.chat.chitchat.Frags.users;

public class chatsAdapter extends FragmentStateAdapter {
    private String[] titles = {"USERS","GROUPS","PROFILE"};

    public chatsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new users();
            case 1:
                return new groups_chat();
            case 2:
                return new profile();
        }
        return new login();
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}
