package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemFriendRequestBinding;
import com.example.myapplication.models.Friendship;
import com.example.myapplication.models.User;
import com.example.myapplication.utilities.ImageUtil;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private List<Friendship> friendRequests;
    private OnRequestActionListener listener;

    public FriendRequestAdapter(List<Friendship> friendRequests, OnRequestActionListener listener) {
        this.friendRequests = friendRequests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFriendRequestBinding binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FriendRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        holder.bind(friendRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public void updateFriendRequests(List<Friendship> newFriendRequests) {
        this.friendRequests = newFriendRequests;
        notifyDataSetChanged();
    }

    class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        private final ItemFriendRequestBinding binding;

        FriendRequestViewHolder(ItemFriendRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.buttonAccept.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRequestAction(friendRequests.get(position), true);
                }
            });

            binding.buttonReject.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRequestAction(friendRequests.get(position), false);
                }
            });
        }

        void bind(Friendship friendship) {
            User sender = friendship.getSender(); // Giả sử có phương thức getSender() trong Friendship
            binding.textName.setText(sender.getName());
            if (sender.getImage() != null) {
                Bitmap bitmap = ImageUtil.getBitmapFromEncodedString(sender.getImage());
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

    public interface OnRequestActionListener {
        void onRequestAction(Friendship request, boolean isAccepted);
    }
}
