package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.adapters.PostAdapter;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.models.Post;
import com.example.myapplication.utilities.Constants;
import com.example.myapplication.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private List<Post> posts;
    private PostAdapter postAdapter;
    private String encodedImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        if (binding.getRoot() == null) {
            Log.e("HomeFragment", "Fragment root view is null");
        } else {
            Log.d("HomeFragment", "Fragment root view inflated successfully");
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
        init();
        setListeners();
        listenPosts();
    }

    private void init() {
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        if (binding.recyclerViewPosts == null) {
            Log.e("HomeFragment", "recyclerViewPosts is null");
        } else {
            Log.d("HomeFragment", "Setting up RecyclerView");
            binding.recyclerViewPosts.setAdapter(postAdapter);
            binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.layoutCreatePost.buttonAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.layoutCreatePost.buttonPost.setOnClickListener(v -> {
            if (isValidPostDetails()) {
                postStatus();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void postStatus() {
        if (binding == null) {
            Log.e("HomeFragment", "Binding is null");
            return;
        }

        loading(true);
        HashMap<String, Object> post = new HashMap<>();
        post.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        post.put(Constants.KEY_POST_CONTENT, binding.layoutCreatePost.editTextPost.getText().toString());
        post.put(Constants.KEY_POST_TIMESTAMP, Timestamp.now());
        if (encodedImage != null) {
            post.put(Constants.KEY_POST_IMAGE, encodedImage);
        }
        database.collection(Constants.KEY_COLLECTION_POSTS)
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(() -> {
                        loading(false);
                        showToast("Posted Successfully");
                        Log.d("HomeFragment", "Post added successfully: " + documentReference.getId());

                        // Tạo một đối tượng Post mới và thêm vào danh sách
                        Post newPost = new Post();
                        newPost.setId(documentReference.getId());
                        newPost.setUserId(preferenceManager.getString(Constants.KEY_USER_ID));
                        newPost.setContent(binding.layoutCreatePost.editTextPost.getText().toString());
                        newPost.setImageUrl(encodedImage);
                        newPost.setTimestamp(Timestamp.now());
                        posts.add(0, newPost);
                        Log.d("HomeFragment", "New post added to list. List size: " + posts.size());
                        postAdapter.notifyItemInserted(0);
                        binding.recyclerViewPosts.smoothScrollToPosition(0);

                        // Reset form
                        binding.layoutCreatePost.editTextPost.setText("");
                        binding.layoutCreatePost.imageViewPreview.setVisibility(View.GONE);
                        encodedImage = null;
                    });
                })
                .addOnFailureListener(exception -> {
                    if (getActivity() == null) {
                        return;
                    }
                    getActivity().runOnUiThread(() -> {
                        loading(false);
                        showToast(exception.getMessage());
                        Log.e("HomeFragment", "Failed to add post", exception);
                    });
                });

    }


    private Boolean isValidPostDetails() {
        if (binding.layoutCreatePost.editTextPost.getText().toString().trim().isEmpty() && encodedImage == null) {
            showToast("Enter a status or add an image");
            return false;
        } else {
            return true;
        }
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.layoutCreatePost.imageViewPreview.setImageBitmap(bitmap);
                            binding.layoutCreatePost.imageViewPreview.setVisibility(View.VISIBLE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void listenPosts() {
        database.collection(Constants.KEY_COLLECTION_POSTS)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            Log.e("HomeFragment", "Firestore listening failed", error);
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    Post post = new Post();
                    post.setId(documentChange.getDocument().getId());
                    post.setUserId(documentChange.getDocument().getString(Constants.KEY_USER_ID));
                    post.setContent(documentChange.getDocument().getString(Constants.KEY_POST_CONTENT));
                    post.setImageUrl(documentChange.getDocument().getString(Constants.KEY_POST_IMAGE));
                    Object timestampObject = documentChange.getDocument().get(Constants.KEY_POST_TIMESTAMP);
                    if (timestampObject instanceof Timestamp) {
                        post.setTimestamp((Timestamp) timestampObject);
                    } else if (timestampObject instanceof Long) {
                        // Nếu timestamp được lưu dưới dạng số (milliseconds)
                        long milliseconds = (Long) timestampObject;
                        post.setTimestamp(new Timestamp(new Date(milliseconds)));
                    } else {
                        Log.e("HomeFragment", "Unexpected timestamp type: " + timestampObject.getClass().getName());
                    }
                    posts.add(0, post); // Thêm vào đầu danh sách
                    postAdapter.notifyItemInserted(0);
                }
            }
            // Không cần sắp xếp lại nếu bạn luôn thêm vào đầu danh sách
            // Collections.sort(posts, (obj1, obj2) -> obj2.getTimestamp().compareTo(obj1.getTimestamp()));
            // postAdapter.notifyDataSetChanged();
            binding.recyclerViewPosts.smoothScrollToPosition(0);
        }
    };


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.layoutCreatePost.buttonPost.setVisibility(View.INVISIBLE);
            binding.layoutCreatePost.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.layoutCreatePost.buttonPost.setVisibility(View.VISIBLE);
            binding.layoutCreatePost.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
