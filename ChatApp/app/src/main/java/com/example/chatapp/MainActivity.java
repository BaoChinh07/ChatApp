package com.example.chatapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chatapp.Adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private ViewPager mViewPager;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();
        setUpViewPager();
    }

    public void setControl() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public void setEvent() {
        //Đặt navigation vào MainActivity
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_chats:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.action_friends:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.action_contacts:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.action_calls:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.action_profile:
                        mViewPager.setCurrentItem(4);
                        break;
                }
                return true;
            }
        });
    }
    // Đặt ViewPager
    private void setUpViewPager() {
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.action_chats).setChecked(true);
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.action_friends).setChecked(true);
                    case 2:
                        mBottomNavigationView.getMenu().findItem(R.id.action_contacts).setChecked(true);
                    case 3:
                        mBottomNavigationView.getMenu().findItem(R.id.action_calls).setChecked(true);
                    case 4:
                        mBottomNavigationView.getMenu().findItem(R.id.action_profile).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void statusActivity(String statusActivity) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("statusActivity", statusActivity);
        mDatabaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusActivity("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        statusActivity("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusActivity("Offline");
    }
}
