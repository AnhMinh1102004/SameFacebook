package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Post;
import com.example.myapplication.utilities.ImageUtil;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.setPostData(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile, imagePostContent;
        TextView textUsername, textTimestamp, textPostContent, textLikeCount;
        Button buttonLike, buttonComment;
        VideoView videoPostContent;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            imagePostContent = itemView.findViewById(R.id.imagePostContent);
            textUsername = itemView.findViewById(R.id.textUsername);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            textPostContent = itemView.findViewById(R.id.textPostContent);
            textLikeCount = itemView.findViewById(R.id.textLikeCount);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonComment = itemView.findViewById(R.id.buttonComment);
            videoPostContent = itemView.findViewById(R.id.videoPostContent);
        }

        void setPostData(Post post) {
            textUsername.setText(post.getUserId()); // You might want to fetch the actual username
            textTimestamp.setText(post.getTimestamp().toDate().toString());
            textPostContent.setText(post.getContent());
            textLikeCount.setText(String.valueOf(post.getLikes()));

            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                imagePostContent.setVisibility(View.VISIBLE);
                Bitmap bitmap = ImageUtil.getBitmapFromEncodedString(post.getImageUrl());
                imagePostContent.setImageBitmap(bitmap);
            } else {
                imagePostContent.setVisibility(View.GONE);
            }

            if (post.getVideoUrl() != null && !post.getVideoUrl().isEmpty()) {
                videoPostContent.setVisibility(View.VISIBLE);
                videoPostContent.setVideoPath(post.getVideoUrl());
            } else {
                videoPostContent.setVisibility(View.GONE);
            }

            // TODO: Load user profile image
            // TODO: Implement like and comment button click listeners
        }
    }
}
