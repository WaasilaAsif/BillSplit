package com.example.billsplit.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.Friend;
import com.example.billsplit.util.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    private final List<Friend> friends = new ArrayList<>();
    private final OnFriendClickListener listener;

    public FriendsAdapter(OnFriendClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Friend> newFriends) {
        friends.clear();
        friends.addAll(newFriends);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend_row, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(friends.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        private final TextView initial;
        private final TextView name;
        private final TextView balance;

        FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.friendInitial);
            name = itemView.findViewById(R.id.friendName);
            balance = itemView.findViewById(R.id.friendBalance);
        }

        void bind(Friend friend, OnFriendClickListener listener) {
            name.setText(friend.getUsername());
            initial.setText(friend.getUsername().isEmpty() ? ""
                    : friend.getUsername().substring(0, 1).toUpperCase(Locale.US));

            if (friend.isSettled()) {
                balance.setText(R.string.settled_up);
                balance.setTextColor(itemView.getResources().getColor(R.color.text_tertiary, null));
            } else if (friend.isOwedToYou()) {
                balance.setText(itemView.getResources()
                        .getString(R.string.owes_you_amount, Money.format(friend.getNetBalance())));
                balance.setTextColor(itemView.getResources().getColor(R.color.primary, null));
            } else {
                balance.setText(itemView.getResources()
                        .getString(R.string.you_owe_amount, Money.format(friend.getNetBalance())));
                balance.setTextColor(itemView.getResources().getColor(R.color.color_error, null));
            }

            itemView.setOnClickListener(v -> listener.onFriendClick(friend));
        }
    }
}
