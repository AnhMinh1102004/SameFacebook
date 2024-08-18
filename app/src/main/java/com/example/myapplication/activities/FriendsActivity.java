package com.example.myapplication.activities;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.databinding.ActivityFriendsBinding;
import com.example.myapplication.fragments.FriendRequestFragment;
import com.example.myapplication.fragments.FriendSearchFragment;
import com.example.myapplication.fragments.FriendListFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsActivity extends AppCompatActivity {

    private ActivityFriendsBinding binding;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FirebaseFirestore firestore;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFriendsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Friends");

        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;

        firestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setupViewPager();
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Bạn bè");
                            break;
                        case 1:
                            tab.setText("Lời mời");
                            break;
                        case 2:
                            tab.setText("Tìm kiếm");
                            break;
                    }
                }).attach();
    }

    private void setupViewPager() {
        FriendsPageAdapter adapter = new FriendsPageAdapter(this);
        viewPager.setAdapter(adapter);
    }

    private class FriendsPageAdapter extends FragmentStateAdapter {
        public FriendsPageAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new FriendListFragment();
                case 1:
                    return new FriendRequestFragment();
                case 2:
                    return new FriendSearchFragment();
                default:
                    return new FriendListFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
