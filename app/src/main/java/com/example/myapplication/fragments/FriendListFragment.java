package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.FriendAdapter;
import com.example.myapplication.models.User;
import com.example.myapplication.models.Friendship;
import com.example.myapplication.utilities.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment implements FriendAdapter.OnFriendClickListener {

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private List<User> friends;
    private FirebaseFirestore firestore;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        recyclerView = view.findViewById(R.id.friendsRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        friends = new ArrayList<>();
        adapter = new FriendAdapter(friends, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        loadFriends();
    }

    private void loadFriends() {
        firestore.collection(Constants.Key_COLLECTION_FRIENDSHIPS)
                .whereEqualTo("status", "accepted")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Friendship friendship = document.toObject(Friendship.class);
                        if (friendship.involvesUser(currentUserId)) {
                            loadUserDetails(friendship.getOtherUserId(currentUserId));
                        }
                    }
                });
    }

    private void loadUserDetails(String userId) {
        firestore.collection(Constants.Key_COLLECTION_USER).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        friends.add(user);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onFriendClick(User friend) {
        // Handle friend click
        // For example, open a chat with this friend or show their profile
    }
}
