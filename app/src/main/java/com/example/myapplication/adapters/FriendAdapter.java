package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemFriendBinding;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.ImageUtil;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<User> friends;
    private OnFriendClickListener listener;

    public FriendAdapter(List<User> friends, OnFriendClickListener listener) {
        this.friends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendBinding binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateFriends(List<User> newFriends) {
        this.friends = newFriends;
        notifyDataSetChanged();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        private final ItemFriendBinding binding;

        FriendViewHolder(ItemFriendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFriendClick(friends.get(position));
                }
            });
        }

        void bind(User friend) {
            binding.textName.setText(friend.name);
            if (friend.image != null) {
                Bitmap bitmap = ImageUtil.getBitmapFromEncodedString(friend.image);
                if (bitmap != null) {
                    binding.imageProfile.setImageBitmap(bitmap);
                } else {
                    binding.imageProfile.setImageResource(R.drawable.default_profile_image);
                }
            } else {
                binding.imageProfile.setImageResource(R.drawable.default_profile_image);
            }
        }
    }

    public interface OnFriendClickListener {
        void onFriendClick(User friend);
    }
}
