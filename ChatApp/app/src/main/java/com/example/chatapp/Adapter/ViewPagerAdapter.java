package com.example.chatapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.chatapp.fragments.CallFragment;
import com.example.chatapp.fragments.ChatsFragment;
import com.example.chatapp.fragments.ContactFragment;
import com.example.chatapp.fragments.FriendsFragment;
import com.example.chatapp.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatsFragment();
            case 1:
                return new FriendsFragment();
            case 2:
                return new ContactFragment();
            case 3:
                return new CallFragment();
            case 4:
                return new ProfileFragment();
            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
