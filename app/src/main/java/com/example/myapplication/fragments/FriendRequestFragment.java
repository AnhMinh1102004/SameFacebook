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
import com.example.myapplication.adapters.FriendRequestAdapter;
import com.example.myapplication.models.FriendRequest;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendRequestAdapter friendRequestAdapter;
    private List<FriendRequest> friendRequests;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        recyclerView = view.findViewById(R.id.friendRequestsRecyclerView);
        preferenceManager = new PreferenceManager(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friendRequests = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(friendRequests, this::onRequestAction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(friendRequestAdapter);
        getFriendRequests();
    }

    private void getFriendRequests() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_COLLECTION_USER)
                .document(preferenceManager.getString(Constants.Key_USER_ID))
                .collection(Constants.Key_COLLECTION_FRIEND_REQUESTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        friendRequests.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            FriendRequest request = new FriendRequest();
                            request.id = queryDocumentSnapshot.getId();
                            request.senderId = queryDocumentSnapshot.getString(Constants.Key_SENDER_ID);
                            request.senderName = queryDocumentSnapshot.getString(Constants.Key_SENDER_NAME);
                            request.senderImage = queryDocumentSnapshot.getString(Constants.Key_SENDER_IMAGE);
                            friendRequests.add(request);
                        }
                        friendRequestAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void onRequestAction(FriendRequest request, boolean isAccepted) {
        // Handle accept or reject friend request
        // Update Firestore accordingly
    }
}
