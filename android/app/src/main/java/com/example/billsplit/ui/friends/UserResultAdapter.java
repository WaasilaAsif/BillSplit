package com.example.billsplit.ui.friends;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billsplit.R;
import com.example.billsplit.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Shared by Add Friend search results and Add Group Member's suggested list. */
public class UserResultAdapter extends RecyclerView.Adapter<UserResultAdapter.ViewHolder> {

    public interface OnAddClickListener {
        void onAdd(User user);
    }

    private final List<User> users = new ArrayList<>();
    private final OnAddClickListener listener;

    public UserResultAdapter(OnAddClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<User> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_result_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.name.setText(user.getUsername());
        holder.email.setText(user.getEmail());
        holder.initial.setText(user.getUsername().isEmpty() ? ""
                : user.getUsername().substring(0, 1).toUpperCase(Locale.US));
        holder.addButton.setOnClickListener(v -> listener.onAdd(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView initial;
        final TextView name;
        final TextView email;
        final TextView addButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.resultInitial);
            name = itemView.findViewById(R.id.resultName);
            email = itemView.findViewById(R.id.resultEmail);
            addButton = itemView.findViewById(R.id.resultAddButton);
        }
    }
}
