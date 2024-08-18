package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.ImageUtil;

import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserSearchViewHolder> {

    private List<User> users;
    private OnUserActionListener listener;

    public UserSearchAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_search, parent, false);
        return new UserSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserSearchViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserSearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView textName, textEmail;
        Button buttonAddFriend;

        UserSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            buttonAddFriend = itemView.findViewById(R.id.buttonAddFriend);

            buttonAddFriend.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAddFriend(users.get(position));
                }
            });
        }

        void bind(User user) {
            textName.setText(user.name);
            textEmail.setText(user.email);
            if (user.image != null) {
                Bitmap bitmap = ImageUtil.getBitmapFromEncodedString(user.image);
                if (bitmap != null) {
                    imageProfile.setImageBitmap(bitmap);
                } else {
                    imageProfile.setImageResource(R.drawable.default_profile_image);
                }
            } else {
                imageProfile.setImageResource(R.drawable.default_profile_image);
            }
        }
    }

    public interface OnUserActionListener {
        void onAddFriend(User user);
    }
}
