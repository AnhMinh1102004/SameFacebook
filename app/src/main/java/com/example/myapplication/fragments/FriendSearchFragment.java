package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.UserSearchAdapter;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendSearchFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private UserSearchAdapter userSearchAdapter;
    private List<User> users;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_search, container, false);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        preferenceManager = new PreferenceManager(requireContext());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>();
        userSearchAdapter = new UserSearchAdapter(users, this::onUserClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userSearchAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void searchUsers(String query) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.Key_COLLECTION_USER)
                .whereGreaterThanOrEqualTo(Constants.Key_NAME, query)
                .whereLessThanOrEqualTo(Constants.Key_NAME, query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        users.clear();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (queryDocumentSnapshot.getId().equals(preferenceManager.getString(Constants.Key_USER_ID))) {
                                continue;
                            }
                            User user = new User();
                            user.id = queryDocumentSnapshot.getId();
                            user.name = queryDocumentSnapshot.getString(Constants.Key_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.Key_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.Key_IMAGE);
                            users.add(user);
                        }
                        userSearchAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void onUserClicked(User user) {
        // Handle sending friend request
        // Update Firestore accordingly
    }
}
