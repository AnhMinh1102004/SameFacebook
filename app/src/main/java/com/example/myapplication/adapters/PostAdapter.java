package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemPostBinding;
import com.example.myapplication.models.Post;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostBinding binding = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Log.d("PostAdapter", "Binding view holder for position: " + position);
        holder.setPostData(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ItemPostBinding binding;

        PostViewHolder(ItemPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setPostData(Post post) {
            if (post == null) {
                Log.e("PostAdapter", "Post is null");
                return;
            }

            Log.d("PostAdapter", "Setting post data: " + post.getId());
            binding.textUsername.setText(post.getUserId() != null ? post.getUserId() : "");
            binding.textTimestamp.setText(post.getTimestamp() != null ? post.getTimestamp().toDate().toString() : "");
            binding.textPostContent.setText(post.getContent() != null ? post.getContent() : "");
            binding.textLikeCount.setText(String.valueOf(post.getLikes()));

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                binding.imagePostContent.setVisibility(View.VISIBLE);
                new DecodeImageTask(binding.imagePostContent).execute(post.getImageUrl());
            } else {
                binding.imagePostContent.setVisibility(View.GONE);
            }

            if (post.getVideoUrl() != null && !post.getVideoUrl().isEmpty()) {
                binding.videoPostContent.setVisibility(View.VISIBLE);
                binding.videoPostContent.setVideoPath(post.getVideoUrl());
            } else {
                binding.videoPostContent.setVisibility(View.GONE);
            }

            // TODO: Load user profile image
            // TODO: Implement like and comment button click listeners
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

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    private class DecodeImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        DecodeImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return getBitmapFromEncodedString(strings[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
