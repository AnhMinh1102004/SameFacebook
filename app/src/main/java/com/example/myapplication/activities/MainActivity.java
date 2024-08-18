package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupViewPagerAndTabs();
        setupListeners();
    }

    private void setupViewPagerAndTabs() {
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new NewsFeedFragment(); // Bảng tin
                    case 1:
                        return new FriendsFragment(); // Kết bạn
                    case 2:
                        return new ProfileFragment(); // Hồ sơ cá nhân
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "Bảng tin";
                    case 1:
                        return "Bạn bè";
                    case 2:
                        return "Hồ sơ";
                    default:
                        return null;
                }
            }
        };

        binding.viewPager.setAdapter(fragmentPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private void setupListeners() {
        binding.fabCreatePost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePostActivity.class)));
        binding.fabAddFriend.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FriendsActivity.class)));
        binding.fabProfile.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ProfileActivity.class)));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
