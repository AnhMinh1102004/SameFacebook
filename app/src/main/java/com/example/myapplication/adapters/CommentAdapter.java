package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Comment;
import com.example.myapplication.utilities.ImageUtil;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.setCommentData(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCommentProfile;
        TextView textCommentUsername, textCommentContent, textCommentTimestamp;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCommentProfile = itemView.findViewById(R.id.imageCommentProfile);
            textCommentUsername = itemView.findViewById(R.id.textCommentUsername);
            textCommentContent = itemView.findViewById(R.id.textCommentContent);
            textCommentTimestamp = itemView.findViewById(R.id.textCommentTimestamp);
        }

        void setCommentData(Comment comment) {
            textCommentUsername.setText(comment.getUserId()); // You might want to fetch the actual username
            textCommentContent.setText(comment.getContent());
            textCommentTimestamp.setText(comment.getTimestamp().toDate().toString());

            // TODO: Load user profile image
            // You would need to fetch the user's profile image from somewhere
            // For example, if it's stored in the Comment object:
            // Bitmap bitmap = ImageUtil.getBitmapFromEncodedString(comment.getUserProfileImage());
            // imageCommentProfile.setImageBitmap(bitmap);
        }
    }
}
